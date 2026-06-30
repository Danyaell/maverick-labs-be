package com.danyaell.mavericklabsbe.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSummaryResponse {
    private String code;
    private String title;
    private Integer releaseOrder;
}
