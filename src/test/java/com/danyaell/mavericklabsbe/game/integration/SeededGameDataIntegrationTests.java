package com.danyaell.mavericklabsbe.game.integration;

import com.danyaell.mavericklabsbe.game.dto.GameDetailResponse;
import com.danyaell.mavericklabsbe.game.dto.StageResponse;
import com.danyaell.mavericklabsbe.game.dto.route.AnalyzeRouteRequest;
import com.danyaell.mavericklabsbe.game.dto.route.RouteAnalysisResponse;
import com.danyaell.mavericklabsbe.game.dto.route.RouteGoal;
import com.danyaell.mavericklabsbe.game.dto.route.RouteWarningResponse;
import com.danyaell.mavericklabsbe.game.entity.Collectible;
import com.danyaell.mavericklabsbe.game.entity.CollectibleRequirement;
import com.danyaell.mavericklabsbe.game.entity.Game;
import com.danyaell.mavericklabsbe.game.entity.RequirementType;
import com.danyaell.mavericklabsbe.game.entity.Stage;
import com.danyaell.mavericklabsbe.game.repository.CollectibleRepository;
import com.danyaell.mavericklabsbe.game.repository.GameRepository;
import com.danyaell.mavericklabsbe.game.repository.StageRepository;
import com.danyaell.mavericklabsbe.game.service.GameService;
import com.danyaell.mavericklabsbe.game.service.RouteAnalysisService;
import com.danyaell.mavericklabsbe.support.MySqlIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MySqlIntegrationTest
@Transactional(readOnly = true)
class SeededGameDataIntegrationTests {

    private static final List<String> MMX_STAGE_ORDER = List.of(
            "chill-penguin",
            "storm-eagle",
            "flame-mammoth",
            "spark-mandrill",
            "armored-armadillo",
            "launch-octopus",
            "boomer-kuwanger",
            "sting-chameleon"
    );

    @Autowired
    private GameService gameService;

    @Autowired
    private RouteAnalysisService routeAnalysisService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private CollectibleRepository collectibleRepository;

    @Test
    void shouldLoadCompleteMegaManXSeedThroughGameService() {
        GameDetailResponse result = gameService.getGameDetailByCode("MMX");

        assertThat(result.code()).isEqualTo("MMX");
        assertThat(result.title()).isEqualTo("Mega Man X");

        assertThat(result.stages())
                .hasSize(8)
                .extracting(StageResponse::slug)
                .containsExactlyElementsOf(MMX_STAGE_ORDER);

        assertThat(result.stages())
                .allSatisfy(stage -> {
                    assertThat(stage.boss()).isNotNull();
                    assertThat(stage.weaponReward()).isNotNull();
                });

        assertThat(result.stages())
                .extracting(stage -> stage.weaponReward().slug())
                .containsExactly(
                        "shotgun-ice",
                        "storm-tornado",
                        "fire-wave",
                        "electric-spark",
                        "rolling-shield",
                        "homing-torpedo",
                        "boomerang-cutter",
                        "chameleon-sting"
                );

        int collectibleCount = result.stages().stream()
                .mapToInt(stage -> stage.collectibles().size())
                .sum();

        assertThat(collectibleCount).isEqualTo(17);
    }

    @Test
    void shouldLoadNormalizedCollectibleRequirementsFromSeed() {
        Game game = gameRepository.findByCodeIgnoreCase("MMX").orElseThrow();
        List<Stage> stages = stageRepository.findByGameIdWithBossAndCollectibles(game.getId());
        List<Long> stageIds = stages.stream().map(Stage::getId).toList();

        List<Collectible> collectibles =
                collectibleRepository.findByStageIdInWithRequirements(stageIds);

        assertThat(collectibles).hasSize(17);
        assertThat(collectibles.stream().mapToInt(c -> c.getRequirements().size()).sum())
                .isEqualTo(39);

        Collectible legUpgrade = collectibleBySlug(collectibles, "leg-upgrade-capsule");
        Collectible busterUpgrade = collectibleBySlug(collectibles, "x-buster-upgrade-capsule");
        Collectible hadouken = collectibleBySlug(collectibles, "armored-armadillo-hadouken");

        assertThat(legUpgrade.getRequirements()).isEmpty();
        assertThat(busterUpgrade.getRequirements()).hasSize(2);
        assertThat(hadouken.getRequirements()).hasSize(25);

        assertThat(hadouken.getRequirements())
                .filteredOn(requirement -> requirement.getRequirementType() == RequirementType.COLLECTIBLE)
                .hasSize(16);

        assertThat(hadouken.getRequirements())
                .filteredOn(requirement -> requirement.getRequirementType() == RequirementType.WEAPON)
                .hasSize(8);

        assertThat(hadouken.getRequirements())
                .filteredOn(requirement -> requirement.getRequirementType() == RequirementType.OTHER)
                .singleElement()
                .satisfies(requirement -> assertThat(requirement.getDescription()).isNotBlank());

        assertThat(collectibles)
                .flatExtracting(Collectible::getRequirements)
                .allSatisfy(requirement -> assertRequirementBelongsToGame(requirement, game.getId()));
    }

    @Test
    void shouldAnalyzeCompleteRouteUsingRealSeed() {
        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                MMX_STAGE_ORDER,
                RouteGoal.HUNDRED_PERCENT
        );

        RouteAnalysisResponse result = routeAnalysisService.analyzeRoute(request);

        assertThat(result.gameCode()).isEqualTo("MMX");
        assertThat(result.difficultyScore()).isBetween(0, 100);
        assertThat(result.backtrackingScore()).isEqualTo(80);

        assertThat(result.warnings())
                .extracting(RouteWarningResponse::collectibleSlug)
                .containsExactly(
                        "chill-penguin-heart-tank",
                        "spark-mandrill-sub-tank",
                        "armored-armadillo-hadouken",
                        "boomer-kuwanger-heart-tank"
                );

        assertThat(result.recommendations()).isNotEmpty();
    }

    private Collectible collectibleBySlug(List<Collectible> collectibles, String slug) {
        return collectibles.stream()
                .filter(collectible -> slug.equals(collectible.getSlug()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing seeded collectible: " + slug));
    }

    private void assertRequirementBelongsToGame(
            CollectibleRequirement requirement,
            Long expectedGameId
    ) {
        switch (requirement.getRequirementType()) {
            case WEAPON -> {
                assertThat(requirement.getRequiredWeapon()).isNotNull();
                assertThat(requirement.getRequiredWeapon().getGame().getId()).isEqualTo(expectedGameId);
            }
            case COLLECTIBLE -> {
                assertThat(requirement.getRequiredCollectible()).isNotNull();
                assertThat(requirement.getRequiredCollectible().getStage().getGame().getId())
                        .isEqualTo(expectedGameId);
            }
            case STAGE_CLEARED -> {
                assertThat(requirement.getRequiredStage()).isNotNull();
                assertThat(requirement.getRequiredStage().getGame().getId()).isEqualTo(expectedGameId);
            }
            case OTHER -> {
                assertThat(requirement.getRequiredWeapon()).isNull();
                assertThat(requirement.getRequiredCollectible()).isNull();
                assertThat(requirement.getRequiredStage()).isNull();
                assertThat(requirement.getDescription()).isNotBlank();
            }
        }
    }
}