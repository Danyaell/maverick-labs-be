package com.danyaell.mavericklabsbe.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Modeled stage and its associated boss, reward, and collectibles.")
public record StageResponse(

        @Schema(
                description = "Stable URL-safe stage identifier.",
                example = "chill-penguin"
        )
        String slug,

        @Schema(
                description = "Display name of the stage.",
                example = "Chill Penguin Stage"
        )
        String name,

        @Schema(
                description = "Canonical display order inside the game.",
                example = "1",
                minimum = "1"
        )
        Integer stageOrder,

        @Schema(
                description = "Frontend asset-registry key for the stage image.",
                example = "mmx.stage.chill-penguin"
        )
        String imageAssetKey,

        @Schema(description = "Boss encountered in this stage.")
        BossResponse boss,

        @Schema(description = "Weapon obtained after clearing this stage.")
        WeaponResponse weaponReward,

        @Schema(
                description = "Collectibles located in this stage, ordered for display."
        )
        List<CollectibleResponse> collectibles

) {
}