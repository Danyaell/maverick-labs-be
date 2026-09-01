package com.danyaell.mavericklabsbe.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Collectible located in a modeled stage.")
public record CollectibleResponse(

        @Schema(
                description = "Stable URL-safe collectible identifier.",
                example = "leg-upgrade-capsule"
        )
        String slug,

        @Schema(
                description = "Display name of the collectible.",
                example = "Leg Upgrade"
        )
        String name,

        @Schema(
                description = "Public collectible category.",
                example = "ARMOR_UPGRADE"
        )
        String type,

        @Schema(
                description = "Player-facing description of the collectible.",
                example = "Unlocks dash movement and longer dash jumps."
        )
        String description,

        @Schema(
                description = "Frontend asset-registry key for the collectible image.",
                example = "mmx.collectible.leg-upgrade"
        )
        String imageAssetKey,

        @Schema(
                description = "Display order inside the stage.",
                example = "1",
                minimum = "1"
        )
        Integer sortOrder

) {
}