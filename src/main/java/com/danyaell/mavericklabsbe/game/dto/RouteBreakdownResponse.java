package com.danyaell.mavericklabsbe.game.dto;

public record RouteBreakdownResponse(
        Integer baseDifficulty,
        Integer bossDifficulty,
        Integer weaknessOptimization,
        Integer timePenalty
) {
}
