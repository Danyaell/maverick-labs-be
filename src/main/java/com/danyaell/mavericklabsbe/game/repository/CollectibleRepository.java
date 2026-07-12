package com.danyaell.mavericklabsbe.game.repository;

import com.danyaell.mavericklabsbe.game.entity.Collectible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectibleRepository extends JpaRepository<Collectible, Long> {

    @Query("SELECT DISTINCT c FROM Collectible c " +
            "LEFT JOIN FETCH c.requirements r " +
            "WHERE c.stage.id IN :stageIds")
    List<Collectible> findByStageIdInWithRequirements(@Param("stageIds") List<Long> stageIds);
}
