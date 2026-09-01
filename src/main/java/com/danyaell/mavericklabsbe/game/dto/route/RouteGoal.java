package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                Route-completion goal. HUNDRED_PERCENT currently requires every
                modeled stage for the requested game exactly once.
                """
)
public enum RouteGoal {
    HUNDRED_PERCENT
}