package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.GameSummaryResponse;
import com.danyaell.mavericklabsbe.game.entity.Game;
import com.danyaell.mavericklabsbe.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public List<GameSummaryResponse> getAllGames() {
        return gameRepository.findAllByOrderByReleaseOrderAsc()
                .stream()
                .map(this::mapToGameSummaryResponse)
                .collect(Collectors.toList());
    }

    private GameSummaryResponse mapToGameSummaryResponse(Game game) {
        return new GameSummaryResponse(
                game.getCode(),
                game.getTitle(),
                game.getReleaseOrder()
        );
    }
}
