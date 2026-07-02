package com.danyaell.mavericklabsbe.game.repository;

import com.danyaell.mavericklabsbe.game.entity.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Long> {
    List<Weapon> findByGameId(Long gameId);
}

