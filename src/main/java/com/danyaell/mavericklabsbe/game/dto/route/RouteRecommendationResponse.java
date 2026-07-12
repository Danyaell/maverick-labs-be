package com.danyaell.mavericklabsbe.game.dto.route;

import java.util.List;

public record RouteRecommendationResponse(
	RecommendationType type,
	RecommendationSeverity severity,
	String message,
	List<String> relatedStages
) {}
