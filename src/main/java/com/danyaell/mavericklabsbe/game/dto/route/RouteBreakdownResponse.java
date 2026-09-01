package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed contributions to the route-analysis scores.")
public record RouteBreakdownResponse(

        @Schema(
                description = "Average base difficulty of the submitted stages.",
                example = "67",
                minimum = "0",
                maximum = "100"
        )
        Integer baseDifficultyAverage,

        @Schema(
                description = """
                        Effective combat difficulty after applying weaknesses
                        that are available at the corresponding boss encounter.
                        """,
                example = "47",
                minimum = "0",
                maximum = "100"
        )
        Integer combatDifficulty,

        @Schema(
                description = """
                        Difference between base difficulty and effective combat
                        difficulty produced by available weaknesses.
                        """,
                example = "20",
                minimum = "0",
                maximum = "100"
        )
        Integer weaknessReduction,

        @Schema(
                description = """
                        Modeled route efficiency after considering backtracking,
                        difficult encounters, and available weaknesses.
                        """,
                example = "68",
                minimum = "0",
                maximum = "100"
        )
        Integer routeEfficiencyScore,

        @Schema(
                description = """
                        Additional modeled minutes produced by backtracking
                        pressure and difficult encounters without a weakness.
                        """,
                example = "20",
                minimum = "0"
        )
        Integer timePenaltyMinutes

) {
}