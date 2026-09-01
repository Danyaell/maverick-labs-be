package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Rule-based guidance generated from an analyzed route.")
public record RouteRecommendationResponse(

        @Schema(
                description = "Recommendation category.",
                example = "BOSS_ORDER"
        )
        RecommendationType type,

        @Schema(
                description = "Priority and presentation severity.",
                example = "WARNING"
        )
        RecommendationSeverity severity,

        @Schema(
                description = "Player-facing recommendation.",
                example = "Move Chill Penguin before Spark Mandrill to reduce difficulty because Chill Penguin gives you Shotgun Ice."
        )
        String message,

        @Schema(
                description = "Stage slugs involved in the recommendation.",
                example = "[\"chill-penguin\", \"spark-mandrill\"]"
        )
        List<String> relatedStages

) {
}