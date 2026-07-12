package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.route.*;
import com.danyaell.mavericklabsbe.game.entity.*;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.repository.GameRepository;
import com.danyaell.mavericklabsbe.game.repository.StageRepository;
import com.danyaell.mavericklabsbe.game.repository.WeaponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RouteAnalysisService Unit Tests")
class RouteAnalysisServiceTests {

	@Mock
	private GameRepository gameRepository;

	@Mock
	private StageRepository stageRepository;

	@Mock
	private WeaponRepository weaponRepository;

	@Mock
	private RecommendationService recommendationService;

	@InjectMocks
	private RouteAnalysisService routeAnalysisService;

	@Test
	@DisplayName("should_AnalyzeValidRoute_When_RequestIsValid")
	void should_AnalyzeValidRoute_When_RequestIsValid() {
		Game game = game();
		Stage chillPenguin = stage(game, 1L, "chill-penguin", "Chill Penguin", 45, 12, "flame-wave");
		Stage sparkMandrill = stage(game, 2L, "spark-mandrill", "Spark Mandrill", 68, 16, "shotgun-ice");
		Weapon shotgunIce = weapon(game, chillPenguin, "shotgun-ice", "Shotgun Ice");

		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
		when(stageRepository.findByGameIdWithAnalysisData(1L)).thenReturn(List.of(chillPenguin, sparkMandrill));
		when(weaponRepository.findByGameId(1L)).thenReturn(List.of(shotgunIce));
		when(recommendationService.generateRecommendations(any())).thenReturn(List.of());

		RouteAnalysisResponse response = routeAnalysisService.analyzeRoute(request(List.of("chill-penguin", "spark-mandrill")));

		assertThat(response.gameCode()).isEqualTo("MMX");
		assertThat(response.difficultyScore()).isBetween(0, 100);
		assertThat(response.backtrackingScore()).isGreaterThanOrEqualTo(0);
		assertThat(response.estimatedMinutes()).isGreaterThan(0);
		assertThat(response.recommendations()).isEmpty();
	}

	@Test
	@DisplayName("should_ApplyWeaknessOptimization_When_WeaknessWasAcquired")
	void should_ApplyWeaknessOptimization_When_WeaknessWasAcquired() {
		Game game = game();
		Stage chillPenguin = stage(game, 1L, "chill-penguin", "Chill Penguin", 45, 12, "flame-wave");
		Stage sparkMandrill = stage(game, 2L, "spark-mandrill", "Spark Mandrill", 68, 16, "shotgun-ice");
		Weapon shotgunIce = weapon(game, chillPenguin, "shotgun-ice", "Shotgun Ice");

		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
		when(stageRepository.findByGameIdWithAnalysisData(1L)).thenReturn(List.of(chillPenguin, sparkMandrill));
		when(weaponRepository.findByGameId(1L)).thenReturn(List.of(shotgunIce));
		when(recommendationService.generateRecommendations(any())).thenReturn(List.of());

		RouteAnalysisResponse optimized = routeAnalysisService.analyzeRoute(request(List.of("chill-penguin", "spark-mandrill")));
		RouteAnalysisResponse notOptimized = routeAnalysisService.analyzeRoute(request(List.of("spark-mandrill", "chill-penguin")));

		assertThat(optimized.breakdown().weaknessOptimization()).isGreaterThan(notOptimized.breakdown().weaknessOptimization());
	}

	@Test
	@DisplayName("should_AddBacktrackingWarning_When_RequirementIsMissing")
	void should_AddBacktrackingWarning_When_RequirementIsMissing() {
		Game game = game();
		Stage chillPenguin = stage(game, 1L, "chill-penguin", "Chill Penguin", 45, 12, "flame-wave");
		Collectible collectible = collectible(chillPenguin, "spark-mandrill-sub-tank", "Sub Tank");
		collectible.setRequirements(List.of(requirement(collectible, RequirementType.WEAPON, "electric-spark")));
		chillPenguin.setCollectibles(List.of(collectible));

		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
		when(stageRepository.findByGameIdWithAnalysisData(1L)).thenReturn(List.of(chillPenguin));
		when(weaponRepository.findByGameId(1L)).thenReturn(List.of());
		when(recommendationService.generateRecommendations(any())).thenReturn(List.of());

		RouteAnalysisResponse response = routeAnalysisService.analyzeRoute(request(List.of("chill-penguin")));

		assertThat(response.warnings()).hasSize(1);
		assertThat(response.warnings().getFirst().type()).isEqualTo(RouteWarningType.MISSING_REQUIREMENT);
		assertThat(response.backtrackingScore()).isGreaterThan(0);
	}

	@Test
	@DisplayName("should_NotAddBacktracking_When_RequirementWasAlreadyMet")
	void should_NotAddBacktracking_When_RequirementWasAlreadyMet() {
		Game game = game();
		Stage chillPenguin = stage(game, 1L, "chill-penguin", "Chill Penguin", 45, 12, "flame-wave");
		Stage sparkMandrill = stage(game, 2L, "spark-mandrill", "Spark Mandrill", 68, 16, "shotgun-ice");
		Collectible collectible = collectible(sparkMandrill, "spark-mandrill-sub-tank", "Sub Tank");
		collectible.setRequirements(List.of(requirement(collectible, RequirementType.WEAPON, "shotgun-ice")));
		sparkMandrill.setCollectibles(List.of(collectible));
		Weapon shotgunIce = weapon(game, chillPenguin, "shotgun-ice", "Shotgun Ice");

		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
		when(stageRepository.findByGameIdWithAnalysisData(1L)).thenReturn(List.of(chillPenguin, sparkMandrill));
		when(weaponRepository.findByGameId(1L)).thenReturn(List.of(shotgunIce));
		when(recommendationService.generateRecommendations(any())).thenReturn(List.of());

		RouteAnalysisResponse response = routeAnalysisService.analyzeRoute(request(List.of("chill-penguin", "spark-mandrill")));
		assertThat(response.backtrackingScore()).isEqualTo(0);
		assertThat(response.warnings()).isEmpty();
	}

	@Test
	@DisplayName("should_RejectDuplicateStages_When_RouteContainsDuplicates")
	void should_RejectDuplicateStages_When_RouteContainsDuplicates() {
		Game game = game();
		Stage chillPenguin = stage(game, 1L, "chill-penguin", "Chill Penguin", 45, 12, "flame-wave");

		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
		when(stageRepository.findByGameIdWithAnalysisData(1L)).thenReturn(List.of(chillPenguin));

		assertThatThrownBy(() -> routeAnalysisService.analyzeRoute(request(List.of("chill-penguin", "chill-penguin"))))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("duplicate");
		verifyNoInteractions(recommendationService);
	}

	@Test
	@DisplayName("should_RejectUnknownStage_When_StageDoesNotBelongToGame")
	void should_RejectUnknownStage_When_StageDoesNotBelongToGame() {
		Game game = game();
		Stage chillPenguin = stage(game, 1L, "chill-penguin", "Chill Penguin", 45, 12, "flame-wave");

		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
		when(stageRepository.findByGameIdWithAnalysisData(1L)).thenReturn(List.of(chillPenguin));

		assertThatThrownBy(() -> routeAnalysisService.analyzeRoute(request(List.of("spark-mandrill"))))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Unknown stage");
		verifyNoInteractions(recommendationService);
	}

	@Test
	@DisplayName("should_RejectIncompleteRoute_When_GoalIsHundredPercent")
	void should_RejectIncompleteRoute_When_GoalIsHundredPercent() {
		Game game = game();
		Stage chillPenguin = stage(game, 1L, "chill-penguin", "Chill Penguin", 45, 12, "flame-wave");
		Stage sparkMandrill = stage(game, 2L, "spark-mandrill", "Spark Mandrill", 68, 16, "shotgun-ice");

		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
		when(stageRepository.findByGameIdWithAnalysisData(1L)).thenReturn(List.of(chillPenguin, sparkMandrill));

		assertThatThrownBy(() -> routeAnalysisService.analyzeRoute(request(List.of("chill-penguin"))))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("complete route");
		verifyNoInteractions(recommendationService);
	}

	@Test
	@DisplayName("should_ThrowResourceNotFound_When_GameCodeDoesNotExist")
	void should_ThrowResourceNotFound_When_GameCodeDoesNotExist() {
		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.empty());
		assertThatThrownBy(() -> routeAnalysisService.analyzeRoute(request(List.of("chill-penguin"))))
				.isInstanceOf(ResourceNotFoundException.class);
		verifyNoInteractions(stageRepository, recommendationService);
	}

	@Test
	@DisplayName("should_IncludeRecommendations_When_ServiceReturnsThem")
	void should_IncludeRecommendations_When_ServiceReturnsThem() {
		Game game = game();
		Stage chillPenguin = stage(game, 1L, "chill-penguin", "Chill Penguin", 45, 12, "flame-wave");

		RouteRecommendationResponse recommendation = new RouteRecommendationResponse(
				RecommendationType.BACKTRACKING,
				RecommendationSeverity.WARNING,
				"test",
				List.of("chill-penguin")
		);

		when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
		when(stageRepository.findByGameIdWithAnalysisData(1L)).thenReturn(List.of(chillPenguin));
		when(weaponRepository.findByGameId(1L)).thenReturn(List.of());
		when(recommendationService.generateRecommendations(any())).thenReturn(List.of(recommendation));

		RouteAnalysisResponse response = routeAnalysisService.analyzeRoute(request(List.of("chill-penguin")));
		assertThat(response.recommendations()).containsExactly(recommendation);
	}

	private AnalyzeRouteRequest request(List<String> stageOrder) {
		return new AnalyzeRouteRequest("MMX", stageOrder, RouteGoal.HUNDRED_PERCENT);
	}

	private Game game() {
		Game game = new Game();
		game.setId(1L);
		game.setCode("MMX");
		game.setTitle("Mega Man X");
		game.setReleaseOrder(1);
		return game;
	}

	private Stage stage(Game game, Long id, String slug, String bossName, int baseDifficulty, int minutes, String weaknessWeapon) {
		Stage stage = new Stage();
		stage.setId(id);
		stage.setGame(game);
		stage.setSlug(slug);
		stage.setName(bossName + " Stage");
		stage.setBaseDifficulty(baseDifficulty);
		stage.setEstimatedMinutes(minutes);
		stage.setCollectibles(List.of());

		Boss boss = new Boss();
		boss.setStage(stage);
		boss.setSlug(slug);
		boss.setName(bossName);
		boss.setWeaknessWeapon(weaknessWeapon);
		stage.setBoss(boss);
		return stage;
	}

	private Weapon weapon(Game game, Stage stage, String slug, String name) {
		Weapon weapon = new Weapon();
		weapon.setGame(game);
		weapon.setObtainedFromStage(stage);
		weapon.setSlug(slug);
		weapon.setName(name);
		return weapon;
	}

	private Collectible collectible(Stage stage, String slug, String name) {
		Collectible collectible = new Collectible();
		collectible.setStage(stage);
		collectible.setSlug(slug);
		collectible.setName(name);
		collectible.setType(CollectibleType.SUB_TANK);
		return collectible;
	}

	private CollectibleRequirement requirement(Collectible collectible, RequirementType type, String requiredKey) {
		CollectibleRequirement requirement = new CollectibleRequirement();
		requirement.setCollectible(collectible);
		requirement.setRequirementType(type);
		requirement.setRequiredKey(requiredKey);
		return requirement;
	}
}
