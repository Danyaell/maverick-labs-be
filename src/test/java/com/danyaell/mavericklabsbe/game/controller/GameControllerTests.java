package com.danyaell.mavericklabsbe.game.controller;

import com.danyaell.mavericklabsbe.common.exception.GlobalExceptionHandler;
import com.danyaell.mavericklabsbe.game.dto.*;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.fixture.GameTestFixture;
import com.danyaell.mavericklabsbe.game.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameController Integration Tests")
class GameControllerTests {

    private MockMvc mockMvc;

    @Mock
    private GameService gameService;

    @BeforeEach
    void setup() {
        GameController gameController = new GameController(gameService);
        mockMvc = MockMvcBuilders.standaloneSetup(gameController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("should_ReturnStatus200_When_GetAllGamesEndpointIsCalled")
    void should_ReturnStatus200_When_GetAllGamesEndpointIsCalled() throws Exception {
        // Arrange
        when(gameService.getAllGames()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should_ReturnGamesList_When_GetAllGamesEndpointIsCalled")
    void should_ReturnGamesList_When_GetAllGamesEndpointIsCalled() throws Exception {
        // Arrange
        List<GameSummaryResponse> mockGames = GameTestFixture.createDefaultGameSummaryResponseList();
        when(gameService.getAllGames()).thenReturn(mockGames);

        // Act & Assert
        mockMvc.perform(get("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("should_ReturnOrderedGames_When_GetAllGamesEndpointIsCalled")
    void should_ReturnOrderedGames_When_GetAllGamesEndpointIsCalled() throws Exception {
        // Arrange
        List<GameSummaryResponse> mockGames = GameTestFixture.createDefaultGameSummaryResponseList();
        when(gameService.getAllGames()).thenReturn(mockGames);

        // Act & Assert
        mockMvc.perform(get("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code", is("MMX")))
                .andExpect(jsonPath("$[1].code", is("MMX2")))
                .andExpect(jsonPath("$[2].code", is("MMX3")));
    }

    @Test
    @DisplayName("should_ReturnGameWithAllFields_When_GetAllGamesEndpointIsCalled")
    void should_ReturnGameWithAllFields_When_GetAllGamesEndpointIsCalled() throws Exception {
        // Arrange
        List<GameSummaryResponse> mockGames = GameTestFixture.createDefaultGameSummaryResponseList();
        when(gameService.getAllGames()).thenReturn(mockGames);

        // Act & Assert
        mockMvc.perform(get("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code", is("MMX")))
                .andExpect(jsonPath("$[0].title", is("Mega Man X")))
                .andExpect(jsonPath("$[0].releaseOrder", is(1)));
    }

    @Test
    @DisplayName("should_ReturnEmptyList_When_NoGamesExist")
    void should_ReturnEmptyList_When_NoGamesExist() throws Exception {
        // Arrange
        when(gameService.getAllGames()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("should_CallGameService_When_GetAllGamesEndpointIsCalled")
    void should_CallGameService_When_GetAllGamesEndpointIsCalled() throws Exception {
        // Arrange
        when(gameService.getAllGames()).thenReturn(new ArrayList<>());

        // Act
        mockMvc.perform(get("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        verify(gameService).getAllGames();
    }

    @Test
    @DisplayName("should_ReturnJsonContentType_When_GetAllGamesEndpointIsCalled")
    void should_ReturnJsonContentType_When_GetAllGamesEndpointIsCalled() throws Exception {
        // Arrange
        when(gameService.getAllGames()).thenReturn(GameTestFixture.createDefaultGameSummaryResponseList());

        // Act
        MvcResult result = mockMvc.perform(get("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        assertThat(result.getResponse().getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    @DisplayName("should_PreserveReleaseOrderSequence_When_ReturnedFromEndpoint")
    void should_PreserveReleaseOrderSequence_When_ReturnedFromEndpoint() throws Exception {
        // Arrange
        List<GameSummaryResponse> mockGames = GameTestFixture.createDefaultGameSummaryResponseList();
        when(gameService.getAllGames()).thenReturn(mockGames);

        // Act & Assert
        mockMvc.perform(get("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].releaseOrder", is(1)))
                .andExpect(jsonPath("$[1].releaseOrder", is(2)))
                .andExpect(jsonPath("$[2].releaseOrder", is(3)));
    }

    // Tests for GET /api/v1/games/{gameCode} endpoint

    @Test
    @DisplayName("should_ReturnStatus200_When_GetGameDetailWithValidCode")
    void should_ReturnStatus200_When_GetGameDetailWithValidCode() throws Exception {
        // Arrange
        GameDetailResponse mockGameDetail = createMockGameDetail("MMX", "Mega Man X", 1);
        when(gameService.getGameDetailByCode("MMX")).thenReturn(mockGameDetail);

        // Act & Assert
        mockMvc.perform(get("/api/v1/games/MMX")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should_ReturnGameDetail_When_GetGameDetailWithValidCode")
    void should_ReturnGameDetail_When_GetGameDetailWithValidCode() throws Exception {
        // Arrange
        GameDetailResponse mockGameDetail = createMockGameDetail("MMX", "Mega Man X", 1);
        when(gameService.getGameDetailByCode("MMX")).thenReturn(mockGameDetail);

        // Act & Assert
        mockMvc.perform(get("/api/v1/games/MMX")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("MMX")))
                .andExpect(jsonPath("$.title", is("Mega Man X")))
                .andExpect(jsonPath("$.releaseOrder", is(1)))
                .andExpect(jsonPath("$.stages", hasSize(1)));
    }

    @Test
    @DisplayName("should_NotExposeInternalIds_When_GetGameDetail")
    void should_NotExposeInternalIds_When_GetGameDetail() throws Exception {
        // Arrange
        GameDetailResponse mockGameDetail = createMockGameDetail("MMX", "Mega Man X", 1);
        when(gameService.getGameDetailByCode("MMX")).thenReturn(mockGameDetail);

        // Act
        MvcResult result = mockMvc.perform(get("/api/v1/games/MMX")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert - verify response doesn't contain 'id' fields
        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).doesNotContain("\"id\"");
    }

    @Test
    @DisplayName("should_ReturnStatus404_When_GameCodeDoesNotExist")
    void should_ReturnStatus404_When_GameCodeDoesNotExist() throws Exception {
        // Arrange
        when(gameService.getGameDetailByCode("INVALID"))
                .thenThrow(new ResourceNotFoundException("Game not found: INVALID"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/games/INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Game not found: INVALID")));
    }

    @Test
    @DisplayName("should_ReturnStatus200_When_GameCodeIsLowercase")
    void should_ReturnStatus200_When_GameCodeIsLowercase() throws Exception {
        // Arrange
        GameDetailResponse mockGameDetail = createMockGameDetail("MMX", "Mega Man X", 1);
        when(gameService.getGameDetailByCode("mmx")).thenReturn(mockGameDetail);

        // Act & Assert
        mockMvc.perform(get("/api/v1/games/mmx")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("MMX")));
    }

    // Helper methods

    private GameDetailResponse createMockGameDetail(String code, String title, Integer releaseOrder) {
        List<StageResponse> stages = List.of(
                new StageResponse(
                        "stage-1",
                        "Stage 1",
                        1,
                        "image-key",
                        new BossResponse("boss-1", "Boss 1", "boss-image"),
                        new WeaponResponse("weapon-1", "Weapon 1", "Description", "weapon-image"),
                        new ArrayList<>()
                )
        );
        return new GameDetailResponse(code, title, releaseOrder, stages);
    }
}




