package com.danyaell.mavericklabsbe.game.repository;

import com.danyaell.mavericklabsbe.game.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    @Query("SELECT DISTINCT s FROM Stage s " +
            "LEFT JOIN FETCH s.boss b " +
            "LEFT JOIN FETCH s.collectibles c " +
            "WHERE s.game.id = :gameId " +
            "ORDER BY s.stageOrder ASC, c.sortOrder ASC")
    List<Stage> findByGameIdWithBossAndCollectibles(@Param("gameId") Long gameId);

    @Query("SELECT DISTINCT s FROM Stage s " +
            "LEFT JOIN FETCH s.boss b " +
            "LEFT JOIN FETCH s.collectibles c " +
            "LEFT JOIN FETCH c.requirements r " +
            "WHERE s.game.id = :gameId " +
            "ORDER BY s.stageOrder ASC, c.sortOrder ASC")
    List<Stage> findByGameIdWithAnalysisData(@Param("gameId") Long gameId);
}
