package com.danyaell.mavericklabsbe.game.repository;

import com.danyaell.mavericklabsbe.game.entity.Game;
import com.danyaell.mavericklabsbe.game.fixture.GameTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("GameRepository Integration Tests")
class GameRepositoryTests {

    @Autowired
    private GameRepository gameRepository;

    @Test
    @DisplayName("should_SaveGame_When_GameIsPersistedToDatabase")
    void should_SaveGame_When_GameIsPersistedToDatabase() {
        // Arrange
        Game game = GameTestFixture.createMegaManX();

        // Act
        Game savedGame = gameRepository.save(game);

        // Assert
        assertThat(savedGame.getId()).isNotNull();
        assertThat(savedGame.getCode()).isEqualTo("MMX");
        assertThat(savedGame.getTitle()).isEqualTo("Mega Man X");
    }

    @Test
    @DisplayName("should_FindAllGamesOrderedByReleaseOrder_When_MultipleGamesExist")
    void should_FindAllGamesOrderedByReleaseOrder_When_MultipleGamesExist() {
        // Arrange
        gameRepository.save(GameTestFixture.createGameWithId(null, "MMX3", "Mega Man X3", 3));
        gameRepository.save(GameTestFixture.createGameWithId(null, "MMX", "Mega Man X", 1));
        gameRepository.save(GameTestFixture.createGameWithId(null, "MMX2", "Mega Man X2", 2));

        // Act
        List<Game> result = gameRepository.findAllByOrderByReleaseOrderAsc();

        // Assert
        assertThat(result)
                .hasSize(3)
                .extracting(Game::getCode)
                .containsExactly("MMX", "MMX2", "MMX3");
    }

    @Test
    @DisplayName("should_ReturnGameInCorrectOrder_When_FindAllByOrderByReleaseOrderAscIsCalled")
    void should_ReturnGameInCorrectOrder_When_FindAllByOrderByReleaseOrderAscIsCalled() {
        // Arrange
        gameRepository.save(new Game(null, "MMX5", "Mega Man X5", 5, new ArrayList<>()));
        gameRepository.save(new Game(null, "MMX2", "Mega Man X2", 2, new ArrayList<>()));
        gameRepository.save(new Game(null, "MMX4", "Mega Man X4", 4, new ArrayList<>()));
        gameRepository.save(new Game(null, "MMX1", "Mega Man X1", 1, new ArrayList<>()));
        gameRepository.save(new Game(null, "MMX3", "Mega Man X3", 3, new ArrayList<>()));

        // Act
        List<Game> result = gameRepository.findAllByOrderByReleaseOrderAsc();

        // Assert
        assertThat(result)
                .extracting(Game::getReleaseOrder)
                .containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("should_ReturnEmptyList_When_NoGamesExistInDatabase")
    void should_ReturnEmptyList_When_NoGamesExistInDatabase() {
        // Act
        List<Game> result = gameRepository.findAllByOrderByReleaseOrderAsc();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should_FindGameById_When_GameExistsInDatabase")
    void should_FindGameById_When_GameExistsInDatabase() {
        // Arrange
        Game savedGame = gameRepository.save(GameTestFixture.createMegaManX());

        // Act
        var foundGame = gameRepository.findById(savedGame.getId());

        // Assert
        assertThat(foundGame)
                .isPresent()
                .get()
                .extracting(Game::getCode, Game::getTitle)
                .containsExactly("MMX", "Mega Man X");
    }

    @Test
    @DisplayName("should_UpdateGame_When_GameIsModifiedAndSaved")
    void should_UpdateGame_When_GameIsModifiedAndSaved() {
        // Arrange
        Game savedGame = gameRepository.save(GameTestFixture.createMegaManX());
        savedGame.setTitle("Mega Man X - Updated");

        // Act
        Game updatedGame = gameRepository.save(savedGame);

        // Assert
        assertThat(updatedGame.getTitle()).isEqualTo("Mega Man X - Updated");
    }

    @Test
    @DisplayName("should_DeleteGame_When_GameIsRemovedFromDatabase")
    void should_DeleteGame_When_GameIsRemovedFromDatabase() {
        // Arrange
        Game savedGame = gameRepository.save(GameTestFixture.createMegaManX());

        // Act
        gameRepository.delete(savedGame);

        // Assert
        var result = gameRepository.findById(savedGame.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should_EnforceUniqueCodeConstraint_When_DuplicateCodeIsInserted")
    void should_EnforceUniqueCodeConstraint_When_DuplicateCodeIsInserted() {
        // Arrange
        gameRepository.save(GameTestFixture.createMegaManX());

        // Act & Assert
        assertThatThrownBy(() -> {
            gameRepository.save(GameTestFixture.createGameWithId(null, "MMX", "Different Title", 99));
            gameRepository.flush();
        }).isNotNull();
    }

    @Test
    @DisplayName("should_CountGamesInDatabase_When_MultipleGamesExist")
    void should_CountGamesInDatabase_When_MultipleGamesExist() {
        // Arrange
        gameRepository.save(GameTestFixture.createMegaManX());
        gameRepository.save(GameTestFixture.createMegaManX2());
        gameRepository.save(GameTestFixture.createMegaManX3());

        // Act
        long count = gameRepository.count();

        // Assert
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("should_CheckGameExistence_When_GameIsInDatabase")
    void should_CheckGameExistence_When_GameIsInDatabase() {
        // Arrange
        Game savedGame = gameRepository.save(GameTestFixture.createMegaManX());

        // Act
        boolean exists = gameRepository.existsById(savedGame.getId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should_ReturnFalseForNonExistentId_When_CheckingGameExistence")
    void should_ReturnFalseForNonExistentId_When_CheckingGameExistence() {
        // Act
        boolean exists = gameRepository.existsById(999L);

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("should_PreserveGameDataIntegrity_When_SavedAndRetrieved")
    void should_PreserveGameDataIntegrity_When_SavedAndRetrieved() {
        // Arrange
        Game originalGame = new Game(null, "TEST_CODE", "Test Game Title", 42, new ArrayList<>());

        // Act
        Game savedGame = gameRepository.save(originalGame);
        Game retrievedGame = gameRepository.findById(savedGame.getId()).orElse(null);

        // Assert
        assertThat(retrievedGame)
                .isNotNull()
                .extracting(Game::getCode, Game::getTitle, Game::getReleaseOrder)
                .containsExactly("TEST_CODE", "Test Game Title", 42);
    }

    @Test
    @DisplayName("should_FindGameByCodeIgnoreCase_When_ExactCodeIsProvided")
    void should_FindGameByCodeIgnoreCase_When_ExactCodeIsProvided() {
        // Arrange
        gameRepository.save(GameTestFixture.createMegaManX());

        // Act
        var result = gameRepository.findByCodeIgnoreCase("MMX");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("MMX");
    }

    @Test
    @DisplayName("should_FindGameByCodeIgnoreCase_When_LowercaseCodeIsProvided")
    void should_FindGameByCodeIgnoreCase_When_LowercaseCodeIsProvided() {
        // Arrange
        gameRepository.save(GameTestFixture.createMegaManX());

        // Act
        var result = gameRepository.findByCodeIgnoreCase("mmx");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("MMX");
    }

    @Test
    @DisplayName("should_ReturnEmptyOptional_When_GameCodeDoesNotExist")
    void should_ReturnEmptyOptional_When_GameCodeDoesNotExist() {
        // Act
        var result = gameRepository.findByCodeIgnoreCase("INVALID");

        // Assert
        assertThat(result).isEmpty();
    }
}

