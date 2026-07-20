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

	private static final int DEFAULT_BASE_DIFFICULTY = 50;
	private static final double WEAKNESS_MULTIPLIER = 0.65;
	private static final double NO_WEAKNESS_MULTIPLIER = 1.0;

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
				simulation.baseDifficultyAverage,
				simulation.combatDifficulty,
				simulation.weaknessReduction,
				simulation.routeEfficiencyScore,
				simulation.timePenaltyMinutes
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

		int difficultWithoutWeaknessCount = 0;
		int rawBacktrackingPenalty = 0;
		int totalMinutes = 0;

		double totalBaseDifficulty = 0;
		double totalEffectiveDifficulty = 0;

		for (String stageSlug : stageOrder) {
			Stage stage = Objects.requireNonNull(
					stageBySlug.get(stageSlug),
					"Validated stage unexpectedly missing: " + stageSlug
			);

			int baseDifficulty = stage.getBaseDifficulty() != null
					? stage.getBaseDifficulty()
					: DEFAULT_BASE_DIFFICULTY;
			totalBaseDifficulty += baseDifficulty;
			boolean weaknessAvailable = hasBossWeakness(stage.getBoss(), acquiredWeapons);

			double multiplier = weaknessAvailable ? WEAKNESS_MULTIPLIER : NO_WEAKNESS_MULTIPLIER;
			totalEffectiveDifficulty += baseDifficulty * multiplier;

			int stageMinutes = stage.getEstimatedMinutes() == null ? 15 : stage.getEstimatedMinutes();
			totalMinutes += stageMinutes;

			if (!weaknessAvailable && baseDifficulty >= 60) {
				difficultWithoutWeaknessCount++;
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
		int difficultBossPenalty = difficultWithoutWeaknessCount * 10;
		int stageCount = stageOrder.size();

		int difficultyScore = stageCount == 0 ? 0 : clampScore((int) Math.round(totalEffectiveDifficulty / stageCount));
		int averageBaseDifficulty = stageCount == 0 ? 0 : (int) Math.round(totalBaseDifficulty / stageCount);
		int weaknessReduction = Math.max(0, averageBaseDifficulty - difficultyScore);

		int timePenaltyMinutes = (backtrackingScore / 4) + (difficultWithoutWeaknessCount * 3);

		int routeEfficiencyScore = clampScore(100 - (backtrackingScore * 0.65) - (difficultBossPenalty * 0.50) + weaknessReduction);

		return new SimulationResult(
				clampScore(difficultyScore),
				backtrackingScore,
				totalMinutes + timePenaltyMinutes,
				averageBaseDifficulty,
				difficultyScore,
				weaknessReduction,
				routeEfficiencyScore,
				timePenaltyMinutes,
				warnings
		);
	}

	private boolean hasBossWeakness(Boss boss, Set<String> acquiredWeapons) {
		if (boss == null || boss.getWeaknessWeapon() == null || boss.getWeaknessWeapon().isBlank()) {
			return false;
		}
		return acquiredWeapons.contains(boss.getWeaknessWeapon());
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
		return Math.clamp(value, 0, 100);
	}
	private int clampScore(double value) {
		return Math.clamp((int) Math.round(value), 0, 100);
	}

	private static class SimulationResult {
		private final int difficultyScore;
		private final int backtrackingScore;
		private final int estimatedMinutes;
		private final int baseDifficultyAverage;
		private final int combatDifficulty;
		private final int weaknessReduction;
		private final int routeEfficiencyScore;
		private final int timePenaltyMinutes;
		private final List<RouteWarningResponse> warnings;

		private SimulationResult(
				int difficultyScore,
				int backtrackingScore,
				int estimatedMinutes,
				int baseDifficultyAverage,
				int combatDifficulty,
				int weaknessReduction,
				int routeEfficiencyScore,
				int timePenaltyMinutes,
				List<RouteWarningResponse> warnings
		) {
			this.difficultyScore = difficultyScore;
			this.backtrackingScore = backtrackingScore;
			this.estimatedMinutes = estimatedMinutes;
			this.baseDifficultyAverage = baseDifficultyAverage;
			this.combatDifficulty = combatDifficulty;
			this.weaknessReduction = weaknessReduction;
			this.routeEfficiencyScore = routeEfficiencyScore;
			this.timePenaltyMinutes = timePenaltyMinutes;
			this.warnings = warnings;
		}
	}
}
