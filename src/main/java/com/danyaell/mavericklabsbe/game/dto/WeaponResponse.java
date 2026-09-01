package com.danyaell.mavericklabsbe.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Weapon awarded after clearing a Maverick stage.")
public record WeaponResponse(

        @Schema(
                description = "Stable URL-safe weapon identifier.",
                example = "shotgun-ice"
        )
        String slug,

        @Schema(
                description = "Display name of the weapon.",
                example = "Shotgun Ice"
        )
        String name,

        @Schema(
                description = "Player-facing description of the weapon.",
                example = "Fires ice projectiles that split when they hit a target."
        )
        String description,

        @Schema(
                description = "Frontend asset-registry key for the weapon image.",
                example = "mmx.weapon.shotgun-ice"
        )
        String imageAssetKey

) {
}