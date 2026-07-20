package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.route.*;
import com.danyaell.mavericklabsbe.game.entity.Boss;
import com.danyaell.mavericklabsbe.game.entity.Game;
import com.danyaell.mavericklabsbe.game.entity.Stage;
import com.danyaell.mavericklabsbe.game.entity.Weapon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecommendationService Unit Tests")
class RecommendationServiceTests {

	private final RecommendationService recommendationService = new RecommendationService();

	@Test
	@DisplayName("should_GenerateBossOrderWarning_When_ProviderAppearsAfterTarget")
	void should_GenerateBossOrderWarning_When_ProviderAppearsAfterTarget() {
		Stage sparkMandrill = stage("spark-mandrill", "Spark Mandrill", "shotgun-ice");
		Stage chillPenguin = stage("chill-penguin", "Chill Penguin", "flame-wave");

		RouteAnalysisContext context = context(
				List.of(sparkMandrill, chillPenguin),
				List.of(),
				Map.of("chill-penguin", weapon("shotgun-ice", "Shotgun Ice", chillPenguin)),
				10
		);

		List<RouteRecommendationResponse> recommendations = recommendationService.generateRecommendations(context);

		assertThat(recommendations)
				.filteredOn(recommendation -> recommendation.type() == RecommendationType.BOSS_ORDER)
				.first()
				.extracting(RouteRecommendationResponse::severity, RouteRecommendationResponse::relatedStages)
				.containsExactly(RecommendationSeverity.WARNING, List.of("chill-penguin", "spark-mandrill"));
	}

	@Test
	@DisplayName("should_GenerateBossOrderInfo_When_ProviderAppearsBeforeTarget")
	void should_GenerateBossOrderInfo_When_ProviderAppearsBeforeTarget() {
		Stage chillPenguin = stage("chill-penguin", "Chill Penguin", "flame-wave");
		Stage sparkMandrill = stage("spark-mandrill", "Spark Mandrill", "shotgun-ice");

		RouteAnalysisContext context = context(
				List.of(chillPenguin, sparkMandrill),
				List.of(),
				Map.of("chill-penguin", weapon("shotgun-ice", "Shotgun Ice", chillPenguin)),
				10
		);

		List<RouteRecommendationResponse> recommendations = recommendationService.generateRecommendations(context);

		assertThat(recommendations)
				.filteredOn(recommendation -> recommendation.type() == RecommendationType.BOSS_ORDER)
				.first()
				.extracting(RouteRecommendationResponse::severity, RouteRecommendationResponse::relatedStages)
				.containsExactly(RecommendationSeverity.INFO, List.of("chill-penguin", "spark-mandrill"));
	}

	@Test
	@DisplayName("should_GenerateBacktrackingRecommendation_FromMissingRequirementWarning")
	void should_GenerateBacktrackingRecommendation_FromMissingRequirementWarning() {
		RouteWarningResponse warning = new RouteWarningResponse(
				RouteWarningType.MISSING_REQUIREMENT,
				"blocked",
				"flame-mammoth",
				"flame-mammoth-heart-tank"
		);

		RouteAnalysisContext context = context(List.of(stage("flame-mammoth", "Flame Mammoth", null)), List.of(warning), Map.of(), 45);
		List<RouteRecommendationResponse> recommendations = recommendationService.generateRecommendations(context);

		assertThat(recommendations)
				.filteredOn(recommendation -> recommendation.type() == RecommendationType.BACKTRACKING)
				.first()
				.extracting(RouteRecommendationResponse::severity, RouteRecommendationResponse::relatedStages)
				.containsExactly(RecommendationSeverity.WARNING, List.of("flame-mammoth"));
	}

	@Test
	@DisplayName("should_NotGenerateDuplicates_When_EquivalentInputsExist")
	void should_NotGenerateDuplicates_When_EquivalentInputsExist() {
		RouteWarningResponse first = new RouteWarningResponse(RouteWarningType.MISSING_REQUIREMENT, "blocked", "flame-mammoth", "one");
		RouteWarningResponse second = new RouteWarningResponse(RouteWarningType.MISSING_REQUIREMENT, "blocked again", "flame-mammoth", "two");

		RouteAnalysisContext context = context(List.of(stage("flame-mammoth", "Flame Mammoth", null)), List.of(first, second), Map.of(), 45);
		List<RouteRecommendationResponse> recommendations = recommendationService.generateRecommendations(context);

		assertThat(recommendations)
				.filteredOn(recommendation -> recommendation.type() == RecommendationType.BACKTRACKING)
				.hasSize(1);
	}

	@Test
	@DisplayName("should_LimitRecommendations_ToMaximumCount")
	void should_LimitRecommendations_ToMaximumCount() {
		List<Stage> orderedStages = List.of(
				stage("a", "A", "w2"),
				stage("b", "B", "w3"),
				stage("c", "C", "w4"),
				stage("d", "D", "w5"),
				stage("e", "E", "w6"),
				stage("f", "F", "w7"),
				stage("g", "G", "w8"),
				stage("h", "H", "w9"),
				stage("i", "I", "w10")
		);
		Map<String, Weapon> weapons = Map.of(
				"i", weapon("w2", "W2", orderedStages.get(8)),
				"h", weapon("w3", "W3", orderedStages.get(7)),
				"g", weapon("w4", "W4", orderedStages.get(6)),
				"f", weapon("w5", "W5", orderedStages.get(5)),
				"e", weapon("w6", "W6", orderedStages.get(4)),
				"d", weapon("w7", "W7", orderedStages.get(3)),
				"c", weapon("w8", "W8", orderedStages.get(2)),
				"b", weapon("w9", "W9", orderedStages.get(1)),
				"a", weapon("w10", "W10", orderedStages.get(0))
		);

		List<RouteRecommendationResponse> recommendations = recommendationService.generateRecommendations(context(orderedStages, List.of(), weapons, 60));
		assertThat(recommendations).hasSizeLessThanOrEqualTo(15);
	}

	@Test
	@DisplayName("should_PrioritizeWarnings_BeforeInfos")
	void should_PrioritizeWarnings_BeforeInfos() {
		Stage sparkMandrill = stage("spark-mandrill", "Spark Mandrill", "shotgun-ice");
		Stage chillPenguin = stage("chill-penguin", "Chill Penguin", "flame-wave");
		RouteWarningResponse warning = new RouteWarningResponse(RouteWarningType.MISSING_REQUIREMENT, "blocked", "spark-mandrill", "x");

		RouteAnalysisContext context = context(
				List.of(sparkMandrill, chillPenguin),
				List.of(warning),
				Map.of("chill-penguin", weapon("shotgun-ice", "Shotgun Ice", chillPenguin)),
				45
		);

		List<RouteRecommendationResponse> recommendations = recommendationService.generateRecommendations(context);
		assertThat(recommendations.getFirst().severity()).isEqualTo(RecommendationSeverity.WARNING);
	}

	@Test
	@DisplayName("should_ReturnEmpty_When_NoRelevantRecommendationsExist")
	void should_ReturnEmpty_When_NoRelevantRecommendationsExist() {
		List<RouteRecommendationResponse> recommendations = recommendationService.generateRecommendations(
				context(List.of(stage("chill-penguin", "Chill Penguin", null)), List.of(), Map.of(), 5)
		);

		assertThat(recommendations).isEmpty();
	}

	private RouteAnalysisContext context(
			List<Stage> orderedStages,
			List<RouteWarningResponse> warnings,
			Map<String, Weapon> weaponsByStage,
			Integer backtrackingScore
	) {
		Game game = new Game();
		game.setCode("MMX");
		return new RouteAnalysisContext(game, orderedStages, weaponsByStage, warnings, backtrackingScore);
	}

	private Stage stage(String slug, String bossName, String weaknessWeapon) {
		Stage stage = new Stage();
		stage.setSlug(slug);
		stage.setName(bossName + " Stage");
		Boss boss = new Boss();
		boss.setSlug(slug);
		boss.setName(bossName);
		boss.setWeaknessWeapon(weaknessWeapon);
		stage.setBoss(boss);
		return stage;
	}

	private Weapon weapon(String slug, String name, Stage obtainedFromStage) {
		Weapon weapon = new Weapon();
		weapon.setSlug(slug);
		weapon.setName(name);
		weapon.setObtainedFromStage(obtainedFromStage);
		return weapon;
	}
}
