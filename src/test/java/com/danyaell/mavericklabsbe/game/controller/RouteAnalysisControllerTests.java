package com.danyaell.mavericklabsbe.game.controller;

import com.danyaell.mavericklabsbe.common.exception.GlobalExceptionHandler;
import com.danyaell.mavericklabsbe.game.dto.*;
import com.danyaell.mavericklabsbe.game.exception.InvalidRouteException;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.service.RouteAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("RouteAnalysisController Tests")
class RouteAnalysisControllerTests {

    private MockMvc mockMvc;

    @Mock
    private RouteAnalysisService routeAnalysisService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        RouteAnalysisController controller = new RouteAnalysisController(routeAnalysisService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("should_Return200_When_RequestIsValid")
    void should_Return200_When_RequestIsValid() throws Exception {
        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin", "spark-mandrill", "storm-eagle"),
                RouteGoal.HUNDRED_PERCENT
        );
        RouteAnalysisResponse response = new RouteAnalysisResponse(
                "MMX",
                62,
                DifficultyLabel.MEDIUM,
                20,
                38,
                List.of(new RouteWarningResponse(
                        RouteWarningType.MISSING_REQUIREMENT,
                        "flame-mammoth",
                        "flame-mammoth-heart-tank",
                        "Missing requirement"
                )),
                new RouteBreakdownResponse(40, 10, 5, 3)
        );
        when(routeAnalysisService.analyzeRoute(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/routes/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameCode", is("MMX")))
                .andExpect(jsonPath("$.difficultyScore", is(62)))
                .andExpect(jsonPath("$.backtrackingScore", is(20)))
                .andExpect(jsonPath("$.estimatedMinutes", is(38)));
    }

    @Test
    @DisplayName("should_Return400_When_GameCodeIsMissing")
    void should_Return400_When_GameCodeIsMissing() throws Exception {
        String invalidRequest = """
                {
                  "stageOrder": ["chill-penguin", "spark-mandrill", "storm-eagle"],
                  "goal": "HUNDRED_PERCENT"
                }
                """;

        mockMvc.perform(post("/api/v1/routes/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should_Return400_When_StageOrderIsEmpty")
    void should_Return400_When_StageOrderIsEmpty() throws Exception {
        String invalidRequest = """
                {
                  "gameCode": "MMX",
                  "stageOrder": [],
                  "goal": "HUNDRED_PERCENT"
                }
                """;

        mockMvc.perform(post("/api/v1/routes/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should_Return404_When_GameDoesNotExist")
    void should_Return404_When_GameDoesNotExist() throws Exception {
        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "UNKNOWN",
                List.of("chill-penguin"),
                RouteGoal.HUNDRED_PERCENT
        );
        when(routeAnalysisService.analyzeRoute(request))
                .thenThrow(new ResourceNotFoundException("Game not found: UNKNOWN"));

        mockMvc.perform(post("/api/v1/routes/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("should_Return400_When_DuplicatedStagesAreProvided")
    void should_Return400_When_DuplicatedStagesAreProvided() throws Exception {
        AnalyzeRouteRequest request = new AnalyzeRouteRequest(
                "MMX",
                List.of("chill-penguin", "chill-penguin", "storm-eagle"),
                RouteGoal.HUNDRED_PERCENT
        );
        when(routeAnalysisService.analyzeRoute(request))
                .thenThrow(new InvalidRouteException("Duplicated stage in route: chill-penguin"));

        mockMvc.perform(post("/api/v1/routes/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Duplicated stage in route: chill-penguin")));
    }
}
