package com.danyaell.mavericklabsbe.game.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Human-readable difficulty band derived from the difficulty score.")
public enum DifficultyLabel {
    EASY,
    MEDIUM,
    HARD
}