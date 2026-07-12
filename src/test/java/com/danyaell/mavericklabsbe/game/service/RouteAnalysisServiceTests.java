package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.AnalyzeRouteRequest;
import com.danyaell.mavericklabsbe.game.dto.RouteAnalysisResponse;
import com.danyaell.mavericklabsbe.game.dto.RouteGoal;
import com.danyaell.mavericklabsbe.game.dto.RouteWarningType;
import com.danyaell.mavericklabsbe.game.entity.*;
import com.danyaell.mavericklabsbe.game.exception.InvalidRouteException;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.repository.CollectibleRepository;
import com.danyaell.mavericklabsbe.game.repository.GameRepository;
import com.danyaell.mavericklabsbe.game.repository.StageRepository;
import com.danyaell.mavericklabsbe.game.repository.WeaponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
    private CollectibleRepository collectibleRepository;

    @InjectMocks
    private RouteAnalysisService routeAnalysisService;

    @Test
    @DisplayName("should_AnalyzeValidRoute_When_RequestContainsAllStages")
    void should_AnalyzeValidRoute_When_RequestContainsAllStages() {
        Game game = createRouteGame();
        mockRouteData(game);

        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin", "spark-mandrill", "storm-eagle", "flame-mammoth"),
                RouteGoal.HUNDRED_PERCENT
        );

        RouteAnalysisResponse result = routeAnalysisService.analyzeRoute(request);

        assertThat(result.gameCode()).isEqualTo("MMX");
        assertThat(result.difficultyScore()).isBetween(0, 100);
        assertThat(result.backtrackingScore()).isEqualTo(0);
        assertThat(result.estimatedMinutes()).isEqualTo(41);
        assertThat(result.warnings()).isEmpty();
    }

    @Test
    @DisplayName("should_ReduceDifficulty_When_WeaknessWasAlreadyAcquired")
    void should_ReduceDifficulty_When_WeaknessWasAlreadyAcquired() {
        Game game = createRouteGame();
        mockRouteData(game);

        AnalyzeRouteRequest optimizedRoute = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin", "spark-mandrill", "storm-eagle", "flame-mammoth"),
                RouteGoal.HUNDRED_PERCENT
        );
        AnalyzeRouteRequest nonOptimizedRoute = new AnalyzeRouteRequest(
                "MMX",
                List.of("spark-mandrill", "chill-penguin", "storm-eagle", "flame-mammoth"),
                RouteGoal.HUNDRED_PERCENT
        );

        RouteAnalysisResponse optimized = routeAnalysisService.analyzeRoute(optimizedRoute);
        RouteAnalysisResponse nonOptimized = routeAnalysisService.analyzeRoute(nonOptimizedRoute);

        assertThat(optimized.difficultyScore()).isLessThan(nonOptimized.difficultyScore());
        assertThat(optimized.breakdown().weaknessOptimization()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should_IncreaseBossDifficulty_When_HardBossIsFoughtWithoutWeakness")
    void should_IncreaseBossDifficulty_When_HardBossIsFoughtWithoutWeakness() {
        Game game = createRouteGame();
        mockRouteData(game);

        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("spark-mandrill", "chill-penguin", "storm-eagle", "flame-mammoth"),
                RouteGoal.HUNDRED_PERCENT
        );

        RouteAnalysisResponse result = routeAnalysisService.analyzeRoute(request);

        assertThat(result.breakdown().bossDifficulty()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should_AddBacktrackingWarning_When_CollectibleRequirementIsMissing")
    void should_AddBacktrackingWarning_When_CollectibleRequirementIsMissing() {
        Game game = createRouteGame();
        mockRouteData(game);

        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("flame-mammoth", "chill-penguin", "spark-mandrill", "storm-eagle"),
                RouteGoal.HUNDRED_PERCENT
        );

        RouteAnalysisResponse result = routeAnalysisService.analyzeRoute(request);

        assertThat(result.backtrackingScore()).isGreaterThan(0);
        assertThat(result.warnings())
                .hasSize(1)
                .first()
                .satisfies(warning -> {
                    assertThat(warning.type()).isEqualTo(RouteWarningType.MISSING_REQUIREMENT);
                    assertThat(warning.stageSlug()).isEqualTo("flame-mammoth");
                    assertThat(warning.collectibleSlug()).isEqualTo("flame-mammoth-heart-tank");
                });
    }

    @Test
    @DisplayName("should_NotAddBacktracking_When_CollectibleRequirementWasAlreadyMet")
    void should_NotAddBacktracking_When_CollectibleRequirementWasAlreadyMet() {
        Game game = createRouteGame();
        mockRouteData(game);

        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin", "spark-mandrill", "flame-mammoth", "storm-eagle"),
                RouteGoal.HUNDRED_PERCENT
        );

        RouteAnalysisResponse result = routeAnalysisService.analyzeRoute(request);

        assertThat(result.backtrackingScore()).isEqualTo(0);
        assertThat(result.warnings()).isEmpty();
    }

    @Test
    @DisplayName("should_RejectRoute_When_StagesAreDuplicated")
    void should_RejectRoute_When_StagesAreDuplicated() {
        Game game = createRouteGame();
        mockRouteData(game);

        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin", "chill-penguin", "storm-eagle", "flame-mammoth"),
                RouteGoal.HUNDRED_PERCENT
        );

        assertThatThrownBy(() -> routeAnalysisService.analyzeRoute(request))
                .isInstanceOf(InvalidRouteException.class)
                .hasMessageContaining("Duplicated stage");
    }

    @Test
    @DisplayName("should_RejectRoute_When_StageDoesNotBelongToGame")
    void should_RejectRoute_When_StageDoesNotBelongToGame() {
        Game game = createRouteGame();
        mockRouteData(game);

        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin", "spark-mandrill", "unknown-stage", "flame-mammoth"),
                RouteGoal.HUNDRED_PERCENT
        );

        assertThatThrownBy(() -> routeAnalysisService.analyzeRoute(request))
                .isInstanceOf(InvalidRouteException.class)
                .hasMessageContaining("Stage does not belong to game");
    }

    @Test
    @DisplayName("should_RejectRoute_When_HundredPercentRouteIsIncomplete")
    void should_RejectRoute_When_HundredPercentRouteIsIncomplete() {
        Game game = createRouteGame();
        mockRouteData(game);

        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin", "spark-mandrill", "flame-mammoth"),
                RouteGoal.HUNDRED_PERCENT
        );

        assertThatThrownBy(() -> routeAnalysisService.analyzeRoute(request))
                .isInstanceOf(InvalidRouteException.class)
                .hasMessageContaining("requires all stages");
    }

    @Test
    @DisplayName("should_ThrowNotFound_When_GameCodeDoesNotExist")
    void should_ThrowNotFound_When_GameCodeDoesNotExist() {
        when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.empty());

        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin"),
                RouteGoal.HUNDRED_PERCENT
        );

        assertThatThrownBy(() -> routeAnalysisService.analyzeRoute(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game not found");
    }

    private Game createRouteGame() {
        Game game = new Game(1L, "MMX", "Mega Man X", 1, new ArrayList<>());

        Stage chillPenguin = createStage(1L, game, "chill-penguin", 1, 4, 8);
        Stage sparkMandrill = createStage(2L, game, "spark-mandrill", 2, 8, 11);
        Stage stormEagle = createStage(3L, game, "storm-eagle", 3, 6, 10);
        Stage flameMammoth = createStage(4L, game, "flame-mammoth", 4, 9, 12);

        chillPenguin.setBoss(createBoss(chillPenguin, "chill-penguin", "fire-wave"));
        sparkMandrill.setBoss(createBoss(sparkMandrill, "spark-mandrill", "shotgun-ice"));
        stormEagle.setBoss(createBoss(stormEagle, "storm-eagle", "chameleon-sting"));
        flameMammoth.setBoss(createBoss(flameMammoth, "flame-mammoth", "storm-tornado"));

        Collectible legUpgrade = createCollectible(1L, chillPenguin, "leg-upgrade-capsule", 1);
        Collectible flameHeartTank = createCollectible(2L, flameMammoth, "flame-mammoth-heart-tank", 1);
        flameHeartTank.setRequirements(List.of(createRequirement(1L, flameHeartTank, RequirementType.COLLECTIBLE, "leg-upgrade-capsule")));

        chillPenguin.setCollectibles(List.of(legUpgrade));
        sparkMandrill.setCollectibles(new ArrayList<>());
        stormEagle.setCollectibles(new ArrayList<>());
        flameMammoth.setCollectibles(List.of(flameHeartTank));

        game.setStages(List.of(chillPenguin, sparkMandrill, stormEagle, flameMammoth));
        game.setWeapons(List.of(
                createWeapon(1L, game, chillPenguin, "shotgun-ice"),
                createWeapon(2L, game, stormEagle, "storm-tornado")
        ));
        return game;
    }

    private void mockRouteData(Game game) {
        when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
        when(stageRepository.findByGameIdWithBossAndCollectibles(game.getId())).thenReturn(game.getStages());
        when(weaponRepository.findByGameId(game.getId())).thenReturn(game.getWeapons());
    }

    private Stage createStage(Long id, Game game, String slug, int order, int baseDifficulty, int estimatedMinutes) {
        Stage stage = new Stage();
        stage.setId(id);
        stage.setGame(game);
        stage.setSlug(slug);
        stage.setName(slug);
        stage.setStageOrder(order);
        stage.setBaseDifficulty(baseDifficulty);
        stage.setEstimatedMinutes(estimatedMinutes);
        stage.setCollectibles(new ArrayList<>());
        return stage;
    }

    private Boss createBoss(Stage stage, String slug, String weaknessWeapon) {
        Boss boss = new Boss();
        boss.setId(stage.getId());
        boss.setStage(stage);
        boss.setSlug(slug);
        boss.setName(slug);
        boss.setWeaknessWeapon(weaknessWeapon);
        return boss;
    }

    private Weapon createWeapon(Long id, Game game, Stage stage, String slug) {
        Weapon weapon = new Weapon();
        weapon.setId(id);
        weapon.setGame(game);
        weapon.setObtainedFromStage(stage);
        weapon.setSlug(slug);
        weapon.setName(slug);
        return weapon;
    }

    private Collectible createCollectible(Long id, Stage stage, String slug, Integer sortOrder) {
        Collectible collectible = new Collectible();
        collectible.setId(id);
        collectible.setStage(stage);
        collectible.setSlug(slug);
        collectible.setName(slug);
        collectible.setType(CollectibleType.HEART_TANK);
        collectible.setSortOrder(sortOrder);
        collectible.setRequirements(new ArrayList<>());
        return collectible;
    }

    private CollectibleRequirement createRequirement(Long id, Collectible collectible, RequirementType type, String requiredKey) {
        CollectibleRequirement requirement = new CollectibleRequirement();
        requirement.setId(id);
        requirement.setCollectible(collectible);
        requirement.setRequirementType(type);
        requirement.setRequiredKey(requiredKey);
        return requirement;
    }
}
