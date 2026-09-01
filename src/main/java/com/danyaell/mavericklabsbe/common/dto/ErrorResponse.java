package com.danyaell.mavericklabsbe.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @Schema(description = "HTTP status code.", example = "400")
    private Integer status;

    @Schema(
            description = "Safe client-facing error message.",
            example = "stageOrder cannot be empty"
    )
    private String message;
}

