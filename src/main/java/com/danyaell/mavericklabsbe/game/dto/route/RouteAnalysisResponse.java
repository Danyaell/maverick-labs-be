package com.danyaell.mavericklabsbe.game.dto.route;

import java.util.List;

public record RouteAnalysisResponse(
	String gameCode,
	Integer difficultyScore,
	DifficultyLabel difficultyLabel,
	Integer backtrackingScore,
	Integer estimatedMinutes,
	List<RouteWarningResponse> warnings,
	RouteBreakdownResponse breakdown,
	List<RouteRecommendationResponse> recommendations
) {}
