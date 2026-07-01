package com.danyaell.mavericklabsbe.game.dto;

import java.util.List;

public record GameDetailResponse(
	String code,
	String title,
	Integer releaseOrder,
	List<StageResponse> stages
) {}


