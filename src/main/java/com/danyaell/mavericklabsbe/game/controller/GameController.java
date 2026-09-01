package com.danyaell.mavericklabsbe.game.controller;

import com.danyaell.mavericklabsbe.common.dto.ErrorResponse;
import com.danyaell.mavericklabsbe.config.openapi.OpenApiExamples;
import com.danyaell.mavericklabsbe.game.dto.GameDetailResponse;
import com.danyaell.mavericklabsbe.game.dto.GameSummaryResponse;
import com.danyaell.mavericklabsbe.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Tag(
        name = "Games",
        description = "Browse the Mega Man X catalog and modeled game details."
)
public class GameController {

    private final GameService gameService;

    @GetMapping
    @Operation(
            operationId = "listGames",
            summary = "List games",
            description = """
                    Returns the eight main Mega Man X games ordered by their
                    release position. Games without modeled stage data are
                    still included in the catalog.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Catalog returned successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = GameSummaryResponse.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Unexpected server error",
                                    value = OpenApiExamples.INTERNAL_SERVER_ERROR_RESPONSE
                            )
                    )
            )
    })
    public ResponseEntity<List<GameSummaryResponse>> getAllGames() {
        List<GameSummaryResponse> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{gameCode}")
    @Operation(
            operationId = "getGameDetail",
            summary = "Get game details",
            description = """
                    Returns the modeled stages, bosses, weapon rewards, and
                    collectibles for a game. Game-code matching is
                    case-insensitive.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Game details returned successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = GameDetailResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The requested game code does not exist.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Game not found",
                                    value = OpenApiExamples.NOT_FOUND_RESPONSE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Unexpected server error",
                                    value = OpenApiExamples.INTERNAL_SERVER_ERROR_RESPONSE
                            )
                    )
            )
    })
    public ResponseEntity<GameDetailResponse> getGameDetail(
            @Parameter(
                    description = "Case-insensitive game code.",
                    example = "MMX",
                    required = true
            )
            @PathVariable String gameCode
    ) {
        GameDetailResponse gameDetail =
                gameService.getGameDetailByCode(gameCode);

        return ResponseEntity.ok(gameDetail);
    }
}