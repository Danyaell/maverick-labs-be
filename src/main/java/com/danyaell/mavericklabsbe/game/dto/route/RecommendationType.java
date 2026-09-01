package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rule family that generated a recommendation.")
public enum RecommendationType {
	BOSS_ORDER,
	BACKTRACKING,
	ROUTE_EFFICIENCY
}