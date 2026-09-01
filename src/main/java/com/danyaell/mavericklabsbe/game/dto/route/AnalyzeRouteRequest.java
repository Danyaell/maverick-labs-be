package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AnalyzeRouteRequest(
        @Schema(
                description = "Stable, case-insensitive game code.",
                example = "MMX"
        )
        @NotBlank(message = "gameCode is required")
        String gameCode,

        @Schema(
                description = """
                        Ordered stage slugs. For the current MMX HUNDRED_PERCENT
                        analysis, all eight modeled stages must appear exactly once.
                        """
        )
        @NotEmpty(message = "stageOrder cannot be empty")
        List<@NotBlank(message = "stageOrder cannot contain blank stage slugs") String> stageOrder,

        @Schema(
                description = "Route-completion goal.",
                example = "HUNDRED_PERCENT"
        )
        @NotNull(message = "goal is required")
        RouteGoal goal
) {
}
