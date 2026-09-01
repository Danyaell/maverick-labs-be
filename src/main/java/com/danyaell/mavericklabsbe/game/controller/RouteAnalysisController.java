package com.danyaell.mavericklabsbe.game.controller;

import com.danyaell.mavericklabsbe.common.dto.ErrorResponse;
import com.danyaell.mavericklabsbe.config.openapi.OpenApiExamples;
import com.danyaell.mavericklabsbe.game.dto.route.AnalyzeRouteRequest;
import com.danyaell.mavericklabsbe.game.dto.route.RouteAnalysisResponse;
import com.danyaell.mavericklabsbe.game.service.RouteAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Tag(
		name = "Route Analysis",
		description = """
                Analyze MMX stage orders and receive modeled scores, warnings,
                breakdowns, and rule-based recommendations.
                """
)
public class RouteAnalysisController {

	private final RouteAnalysisService routeAnalysisService;

	@PostMapping("/analyze")
	@Operation(
			operationId = "analyzeRoute",
			summary = "Analyze a stage route",
			description = """
                    Validates and simulates an ordered MMX route.

                    Weapons become available after clearing their provider
                    stage. A boss weakness reduces difficulty only when the
                    corresponding weapon was acquired earlier in the route.

                    Unavailable collectible requirements add modeled
                    backtracking pressure. Estimated time is a model output,
                    not a speedrun prediction or guaranteed completion time.
                    """,
			requestBody =
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					description = """
                                    Complete MMX route. HUNDRED_PERCENT currently
                                    requires all eight modeled Maverick stages
                                    exactly once.
                                    """,
					content = @Content(
							mediaType =
									MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(
									implementation =
											AnalyzeRouteRequest.class
							),
							examples = @ExampleObject(
									name = "MMX hundred-percent route",
									summary =
											"Eight-stage MMX route",
									value =
											OpenApiExamples
													.ANALYZE_ROUTE_REQUEST
							)
					)
			)
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Route analyzed successfully.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(
									implementation =
											RouteAnalysisResponse.class
							),
							examples = @ExampleObject(
									name = "MMX route analysis",
									summary =
											"Representative analysis response",
									value =
											OpenApiExamples
													.ANALYZE_ROUTE_RESPONSE
							)
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = """
                            The request failed validation or the route contains
                            unknown, duplicated, or missing stages.
                            """,
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(
									implementation = ErrorResponse.class
							),
							examples = @ExampleObject(
									name = "Invalid route request",
									value =
											OpenApiExamples
													.BAD_REQUEST_RESPONSE
							)
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "The requested game code does not exist.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(
									implementation = ErrorResponse.class
							),
							examples = @ExampleObject(
									name = "Game not found",
									value =
											OpenApiExamples
													.NOT_FOUND_RESPONSE
							)
					)
			),
			@ApiResponse(
					responseCode = "500",
					description = "Unexpected server error.",
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(
									implementation = ErrorResponse.class
							),
							examples = @ExampleObject(
									name = "Unexpected server error",
									value =
											OpenApiExamples
													.INTERNAL_SERVER_ERROR_RESPONSE
							)
					)
			)
	})
	public ResponseEntity<RouteAnalysisResponse> analyzeRoute(
			@Valid @RequestBody AnalyzeRouteRequest request
	) {
		RouteAnalysisResponse response =
				routeAnalysisService.analyzeRoute(request);

		return ResponseEntity.ok(response);
	}
}