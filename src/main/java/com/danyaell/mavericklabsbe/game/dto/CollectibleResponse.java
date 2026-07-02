package com.danyaell.mavericklabsbe.game.dto;

public record CollectibleResponse(
        String slug,
        String name,
        String type,
        String description,
        String imageAssetKey,
        Integer sortOrder
) {}
