package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.GameSummaryResponse;
import com.danyaell.mavericklabsbe.game.entity.Game;
import com.danyaell.mavericklabsbe.game.fixture.GameTestFixture;
import com.danyaell.mavericklabsbe.game.repository.GameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService Unit Tests")
class GameServiceTests {

    @Mock
    private GameRepository gameRepository;

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
        Game game = new Game(1L, "TESTCODE", "Test Title", 99);
        when(gameRepository.findAllByOrderByReleaseOrderAsc()).thenReturn(List.of(game));

        // Act
        List<GameSummaryResponse> result = gameService.getAllGames();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isNotNull().isEqualTo("TESTCODE");
        assertThat(result.getFirst().getTitle()).isNotNull().isEqualTo("Test Title");
        assertThat(result.getFirst().getReleaseOrder()).isNotNull().isEqualTo(99);
    }
}

