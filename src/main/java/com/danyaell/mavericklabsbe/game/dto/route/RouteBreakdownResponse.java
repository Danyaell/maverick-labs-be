package com.danyaell.mavericklabsbe.game.dto.route;

public record RouteBreakdownResponse(
	Integer baseDifficultyAverage,
	Integer combatDifficulty,
	Integer weaknessReduction,
	Integer routeEfficiencyScore,
	Integer timePenaltyMinutes
) {}
