package com.danyaell.mavericklabsbe.game.repository;

import com.danyaell.mavericklabsbe.game.entity.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findAllByOrderByReleaseOrderAsc();
    Optional<Game> findByCodeIgnoreCase(String code);

    @EntityGraph(attributePaths = {
            "stages",
            "stages.boss",
            "stages.collectibles",
            "stages.collectibles.requirements",
            "weapons",
            "weapons.obtainedFromStage"
    })
    @Query("SELECT g FROM Game g WHERE LOWER(g.code) = LOWER(:code)")
    Optional<Game> findByCodeWithRouteData(@Param("code") String code);
}
