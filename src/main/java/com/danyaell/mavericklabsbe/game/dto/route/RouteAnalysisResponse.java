package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Complete result produced by the route-analysis engine.")
public record RouteAnalysisResponse(

        @Schema(
                description = "Canonical game code used for the analysis.",
                example = "MMX"
        )
        String gameCode,

        @Schema(
                description = """
                        Modeled combat difficulty after applying any boss
                        weaknesses available at each point in the route.
                        """,
                example = "47",
                minimum = "0",
                maximum = "100"
        )
        Integer difficultyScore,

        @Schema(
                description = "Human-readable difficulty band derived from the difficulty score.",
                example = "MEDIUM"
        )
        DifficultyLabel difficultyLabel,

        @Schema(
                description = """
                        Modeled backtracking pressure caused by collectible
                        requirements that are unavailable on the first visit.
                        """,
                example = "80",
                minimum = "0",
                maximum = "100"
        )
        Integer backtrackingScore,

        @Schema(
                description = """
                        Modeled route duration in minutes. This value is not a
                        speedrun prediction or a guaranteed completion time.
                        """,
                example = "140",
                minimum = "0"
        )
        Integer estimatedMinutes,

        @Schema(
                description = "Collectible requirements that may require revisiting a stage."
        )
        List<RouteWarningResponse> warnings,

        @Schema(
                description = "Detailed score contributions produced by the simulation."
        )
        RouteBreakdownResponse breakdown,

        @Schema(
                description = """
                        Rule-based recommendations generated and prioritized
                        from the analyzed route.
                        """
        )
        List<RouteRecommendationResponse> recommendations

) {
}