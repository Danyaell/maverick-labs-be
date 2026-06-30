package com.danyaell.mavericklabsbe.game.fixture;

import com.danyaell.mavericklabsbe.game.dto.GameSummaryResponse;
import com.danyaell.mavericklabsbe.game.entity.Game;

import java.util.ArrayList;
import java.util.List;

public class GameTestFixture {

    public static Game createGame(String code, String title, Integer releaseOrder) {
        return new Game(null, code, title, releaseOrder);
    }

    public static Game createGameWithId(Long id, String code, String title, Integer releaseOrder) {
        return new Game(id, code, title, releaseOrder);
    }

    public static Game createMegaManX() {
        return createGame("MMX", "Mega Man X", 1);
    }

    public static Game createMegaManX2() {
        return createGame("MMX2", "Mega Man X2", 2);
    }

    public static Game createMegaManX3() {
        return createGame("MMX3", "Mega Man X3", 3);
    }

    public static List<Game> createDefaultGameList() {
        List<Game> games = new ArrayList<>();
        games.add(createGameWithId(1L, "MMX", "Mega Man X", 1));
        games.add(createGameWithId(2L, "MMX2", "Mega Man X2", 2));
        games.add(createGameWithId(3L, "MMX3", "Mega Man X3", 3));
        return games;
    }

    public static GameSummaryResponse createGameSummaryResponse(String code, String title, Integer releaseOrder) {
        return new GameSummaryResponse(code, title, releaseOrder);
    }

    public static List<GameSummaryResponse> createDefaultGameSummaryResponseList() {
        List<GameSummaryResponse> responses = new ArrayList<>();
        responses.add(createGameSummaryResponse("MMX", "Mega Man X", 1));
        responses.add(createGameSummaryResponse("MMX2", "Mega Man X2", 2));
        responses.add(createGameSummaryResponse("MMX3", "Mega Man X3", 3));
        return responses;
    }
}

