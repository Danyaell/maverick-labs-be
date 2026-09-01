package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category of warning detected during route simulation.")
public enum RouteWarningType {
	MISSING_REQUIREMENT
}