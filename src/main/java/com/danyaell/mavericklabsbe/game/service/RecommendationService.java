package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.route.*;
import com.danyaell.mavericklabsbe.game.entity.Boss;
import com.danyaell.mavericklabsbe.game.entity.Stage;
import com.danyaell.mavericklabsbe.game.entity.Weapon;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

	private static final int MAX_RECOMMENDATIONS = 20;
	private static final int MAX_POSITIVE_BOSS_ORDER = 2;

	public List<RouteRecommendationResponse> generateRecommendations(RouteAnalysisContext context) {
		List<RouteRecommendationResponse> collected = new ArrayList<>();
		collected.addAll(buildBacktrackingRecommendations(context));
		collected.addAll(buildBossOrderRecommendations(context));
		addRouteEfficiencyRecommendationIfNeeded(collected, context);
		return prioritizeAndTrim(collected);
	}

	private List<RouteRecommendationResponse> buildBacktrackingRecommendations(RouteAnalysisContext context) {
		Set<String> stageSeen = new HashSet<>();
		List<RouteRecommendationResponse> recommendations = new ArrayList<>();

		for (RouteWarningResponse warning : context.warnings()) {
			if (warning.type() != RouteWarningType.MISSING_REQUIREMENT || !stageSeen.add(warning.stageSlug())) {
				continue;
			}

			String message = "You may need to revisit %s to collect all items."
					.formatted(humanizeStageSlug(warning.stageSlug()));

			recommendations.add(new RouteRecommendationResponse(
					RecommendationType.BACKTRACKING,
					RecommendationSeverity.WARNING,
					message,
					List.of(warning.stageSlug())
			));
		}

		return recommendations;
	}

	private List<RouteRecommendationResponse> buildBossOrderRecommendations(RouteAnalysisContext context) {
		List<Stage> orderedStages = context.orderedStages();
		Map<String, Integer> indexByStageSlug = new HashMap<>();
		for (int i = 0; i < orderedStages.size(); i++) {
			indexByStageSlug.put(orderedStages.get(i).getSlug(), i);
		}

		Map<String, Weapon> weaponByProvider = context.weaponsByObtainedStageSlug();
		Map<String, String> providerByWeaponSlug = weaponByProvider.values().stream()
				.filter(weapon -> weapon.getObtainedFromStage() != null)
				.collect(Collectors.toMap(
						Weapon::getSlug,
						weapon -> weapon.getObtainedFromStage().getSlug(),
						(left, right) -> left
				));

		List<RouteRecommendationResponse> recommendations = new ArrayList<>();
		int positiveCount = 0;

		for (Stage targetStage : orderedStages) {
			Boss targetBoss = targetStage.getBoss();
			if (targetBoss == null || targetBoss.getWeaknessWeapon() == null || targetBoss.getWeaknessWeapon().isBlank()) {
				continue;
			}

			String providerStageSlug = providerByWeaponSlug.get(targetBoss.getWeaknessWeapon());
			if (providerStageSlug == null || providerStageSlug.equals(targetStage.getSlug())) {
				continue;
			}

			Integer providerIndex = indexByStageSlug.get(providerStageSlug);
			Integer targetIndex = indexByStageSlug.get(targetStage.getSlug());
			if (providerIndex == null || targetIndex == null) {
				continue;
			}

			Stage providerStage = orderedStages.get(providerIndex);
			String providerName = providerStage.getBoss() != null ? providerStage.getBoss().getName() : providerStage.getName();
			String targetName = targetBoss.getName();
			Weapon providerWeapon = weaponByProvider.get(providerStageSlug);
			String weaponName = providerWeapon != null ? providerWeapon.getName() : targetBoss.getWeaknessWeapon();

			if (providerIndex > targetIndex) {
				String message = "Move %s before %s to reduce difficulty because %s gives you %s."
						.formatted(providerName, targetName, providerName, weaponName);
				recommendations.add(new RouteRecommendationResponse(
						RecommendationType.BOSS_ORDER,
						RecommendationSeverity.WARNING,
						message,
						List.of(providerStageSlug, targetStage.getSlug())
				));
				continue;
			}

			if (positiveCount < MAX_POSITIVE_BOSS_ORDER) {
				String message = "Good choice: %s before %s reduces difficulty because you get %s."
						.formatted(providerName, targetName, weaponName);
				recommendations.add(new RouteRecommendationResponse(
						RecommendationType.BOSS_ORDER,
						RecommendationSeverity.INFO,
						message,
						List.of(providerStageSlug, targetStage.getSlug())
				));
				positiveCount++;
			}
		}

		return recommendations;
	}

	private void addRouteEfficiencyRecommendationIfNeeded(List<RouteRecommendationResponse> collected, RouteAnalysisContext context) {
		boolean hasEnoughSpecificRecommendations = collected.stream()
				.filter(recommendation -> recommendation.type() != RecommendationType.ROUTE_EFFICIENCY)
				.count() >= 3;

		if (context.backtrackingScore() < 40 || hasEnoughSpecificRecommendations) {
			return;
		}

		collected.add(new RouteRecommendationResponse(
				RecommendationType.ROUTE_EFFICIENCY,
				RecommendationSeverity.WARNING,
				"This route has notable backtracking. Consider moving upgrade-heavy stages earlier.",
				List.of()
		));
	}

	private List<RouteRecommendationResponse> prioritizeAndTrim(List<RouteRecommendationResponse> recommendations) {
		return recommendations.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.collectingAndThen(
						Collectors.toMap(
								this::dedupeKey,
								recommendation -> recommendation,
								(existing, ignored) -> existing,
								LinkedHashMap::new
						),
						map -> map.values().stream()
								.sorted(this::compareRecommendations)
								.limit(MAX_RECOMMENDATIONS)
								.toList()
				));
	}

	private String dedupeKey(RouteRecommendationResponse recommendation) {
		String stages = String.join("|", recommendation.relatedStages());
		return recommendation.type() + "|" + recommendation.severity() + "|" + stages;
	}

	private int compareRecommendations(RouteRecommendationResponse left, RouteRecommendationResponse right) {
		int orderCompare = Integer.compare(priorityOrder(left), priorityOrder(right));
		if (orderCompare != 0) {
			return orderCompare;
		}
		return left.message().compareToIgnoreCase(right.message());
	}

	private int priorityOrder(RouteRecommendationResponse recommendation) {
		if (recommendation.type() == RecommendationType.BACKTRACKING && recommendation.severity() == RecommendationSeverity.WARNING) {
			return 0;
		}
		if (recommendation.type() == RecommendationType.BOSS_ORDER && recommendation.severity() == RecommendationSeverity.WARNING) {
			return 1;
		}
		if (recommendation.type() == RecommendationType.ROUTE_EFFICIENCY && recommendation.severity() == RecommendationSeverity.WARNING) {
			return 2;
		}
		if (recommendation.type() == RecommendationType.BOSS_ORDER && recommendation.severity() == RecommendationSeverity.INFO) {
			return 3;
		}
		if (recommendation.severity() == RecommendationSeverity.WARNING) {
			return 4;
		}
		if (recommendation.severity() == RecommendationSeverity.INFO) {
			return 5;
		}
		return 6;
	}

	private String humanizeStageSlug(String slug) {
		return Arrays.stream(slug.split("-"))
				.map(this::capitalize)
				.collect(Collectors.joining(" "));
	}

	private String capitalize(String value) {
		if (value.isBlank()) {
			return value;
		}
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}
}
