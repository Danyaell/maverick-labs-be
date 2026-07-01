package com.danyaell.mavericklabsbe.game.controller;

import com.danyaell.mavericklabsbe.game.dto.GameDetailResponse;
import com.danyaell.mavericklabsbe.game.dto.GameSummaryResponse;
import com.danyaell.mavericklabsbe.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<List<GameSummaryResponse>> getAllGames() {
        List<GameSummaryResponse> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{gameCode}")
    public ResponseEntity<GameDetailResponse> getGameDetail(@PathVariable String gameCode) {
        GameDetailResponse gameDetail = gameService.getGameDetailByCode(gameCode);
        return ResponseEntity.ok(gameDetail);
    }
}
