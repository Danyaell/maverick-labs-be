package com.danyaell.mavericklabsbe.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AnalyzeRouteRequest(
        @NotBlank(message = "gameCode is required")
        String gameCode,
        @NotEmpty(message = "stageOrder cannot be empty")
        List<@NotBlank(message = "stage slug cannot be blank") String> stageOrder,
        @NotNull(message = "goal is required")
        RouteGoal goal
) {
}
