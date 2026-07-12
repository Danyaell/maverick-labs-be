package com.danyaell.mavericklabsbe.game.controller;

import com.danyaell.mavericklabsbe.common.exception.GlobalExceptionHandler;
import com.danyaell.mavericklabsbe.game.dto.route.*;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.service.RouteAnalysisService;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("RouteAnalysisController Contract Tests")
class RouteAnalysisControllerTests {

	private MockMvc mockMvc;

	@Mock
	private RouteAnalysisService routeAnalysisService;

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
		when(routeAnalysisService.analyzeRoute(org.mockito.ArgumentMatchers.any()))
				.thenReturn(response(List.of()));

		mockMvc.perform(post("/api/v1/routes/analyze")
						.contentType(MediaType.APPLICATION_JSON)
						.content(validPayload()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.gameCode").value("MMX"))
				.andExpect(jsonPath("$.difficultyScore").value(55))
				.andExpect(jsonPath("$.backtrackingScore").value(20))
				.andExpect(jsonPath("$.estimatedMinutes").value(46))
				.andExpect(jsonPath("$.recommendations").isArray());

		verify(routeAnalysisService).analyzeRoute(org.mockito.ArgumentMatchers.any());
	}

	@Test
	@DisplayName("should_Return400_When_GameCodeIsMissing")
	void should_Return400_When_GameCodeIsMissing() throws Exception {
		mockMvc.perform(post("/api/v1/routes/analyze")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "gameCode": "",
							  "stageOrder": ["chill-penguin"],
							  "goal": "HUNDRED_PERCENT"
							}
							"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	@DisplayName("should_Return400_When_StageOrderIsEmpty")
	void should_Return400_When_StageOrderIsEmpty() throws Exception {
		mockMvc.perform(post("/api/v1/routes/analyze")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "gameCode": "MMX",
							  "stageOrder": [],
							  "goal": "HUNDRED_PERCENT"
							}
							"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	@DisplayName("should_Return404_When_GameCodeIsInvalid")
	void should_Return404_When_GameCodeIsInvalid() throws Exception {
		when(routeAnalysisService.analyzeRoute(org.mockito.ArgumentMatchers.any()))
				.thenThrow(new ResourceNotFoundException("Game not found: INVALID"));

		mockMvc.perform(post("/api/v1/routes/analyze")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "gameCode": "INVALID",
							  "stageOrder": ["chill-penguin"],
							  "goal": "HUNDRED_PERCENT"
							}
							"""))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Game not found: INVALID"));
	}

	@Test
	@DisplayName("should_Return400_When_ServiceRejectsDuplicateStages")
	void should_Return400_When_ServiceRejectsDuplicateStages() throws Exception {
		when(routeAnalysisService.analyzeRoute(org.mockito.ArgumentMatchers.any()))
				.thenThrow(new IllegalArgumentException("stageOrder contains duplicate stage: chill-penguin"));

		mockMvc.perform(post("/api/v1/routes/analyze")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "gameCode": "MMX",
							  "stageOrder": ["chill-penguin", "chill-penguin"],
							  "goal": "HUNDRED_PERCENT"
							}
							"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	@DisplayName("should_ExposeRecommendationShape_When_ResponseContainsRecommendations")
	void should_ExposeRecommendationShape_When_ResponseContainsRecommendations() throws Exception {
		RouteRecommendationResponse recommendation = new RouteRecommendationResponse(
				RecommendationType.BACKTRACKING,
				RecommendationSeverity.WARNING,
				"You may need to revisit Flame Mammoth to collect all items.",
				List.of("flame-mammoth")
		);

		when(routeAnalysisService.analyzeRoute(org.mockito.ArgumentMatchers.any()))
				.thenReturn(response(List.of(recommendation)));

		mockMvc.perform(post("/api/v1/routes/analyze")
						.contentType(MediaType.APPLICATION_JSON)
						.content(validPayload()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.recommendations[0].type").value("BACKTRACKING"))
				.andExpect(jsonPath("$.recommendations[0].severity").value("WARNING"))
				.andExpect(jsonPath("$.recommendations[0].message").isString())
				.andExpect(jsonPath("$.recommendations[0].relatedStages[0]").value("flame-mammoth"));
	}

	private String validPayload() {
		return """
			{
			  "gameCode": "MMX",
			  "stageOrder": ["chill-penguin"],
			  "goal": "HUNDRED_PERCENT"
			}
			""";
	}

	private RouteAnalysisResponse response(List<RouteRecommendationResponse> recommendations) {
		return new RouteAnalysisResponse(
				"MMX",
				55,
				DifficultyLabel.MEDIUM,
				20,
				46,
				List.of(),
				new RouteBreakdownResponse(60, 20, 20, 6),
				recommendations
		);
	}
}
