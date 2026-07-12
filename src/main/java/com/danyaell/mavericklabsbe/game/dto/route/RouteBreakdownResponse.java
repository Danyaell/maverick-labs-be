package com.danyaell.mavericklabsbe.game.dto.route;

public record RouteBreakdownResponse(
	Integer bossDifficulty,
	Integer weaknessOptimization,
	Integer backtrackingPenalty,
	Integer timePenalty
) {}
