package com.danyaell.mavericklabsbe.game.service;

import com.danyaell.mavericklabsbe.game.dto.*;
import com.danyaell.mavericklabsbe.game.entity.*;
import com.danyaell.mavericklabsbe.game.exception.ResourceNotFoundException;
import com.danyaell.mavericklabsbe.game.repository.GameRepository;
import com.danyaell.mavericklabsbe.game.repository.StageRepository;
import com.danyaell.mavericklabsbe.game.repository.WeaponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final StageRepository stageRepository;
    private final WeaponRepository weaponRepository;

    public List<GameSummaryResponse> getAllGames() {
        return gameRepository.findAllByOrderByReleaseOrderAsc()
                .stream()
                .map(this::mapToGameSummaryResponse)
                .collect(Collectors.toList());
    }

    public GameDetailResponse getGameDetailByCode(String gameCode) {
        if (gameCode == null || gameCode.isBlank()) {
            throw new ResourceNotFoundException("Game code cannot be empty");
        }

        Game game = gameRepository.findByCodeIgnoreCase(gameCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameCode));

        // Load stages with boss and collectibles to avoid lazy loading issues
        List<Stage> stages = stageRepository.findByGameIdWithBossAndCollectibles(game.getId());

        // Load weapons for this game and create a map by obtainedFromStageId
        List<Weapon> weapons = weaponRepository.findByGameId(game.getId());
        Map<Long, Weapon> weaponsByStageId = weapons.stream()
                .filter(w -> w.getObtainedFromStage() != null)
                .collect(Collectors.toMap(
                        w -> w.getObtainedFromStage().getId(),
                        w -> w
                ));

        // Map stages to DTOs
        List<StageResponse> stageResponses = stages.stream()
                .map(stage -> mapToStageResponse(stage, weaponsByStageId.get(stage.getId())))
                .collect(Collectors.toList());

        return new GameDetailResponse(
                game.getCode(),
                game.getTitle(),
                game.getReleaseOrder(),
                stageResponses
        );
    }

    private GameSummaryResponse mapToGameSummaryResponse(Game game) {
        return new GameSummaryResponse(
                game.getCode(),
                game.getTitle(),
                game.getReleaseOrder()
        );
    }

    private StageResponse mapToStageResponse(Stage stage, Weapon weaponReward) {
        BossResponse bossResponse = null;
        if (stage.getBoss() != null) {
            bossResponse = new BossResponse(
                    stage.getBoss().getSlug(),
                    stage.getBoss().getName(),
                    stage.getBoss().getImageAssetKey()
            );
        }

        WeaponResponse weaponResponse = null;
        if (weaponReward != null) {
            weaponResponse = new WeaponResponse(
                    weaponReward.getSlug(),
                    weaponReward.getName(),
                    weaponReward.getDescription(),
                    weaponReward.getImageAssetKey()
            );
        }

        List<CollectibleResponse> collectibles = stage.getCollectibles().stream()
                .sorted((c1, c2) -> {
                    Integer sort1 = c1.getSortOrder() != null ? c1.getSortOrder() : Integer.MAX_VALUE;
                    Integer sort2 = c2.getSortOrder() != null ? c2.getSortOrder() : Integer.MAX_VALUE;
                    return sort1.compareTo(sort2);
                })
                .map(this::mapToCollectibleResponse)
                .collect(Collectors.toList());

        return new StageResponse(
                stage.getSlug(),
                stage.getName(),
                stage.getStageOrder(),
                stage.getImageAssetKey(),
                bossResponse,
                weaponResponse,
                collectibles
        );
    }

    private CollectibleResponse mapToCollectibleResponse(Collectible collectible) {
        return new CollectibleResponse(
                collectible.getSlug(),
                collectible.getName(),
                collectible.getType().name(),
                collectible.getDescription(),
                collectible.getImageAssetKey(),
                collectible.getSortOrder()
        );
    }
}
