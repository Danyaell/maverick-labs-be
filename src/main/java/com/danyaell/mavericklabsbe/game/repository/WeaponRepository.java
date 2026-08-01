package com.danyaell.mavericklabsbe.game.repository;

import com.danyaell.mavericklabsbe.game.entity.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Long> {
    @Query("""
    SELECT weapon
    FROM Weapon weapon
    LEFT JOIN FETCH weapon.obtainedFromStage
    WHERE weapon.game.id = :gameId
    """)
    List<Weapon> findByGameId(@Param("gameId") Long gameId);
}

