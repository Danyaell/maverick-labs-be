package com.danyaell.mavericklabsbe.game.dto.route;

public record RouteWarningResponse(
	RouteWarningType type,
	String message,
	String stageSlug,
	String collectibleSlug
) {}
