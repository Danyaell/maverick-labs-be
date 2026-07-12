package com.danyaell.mavericklabsbe.game.dto;

public record RouteWarningResponse(
        RouteWarningType type,
        String stageSlug,
        String collectibleSlug,
        String message
) {
}
