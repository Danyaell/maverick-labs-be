package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Severity used to prioritize and present a recommendation.")
public enum RecommendationSeverity {
	INFO,
	WARNING,
	SUCCESS
}