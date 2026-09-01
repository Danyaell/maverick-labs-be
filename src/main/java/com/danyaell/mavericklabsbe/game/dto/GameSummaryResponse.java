package com.danyaell.mavericklabsbe.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Summary of a game displayed in the Maverick Labs catalog.")
public class GameSummaryResponse {

    @Schema(
            description = "Stable game code.",
            example = "MMX"
    )
    private String code;

    @Schema(
            description = "Display title of the game.",
            example = "Mega Man X"
    )
    private String title;

    @Schema(
            description = "Release position inside the main Mega Man X series.",
            example = "1",
            minimum = "1"
    )
    private Integer releaseOrder;
}