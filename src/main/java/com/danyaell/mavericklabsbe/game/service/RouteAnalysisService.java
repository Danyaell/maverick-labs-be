package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.*;
import com.danyaell.mavericklabsbe.game.entity.*;
import com.danyaell.mavericklabsbe.game.exception.InvalidRouteException;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.repository.CollectibleRepository;
import com.danyaell.mavericklabsbe.game.repository.GameRepository;
import com.danyaell.mavericklabsbe.game.repository.StageRepository;
import com.danyaell.mavericklabsbe.game.repository.WeaponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteAnalysisService {

    private static final int MAX_SCORE = 100;
    private static final int BASE_DIFFICULTY_CAP = 10;
    private static final int WEAKNESS_OPTIMIZATION_BONUS = 8;
    private static final int HARD_BOSS_WITHOUT_WEAKNESS_PENALTY = 10;
    private static final int HARD_BOSS_BASE_DIFFICULTY_THRESHOLD = 7;
    private static final int BACKTRACK_TIME_PENALTY_PER_WARNING = 5;
    private static final int HARD_BOSS_TIME_PENALTY = 3;

    private final CollectibleRepository collectibleRepository;
    private final GameRepository gameRepository;
    private final StageRepository stageRepository;
    private final WeaponRepository weaponRepository;

    @Transactional(readOnly = true)
    public RouteAnalysisResponse analyzeRoute(AnalyzeRouteRequest request) {
        Game game = gameRepository.findByCodeIgnoreCase(request.gameCode())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + request.gameCode()));

        List<Stage> stages = stageRepository.findByGameIdWithBossAndCollectibles(game.getId());
        preloadCollectibleRequirements(stages);
        List<Weapon> weapons = weaponRepository.findByGameId(game.getId());

        List<Stage> sortedStages = sortStages(stages);
        validateRouteRequest(request, game.getCode(), sortedStages);

        Map<String, Stage> stageBySlug = sortedStages.stream()
                .collect(Collectors.toMap(
                        stage -> normalize(stage.getSlug()),
                        stage -> stage
                ));

        Map<Long, String> weaponRewardByStageId = weapons.stream()
                .filter(weapon -> weapon.getObtainedFromStage() != null && hasText(weapon.getSlug()))
                .collect(Collectors.toMap(
                        weapon -> weapon.getObtainedFromStage().getId(),
                        weapon -> normalize(weapon.getSlug()),
                        (first, ignored) -> first
                ));

        Set<String> acquiredWeapons = new HashSet<>();
        Set<String> acquiredCollectibles = new HashSet<>();
        Set<String> visitedStages = new HashSet<>();
        List<RouteWarningResponse> warnings = new ArrayList<>();

        int baseDifficultyTotal = 0;
        int bossDifficulty = 0;
        int weaknessOptimization = 0;
        int stageMinutes = 0;
        int blockedCollectibles = 0;
        int totalCollectibles = 0;
        int hardBossWarnings = 0;

        for (String stageSlug : request.stageOrder()) {
            Stage currentStage = stageBySlug.get(normalize(stageSlug));
            visitedStages.add(normalize(currentStage.getSlug()));
            baseDifficultyTotal += normalizeToNonNegative(currentStage.getBaseDifficulty());
            stageMinutes += normalizeToNonNegative(currentStage.getEstimatedMinutes());

            List<Collectible> collectibles = sortCollectibles(currentStage.getCollectibles());
            totalCollectibles += collectibles.size();
            for (Collectible collectible : collectibles) {
                Optional<CollectibleRequirement> missingRequirement = collectible.getRequirements().stream()
                        .filter(requirement -> !isRequirementSatisfied(requirement, acquiredWeapons, acquiredCollectibles, visitedStages))
                        .findFirst();

                if (missingRequirement.isPresent()) {
                    blockedCollectibles++;
                    warnings.add(new RouteWarningResponse(
                            RouteWarningType.MISSING_REQUIREMENT,
                            currentStage.getSlug(),
                            collectible.getSlug(),
                            "Collectible '" + collectible.getSlug() + "' is missing requirement '" +
                                    missingRequirement.get().getRequiredKey() + "'"
                    ));
                    continue;
                }

                acquiredCollectibles.add(normalize(collectible.getSlug()));
            }

            Boss boss = currentStage.getBoss();
            if (boss != null && hasText(boss.getWeaknessWeapon())) {
                String weaknessWeapon = normalize(boss.getWeaknessWeapon());
                if (acquiredWeapons.contains(weaknessWeapon)) {
                    weaknessOptimization += WEAKNESS_OPTIMIZATION_BONUS;
                } else if (normalizeToNonNegative(currentStage.getBaseDifficulty()) >= HARD_BOSS_BASE_DIFFICULTY_THRESHOLD) {
                    bossDifficulty += HARD_BOSS_WITHOUT_WEAKNESS_PENALTY;
                    hardBossWarnings++;
                }
            }

            String rewardedWeapon = weaponRewardByStageId.get(currentStage.getId());
            if (rewardedWeapon != null) {
                acquiredWeapons.add(rewardedWeapon);
            }
        }

        int backtrackingScore = totalCollectibles == 0
                ? 0
                : clampScore(Math.round((blockedCollectibles * 100f) / totalCollectibles));

        int timePenalty = (blockedCollectibles * BACKTRACK_TIME_PENALTY_PER_WARNING) + (hardBossWarnings * HARD_BOSS_TIME_PENALTY);
        int estimatedMinutes = stageMinutes + timePenalty;
        int normalizedBaseDifficulty = normalizeBaseDifficulty(baseDifficultyTotal, request.stageOrder().size());
        int difficultyScore = clampScore(normalizedBaseDifficulty + backtrackingScore + bossDifficulty + timePenalty - weaknessOptimization);

        return new RouteAnalysisResponse(
                game.getCode(),
                difficultyScore,
                calculateDifficultyLabel(difficultyScore),
                backtrackingScore,
                estimatedMinutes,
                warnings,
                new RouteBreakdownResponse(
                        normalizedBaseDifficulty,
                        bossDifficulty,
                        weaknessOptimization,
                        timePenalty
                )
        );
    }

    private void preloadCollectibleRequirements(List<Stage> stages) {
        List<Long> stageIds = stages.stream()
                .map(Stage::getId)
                .filter(Objects::nonNull)
                .toList();

        if (stageIds.isEmpty()) {
            return;
        }

        collectibleRepository.findByStageIdInWithRequirements(stageIds);
    }

    private void validateRouteRequest(AnalyzeRouteRequest request, String gameCode, List<Stage> gameStages) {
        if (request.goal() != RouteGoal.HUNDRED_PERCENT) {
            throw new InvalidRouteException("Unsupported goal: " + request.goal());
        }

        Set<String> routeStages = new HashSet<>();
        for (String stageSlug : request.stageOrder()) {
            String normalizedSlug = normalize(stageSlug);
            if (!routeStages.add(normalizedSlug)) {
                throw new InvalidRouteException("Duplicated stage in route: " + stageSlug);
            }
        }

        Set<String> gameStageSlugs = gameStages.stream()
                .map(Stage::getSlug)
                .filter(this::hasText)
                .map(this::normalize)
                .collect(Collectors.toSet());

        for (String routeStageSlug : routeStages) {
            if (!gameStageSlugs.contains(routeStageSlug)) {
                throw new InvalidRouteException("Stage does not belong to game " + gameCode + ": " + routeStageSlug);
            }
        }

        if (request.goal() == RouteGoal.HUNDRED_PERCENT && routeStages.size() != gameStageSlugs.size()) {
            throw new InvalidRouteException("HUNDRED_PERCENT goal requires all stages from the game");
        }
    }

    private boolean isRequirementSatisfied(
            CollectibleRequirement requirement,
            Set<String> acquiredWeapons,
            Set<String> acquiredCollectibles,
            Set<String> visitedStages
    ) {
        if (requirement == null || !hasText(requirement.getRequiredKey()) || requirement.getRequirementType() == null) {
            return false;
        }

        String requiredKey = normalize(requirement.getRequiredKey());
        return switch (requirement.getRequirementType()) {
            case WEAPON -> acquiredWeapons.contains(requiredKey);
            case COLLECTIBLE -> acquiredCollectibles.contains(requiredKey);
            case STAGE_CLEARED -> visitedStages.contains(requiredKey);
            case OTHER -> false;
        };
    }

    private int normalizeBaseDifficulty(int difficultyTotal, int stageCount) {
        if (stageCount <= 0) {
            return 0;
        }
        float maxPossible = stageCount * (float) BASE_DIFFICULTY_CAP;
        return clampScore(Math.round((difficultyTotal * MAX_SCORE) / maxPossible));
    }

    private DifficultyLabel calculateDifficultyLabel(int difficultyScore) {
        if (difficultyScore <= 39) {
            return DifficultyLabel.EASY;
        }
        if (difficultyScore <= 69) {
            return DifficultyLabel.MEDIUM;
        }
        return DifficultyLabel.HARD;
    }

    private int clampScore(int value) {
        if (value < 0) {
            return 0;
        }
        return Math.min(value, MAX_SCORE);
    }

    private int normalizeToNonNegative(Integer value) {
        if (value == null) {
            return 0;
        }
        return Math.max(value, 0);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private List<Stage> sortStages(List<Stage> stages) {
        if (stages == null) {
            return List.of();
        }
        return stages.stream()
                .sorted(Comparator.comparing(stage -> normalizeToNonNegative(stage.getStageOrder())))
                .toList();
    }

    private List<Collectible> sortCollectibles(List<Collectible> collectibles) {
        if (collectibles == null) {
            return List.of();
        }
        return collectibles.stream()
                .sorted(Comparator.comparing(collectible -> normalizeToNonNegative(collectible.getSortOrder())))
                .toList();
    }
}
