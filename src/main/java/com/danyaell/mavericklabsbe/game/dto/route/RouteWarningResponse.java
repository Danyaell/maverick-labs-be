package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Warning generated while simulating route progression.")
public record RouteWarningResponse(

		@Schema(
				description = "Category of route warning.",
				example = "MISSING_REQUIREMENT"
		)
		RouteWarningType type,

		@Schema(
				description = "Player-facing explanation of the warning.",
				example = "Collectible Sub Tank may require revisiting Spark Mandrill Stage later."
		)
		String message,

		@Schema(
				description = "Stage associated with the warning.",
				example = "spark-mandrill"
		)
		String stageSlug,

		@Schema(
				description = "Collectible whose requirement was unavailable.",
				example = "spark-mandrill-sub-tank"
		)
		String collectibleSlug

) {}