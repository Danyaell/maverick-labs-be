package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.route.RouteWarningResponse;
import com.danyaell.mavericklabsbe.game.entity.Game;
import com.danyaell.mavericklabsbe.game.entity.Stage;
import com.danyaell.mavericklabsbe.game.entity.Weapon;

import java.util.List;
import java.util.Map;

public record RouteAnalysisContext(
	Game game,
	List<Stage> orderedStages,
	Map<String, Weapon> weaponsByObtainedStageSlug,
	List<RouteWarningResponse> warnings,
	Integer backtrackingScore
) {}
