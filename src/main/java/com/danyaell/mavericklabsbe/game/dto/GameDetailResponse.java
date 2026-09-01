package com.danyaell.mavericklabsbe.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detailed modeled information for one game.")
public record GameDetailResponse(

        @Schema(
                description = "Stable game code.",
                example = "MMX"
        )
        String code,

        @Schema(
                description = "Display title of the game.",
                example = "Mega Man X"
        )
        String title,

        @Schema(
                description = "Release position inside the main Mega Man X series.",
                example = "1",
                minimum = "1"
        )
        Integer releaseOrder,

        @Schema(
                description = """
                        Modeled Maverick stages. Games whose detailed content
                        is not available yet return an empty collection.
                        """
        )
        List<StageResponse> stages

) {
}