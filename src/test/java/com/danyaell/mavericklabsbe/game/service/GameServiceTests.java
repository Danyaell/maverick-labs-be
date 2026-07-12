package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.GameDetailResponse;
import com.danyaell.mavericklabsbe.game.dto.GameSummaryResponse;
import com.danyaell.mavericklabsbe.game.entity.*;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.fixture.GameTestFixture;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService Unit Tests")
class GameServiceTests {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private WeaponRepository weaponRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    @DisplayName("should_ReturnEmptyList_When_NoGamesExist")
    void should_ReturnEmptyList_When_NoGamesExist() {
        // Arrange
        when(gameRepository.findAllByOrderByReleaseOrderAsc()).thenReturn(new ArrayList<>());

        // Act
        List<GameSummaryResponse> result = gameService.getAllGames();

        // Assert
        assertThat(result).isEmpty();
        verify(gameRepository).findAllByOrderByReleaseOrderAsc();
    }

    @Test
    @DisplayName("should_ReturnOrderedGameList_When_GetAllGamesIsCalled")
    void should_ReturnOrderedGameList_When_GetAllGamesIsCalled() {
        // Arrange
        List<Game> mockGames = GameTestFixture.createDefaultGameList();
        when(gameRepository.findAllByOrderByReleaseOrderAsc()).thenReturn(mockGames);

        // Act
        List<GameSummaryResponse> result = gameService.getAllGames();

        // Assert
        assertThat(result)
                .isNotEmpty()
                .hasSize(3)
                .extracting(GameSummaryResponse::getCode)
                .containsExactly("MMX", "MMX2", "MMX3");
    }

    @Test
    @DisplayName("should_RetainReleaseOrder_When_MappingGameToResponse")
    void should_RetainReleaseOrder_When_MappingGameToResponse() {
        // Arrange
        List<Game> mockGames = GameTestFixture.createDefaultGameList();
        when(gameRepository.findAllByOrderByReleaseOrderAsc()).thenReturn(mockGames);

        // Act
        List<GameSummaryResponse> result = gameService.getAllGames();

        // Assert
        assertThat(result)
                .extracting(GameSummaryResponse::getReleaseOrder)
                .containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("should_MapGameFieldsCorrectly_When_ConvertingToResponse")
    void should_MapGameFieldsCorrectly_When_ConvertingToResponse() {
        // Arrange
        Game megaManX = GameTestFixture.createGameWithId(1L, "MMX", "Mega Man X", 1);
        when(gameRepository.findAllByOrderByReleaseOrderAsc()).thenReturn(List.of(megaManX));

        // Act
        List<GameSummaryResponse> result = gameService.getAllGames();

        // Assert
        assertThat(result).hasSize(1);
        GameSummaryResponse response = result.getFirst();
        assertThat(response)
                .extracting(GameSummaryResponse::getCode, GameSummaryResponse::getTitle, GameSummaryResponse::getReleaseOrder)
                .containsExactly("MMX", "Mega Man X", 1);
    }

    @Test
    @DisplayName("should_CallRepositoryOnce_When_GetAllGamesIsCalled")
    void should_CallRepositoryOnce_When_GetAllGamesIsCalled() {
        // Arrange
        when(gameRepository.findAllByOrderByReleaseOrderAsc()).thenReturn(new ArrayList<>());

        // Act
        gameService.getAllGames();

        // Assert
        verify(gameRepository).findAllByOrderByReleaseOrderAsc();
    }

    @Test
    @DisplayName("should_NotExcludeAnyFields_When_MappingToResponse")
    void should_NotExcludeAnyFields_When_MappingToResponse() {
        // Arrange
        Game game = new Game(1L, "TESTCODE", "Test Title", 99, new ArrayList<>());
        when(gameRepository.findAllByOrderByReleaseOrderAsc()).thenReturn(List.of(game));

        // Act
        List<GameSummaryResponse> result = gameService.getAllGames();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isNotNull().isEqualTo("TESTCODE");
        assertThat(result.getFirst().getTitle()).isNotNull().isEqualTo("Test Title");
        assertThat(result.getFirst().getReleaseOrder()).isNotNull().isEqualTo(99);
    }

    // New tests for getGameDetailByCode

    @Test
    @DisplayName("should_ReturnGameDetail_When_ValidCodeProvided")
    void should_ReturnGameDetail_When_ValidCodeProvided() {
        // Arrange
        Game game = createTestGame(1L, "MMX", "Mega Man X", 1);
        List<Stage> stages = createTestStages(game);

        when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
        when(stageRepository.findByGameIdWithBossAndCollectibles(1L)).thenReturn(stages);
        when(weaponRepository.findByGameId(1L)).thenReturn(new ArrayList<>());

        // Act
        GameDetailResponse result = gameService.getGameDetailByCode("MMX");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("MMX");
        assertThat(result.title()).isEqualTo("Mega Man X");
        assertThat(result.releaseOrder()).isEqualTo(1);
        assertThat(result.stages()).hasSize(2);
    }

    @Test
    @DisplayName("should_ReturnStagesOrderedByStageOrder")
    void should_ReturnStagesOrderedByStageOrder() {
        // Arrange
        Game game = createTestGame(1L, "MMX", "Mega Man X", 1);
        Stage stage1 = createTestStage(1L, game, "stage-1", "Stage 1", 2);
        Stage stage2 = createTestStage(2L, game, "stage-2", "Stage 2", 1);
        List<Stage> stages = List.of(stage1, stage2); // Assume repository returns them ordered

        when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
        when(stageRepository.findByGameIdWithBossAndCollectibles(1L)).thenReturn(stages);
        when(weaponRepository.findByGameId(1L)).thenReturn(new ArrayList<>());

        // Act
        GameDetailResponse result = gameService.getGameDetailByCode("MMX");

        // Assert
        assertThat(result.stages())
                .extracting("stageOrder")
                .containsExactly(2, 1);
    }

    @Test
    @DisplayName("should_ReturnCollectiblesOrderedBySortOrder")
    void should_ReturnCollectiblesOrderedBySortOrder() {
        // Arrange
        Game game = createTestGame(1L, "MMX", "Mega Man X", 1);
        Stage stage = createTestStage(1L, game, "stage-1", "Stage 1", 1);

        Collectible c1 = createTestCollectible(1L, stage, "col-1", "Collectible 1", 2);
        Collectible c2 = createTestCollectible(2L, stage, "col-2", "Collectible 2", 1);
        stage.setCollectibles(List.of(c1, c2));

        when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
        when(stageRepository.findByGameIdWithBossAndCollectibles(1L)).thenReturn(List.of(stage));
        when(weaponRepository.findByGameId(1L)).thenReturn(new ArrayList<>());

        // Act
        GameDetailResponse result = gameService.getGameDetailByCode("MMX");

        // Assert
        assertThat(result.stages().getFirst().collectibles())
                .extracting("sortOrder")
                .containsExactly(1, 2);
    }

    @Test
    @DisplayName("should_IncludeBossForEachStage")
    void should_IncludeBossForEachStage() {
        // Arrange
        Game game = createTestGame(1L, "MMX", "Mega Man X", 1);
        Stage stage = createTestStage(1L, game, "stage-1", "Stage 1", 1);
        Boss boss = new Boss(1L, stage, "chill-penguin", "Chill Penguin", "image-key", null);
        stage.setBoss(boss);

        when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
        when(stageRepository.findByGameIdWithBossAndCollectibles(1L)).thenReturn(List.of(stage));
        when(weaponRepository.findByGameId(1L)).thenReturn(new ArrayList<>());

        // Act
        GameDetailResponse result = gameService.getGameDetailByCode("MMX");

        // Assert
        assertThat(result.stages().getFirst().boss()).isNotNull();
        assertThat(result.stages().getFirst().boss().slug()).isEqualTo("chill-penguin");
        assertThat(result.stages().getFirst().boss().name()).isEqualTo("Chill Penguin");
    }

    @Test
    @DisplayName("should_IncludeWeaponRewardForEachStage")
    void should_IncludeWeaponRewardForEachStage() {
        // Arrange
        Game game = createTestGame(1L, "MMX", "Mega Man X", 1);
        Stage stage = createTestStage(1L, game, "stage-1", "Stage 1", 1);

        Weapon weapon = new Weapon(1L, game, stage, "shotgun-ice", "Shotgun Ice", "desc", "image-key");

        when(gameRepository.findByCodeIgnoreCase("MMX")).thenReturn(Optional.of(game));
        when(stageRepository.findByGameIdWithBossAndCollectibles(1L)).thenReturn(List.of(stage));
        when(weaponRepository.findByGameId(1L)).thenReturn(List.of(weapon));

        // Act
        GameDetailResponse result = gameService.getGameDetailByCode("MMX");

        // Assert
        assertThat(result.stages().getFirst().weaponReward()).isNotNull();
        assertThat(result.stages().getFirst().weaponReward().slug()).isEqualTo("shotgun-ice");
        assertThat(result.stages().getFirst().weaponReward().name()).isEqualTo("Shotgun Ice");
    }

    @Test
    @DisplayName("should_ThrowResourceNotFoundException_When_GameCodeDoesNotExist")
    void should_ThrowResourceNotFoundException_When_GameCodeDoesNotExist() {
        // Arrange
        when(gameRepository.findByCodeIgnoreCase("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gameService.getGameDetailByCode("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Game not found: INVALID");
    }

    @Test
    @DisplayName("should_FindGameRegardlessOfLowercaseCode")
    void should_FindGameRegardlessOfLowercaseCode() {
        // Arrange
        Game game = createTestGame(1L, "MMX", "Mega Man X", 1);
        List<Stage> stages = createTestStages(game);

        when(gameRepository.findByCodeIgnoreCase("mmx")).thenReturn(Optional.of(game));
        when(stageRepository.findByGameIdWithBossAndCollectibles(1L)).thenReturn(stages);
        when(weaponRepository.findByGameId(1L)).thenReturn(new ArrayList<>());

        // Act
        GameDetailResponse result = gameService.getGameDetailByCode("mmx");

        // Assert
        assertThat(result.code()).isEqualTo("MMX");
    }

    // Helper methods

    private Game createTestGame(Long id, String code, String title, Integer releaseOrder) {
        return new Game(id, code, title, releaseOrder, new ArrayList<>());
    }

    private Stage createTestStage(Long id, Game game, String slug, String name, Integer stageOrder) {
        Stage stage = new Stage();
        stage.setId(id);
        stage.setGame(game);
        stage.setSlug(slug);
        stage.setName(name);
        stage.setStageOrder(stageOrder);
        stage.setBaseDifficulty(5);
        stage.setEstimatedMinutes(10);
        stage.setImageAssetKey("image-key");
        stage.setCollectibles(new ArrayList<>());
        return stage;
    }

    private Collectible createTestCollectible(Long id, Stage stage, String slug, String name, Integer sortOrder) {
        Collectible collectible = new Collectible();
        collectible.setId(id);
        collectible.setStage(stage);
        collectible.setSlug(slug);
        collectible.setName(name);
        collectible.setType(CollectibleType.HEART_TANK);
        collectible.setDescription("Test description");
        collectible.setImageAssetKey("image-key");
        collectible.setSortOrder(sortOrder);
        return collectible;
    }

    private List<Stage> createTestStages(Game game) {
        Stage stage1 = createTestStage(1L, game, "stage-1", "Stage 1", 1);
        Stage stage2 = createTestStage(2L, game, "stage-2", "Stage 2", 2);
        return List.of(stage1, stage2);
    }
}
