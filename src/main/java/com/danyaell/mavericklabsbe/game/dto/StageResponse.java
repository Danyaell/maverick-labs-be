package com.danyaell.mavericklabsbe.game.dto;

import java.util.List;

public record StageResponse(
	String slug,
	String name,
	Integer stageOrder,
	String imageAssetKey,
	BossResponse boss,
	WeaponResponse weaponReward,
	List<CollectibleResponse> collectibles
) {}


