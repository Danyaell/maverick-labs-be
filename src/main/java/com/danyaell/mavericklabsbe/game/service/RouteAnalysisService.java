package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.route.*;
import com.danyaell.mavericklabsbe.game.entity.*;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.repository.CollectibleRepository;
import com.danyaell.mavericklabsbe.game.repository.GameRepository;
import com.danyaell.mavericklabsbe.game.repository.StageRepository;
import com.danyaell.mavericklabsbe.game.repository.WeaponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteAnalysisService {

	private final GameRepository gameRepository;
	private final StageRepository stageRepository;
	private final WeaponRepository weaponRepository;
	private final CollectibleRepository collectibleRepository;
	private final RecommendationService recommendationService;

	public RouteAnalysisResponse analyzeRoute(AnalyzeRouteRequest request) {
		Game game = gameRepository.findByCodeIgnoreCase(request.gameCode().trim())
				.orElseThrow(() -> new ResourceNotFoundException("Game not found: " + request.gameCode()));

		List<Stage> stages = stageRepository.findByGameIdWithAnalysisData(game.getId());
		preloadCollectibleRequirements(stages);
		Map<String, Stage> stageBySlug = stages.stream()
				.collect(Collectors.toMap(Stage::getSlug, stage -> stage));

		validateRoute(request, stageBySlug, stages);

		List<Weapon> weapons = weaponRepository.findByGameId(game.getId());
		Map<String, Weapon> weaponsByStageSlug = weapons.stream()
				.filter(weapon -> weapon.getObtainedFromStage() != null)
				.collect(Collectors.toMap(
						weapon -> weapon.getObtainedFromStage().getSlug(),
						weapon -> weapon,
						(left, right) -> left
				));

		SimulationResult simulation = simulate(request.stageOrder(), stageBySlug, weaponsByStageSlug);
		RouteBreakdownResponse breakdown = new RouteBreakdownResponse(
				simulation.bossDifficulty,
				simulation.weaknessOptimization,
				simulation.backtrackingPenalty,
				simulation.timePenalty
		);

		RouteAnalysisContext context = new RouteAnalysisContext(
				game,
				request.stageOrder().stream().map(stageBySlug::get).toList(),
				weaponsByStageSlug,
				simulation.warnings,
				simulation.backtrackingScore
		);
		List<RouteRecommendationResponse> recommendations = recommendationService.generateRecommendations(context);

		return new RouteAnalysisResponse(
				game.getCode(),
				simulation.difficultyScore,
				toDifficultyLabel(simulation.difficultyScore),
				simulation.backtrackingScore,
				simulation.estimatedMinutes,
				simulation.warnings,
				breakdown,
				recommendations
		);
	}

	private void validateRoute(AnalyzeRouteRequest request, Map<String, Stage> stageBySlug, List<Stage> gameStages) {
		Set<String> unique = new HashSet<>();
		for (String stageSlug : request.stageOrder()) {
			if (!unique.add(stageSlug)) {
				throw new IllegalArgumentException("stageOrder contains duplicate stage: " + stageSlug);
			}
			if (!stageBySlug.containsKey(stageSlug)) {
				throw new IllegalArgumentException("Unknown stage for game " + request.gameCode() + ": " + stageSlug);
			}
		}

		if (request.goal() == RouteGoal.HUNDRED_PERCENT) {
			Set<String> gameStageSlugs = gameStages.stream().map(Stage::getSlug).collect(Collectors.toSet());
			if (request.stageOrder().size() != gameStageSlugs.size() || !gameStageSlugs.equals(unique)) {
				throw new IllegalArgumentException("HUNDRED_PERCENT requires a complete route including all game stages");
			}
		}
	}

	private void preloadCollectibleRequirements(List<Stage> stages) {
		if (stages.isEmpty()) {
			return;
		}

		List<Long> stageIds = stages.stream()
				.map(Stage::getId)
				.filter(Objects::nonNull)
				.toList();

		if (stageIds.isEmpty()) {
			return;
		}

		Map<Long, List<Collectible>> collectiblesByStageId = collectibleRepository.findByStageIdInWithRequirements(stageIds)
				.stream()
				.collect(Collectors.groupingBy(
						collectible -> collectible.getStage().getId(),
						LinkedHashMap::new,
						Collectors.toList()
				));

		for (Stage stage : stages) {
			List<Collectible> collectibles = collectiblesByStageId.getOrDefault(stage.getId(), List.of());
			stage.setCollectibles(new ArrayList<>(collectibles));
		}
	}

	private SimulationResult simulate(
			List<String> stageOrder,
			Map<String, Stage> stageBySlug,
			Map<String, Weapon> weaponsByStageSlug
	) {
		Set<String> acquiredWeapons = new HashSet<>();
		Set<String> acquiredCollectibles = new HashSet<>();
		Set<String> visitedStages = new HashSet<>();
		List<RouteWarningResponse> warnings = new ArrayList<>();

		int bossDifficulty = 0;
		int weaknessOptimization = 0;
		int difficultWithoutWeaknessCount = 0;
		int rawBacktrackingPenalty = 0;
		int totalMinutes = 0;

		for (String stageSlug : stageOrder) {
			Stage stage = stageBySlug.get(stageSlug);
			if (stage == null) {
				continue;
			}

			int baseDifficulty = stage.getBaseDifficulty() == null ? 50 : stage.getBaseDifficulty();
			int stageMinutes = stage.getEstimatedMinutes() == null ? 15 : stage.getEstimatedMinutes();
			totalMinutes += stageMinutes;
			bossDifficulty += baseDifficulty;

			Boss boss = stage.getBoss();
			if (boss != null && boss.getWeaknessWeapon() != null && !boss.getWeaknessWeapon().isBlank()) {
				if (acquiredWeapons.contains(boss.getWeaknessWeapon())) {
					weaknessOptimization += 20;
				} else if (baseDifficulty >= 60) {
					difficultWithoutWeaknessCount++;
				}
			}

			for (Collectible collectible : stage.getCollectibles()) {
				boolean blocked = false;
				for (CollectibleRequirement requirement : collectible.getRequirements()) {
					if (!isRequirementMet(requirement, acquiredWeapons, acquiredCollectibles, visitedStages)) {
						blocked = true;
						break;
					}
				}

				if (blocked) {
					rawBacktrackingPenalty += 20;
					warnings.add(new RouteWarningResponse(
							RouteWarningType.MISSING_REQUIREMENT,
							"Collectible %s may require revisiting %s later.".formatted(collectible.getName(), stage.getName()),
							stage.getSlug(),
							collectible.getSlug()
					));
					continue;
				}

				acquiredCollectibles.add(collectible.getSlug());
			}

			visitedStages.add(stage.getSlug());
			Weapon obtainedWeapon = weaponsByStageSlug.get(stageSlug);
			if (obtainedWeapon != null) {
				acquiredWeapons.add(obtainedWeapon.getSlug());
			}
		}

		int backtrackingScore = Math.min(100, rawBacktrackingPenalty);
		int backtrackingPenalty = backtrackingScore;
		int difficultBossPenalty = difficultWithoutWeaknessCount * 10;
		int difficultyScore = clampScore(bossDifficulty - weaknessOptimization + difficultBossPenalty + (backtrackingScore / 2));
		int timePenalty = (backtrackingScore / 4) + (difficultWithoutWeaknessCount * 3);

		return new SimulationResult(
				clampScore(difficultyScore),
				backtrackingScore,
				totalMinutes + timePenalty,
				bossDifficulty,
				weaknessOptimization,
				backtrackingPenalty,
				timePenalty,
				warnings
		);
	}

	private boolean isRequirementMet(
			CollectibleRequirement requirement,
			Set<String> acquiredWeapons,
			Set<String> acquiredCollectibles,
			Set<String> visitedStages
	) {
		return switch (requirement.getRequirementType()) {
			case WEAPON -> acquiredWeapons.contains(requirement.getRequiredKey());
			case COLLECTIBLE -> acquiredCollectibles.contains(requirement.getRequiredKey());
			case STAGE_CLEARED -> visitedStages.contains(requirement.getRequiredKey());
			case OTHER -> false;
		};
	}

	private DifficultyLabel toDifficultyLabel(int difficultyScore) {
		if (difficultyScore <= 39) {
			return DifficultyLabel.EASY;
		}
		if (difficultyScore <= 69) {
			return DifficultyLabel.MEDIUM;
		}
		return DifficultyLabel.HARD;
	}

	private int clampScore(int value) {
		return Math.max(0, Math.min(100, value));
	}

	private static class SimulationResult {
		private final int difficultyScore;
		private final int backtrackingScore;
		private final int estimatedMinutes;
		private final int bossDifficulty;
		private final int weaknessOptimization;
		private final int backtrackingPenalty;
		private final int timePenalty;
		private final List<RouteWarningResponse> warnings;

		private SimulationResult(
				int difficultyScore,
				int backtrackingScore,
				int estimatedMinutes,
				int bossDifficulty,
				int weaknessOptimization,
				int backtrackingPenalty,
				int timePenalty,
				List<RouteWarningResponse> warnings
		) {
			this.difficultyScore = difficultyScore;
			this.backtrackingScore = backtrackingScore;
			this.estimatedMinutes = estimatedMinutes;
			this.bossDifficulty = bossDifficulty;
			this.weaknessOptimization = weaknessOptimization;
			this.backtrackingPenalty = backtrackingPenalty;
			this.timePenalty = timePenalty;
			this.warnings = warnings;
		}
	}
}
