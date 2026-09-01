package com.danyaell.mavericklabsbe.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public boss information associated with a stage.")
public record BossResponse(

        @Schema(
                description = "Stable URL-safe boss identifier.",
                example = "chill-penguin"
        )
        String slug,

        @Schema(
                description = "Display name of the boss.",
                example = "Chill Penguin"
        )
        String name,

        @Schema(
                description = "Frontend asset-registry key for the boss image.",
                example = "mmx.boss.chill-penguin"
        )
        String imageAssetKey

) {
}