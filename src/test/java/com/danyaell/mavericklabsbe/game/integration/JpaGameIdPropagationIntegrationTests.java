package com.danyaell.mavericklabsbe.game.integration;

import com.danyaell.mavericklabsbe.game.entity.Boss;
import com.danyaell.mavericklabsbe.game.entity.Collectible;
import com.danyaell.mavericklabsbe.game.entity.CollectibleRequirement;
import com.danyaell.mavericklabsbe.game.entity.CollectibleType;
import com.danyaell.mavericklabsbe.game.entity.Game;
import com.danyaell.mavericklabsbe.game.entity.RequirementType;
import com.danyaell.mavericklabsbe.game.entity.Stage;
import com.danyaell.mavericklabsbe.game.entity.Weapon;
import com.danyaell.mavericklabsbe.support.MySqlIntegrationTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@MySqlIntegrationTest
@Transactional
class JpaGameIdPropagationIntegrationTests {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldPropagateAndPersistGameIdThroughJpaAssociations() {
        Game game = new Game();
        game.setCode("JPA-GAME-ID-TEST");
        game.setTitle("JPA Game ID Test");
        game.setReleaseOrder(9_000);

        entityManager.persist(game);

        Stage stage = new Stage();
        stage.setGame(game);
        stage.setSlug("jpa-test-stage");
        stage.setName("JPA Test Stage");
        stage.setStageOrder(1);
        stage.setBaseDifficulty(50);
        stage.setEstimatedMinutes(15);

        entityManager.persist(stage);

        Weapon weapon = new Weapon();
        weapon.setObtainedFromStage(stage);
        weapon.setSlug("jpa-test-weapon");
        weapon.setName("JPA Test Weapon");
        weapon.setDescription("Weapon created by the JPA integration test.");

        entityManager.persist(weapon);

        Boss boss = new Boss();
        boss.setStage(stage);
        boss.setSlug("jpa-test-boss");
        boss.setName("JPA Test Boss");
        boss.setWeaknessWeapon(weapon);

        entityManager.persist(boss);

        Collectible collectible = new Collectible();
        collectible.setStage(stage);
        collectible.setSlug("jpa-test-collectible");
        collectible.setName("JPA Test Collectible");
        collectible.setType(CollectibleType.ARMOR_UPGRADE);
        collectible.setDescription("Collectible created by the JPA integration test.");
        collectible.setSortOrder(1);

        entityManager.persist(collectible);

        CollectibleRequirement requirement = new CollectibleRequirement();
        requirement.setCollectible(collectible);
        requirement.setRequirementType(RequirementType.WEAPON);
        requirement.setRequiredWeapon(weapon);
        requirement.setDescription("Requires the JPA test weapon.");

        entityManager.persist(requirement);

        assertThat(weapon.getGame()).isSameAs(game);
        assertThat(boss.getGame()).isSameAs(game);
        assertThat(collectible.getGame()).isSameAs(game);
        assertThat(requirement.getGame()).isSameAs(game);

        entityManager.flush();

        Long expectedGameId = game.getId();
        Long stageId = stage.getId();
        Long weaponId = weapon.getId();
        Long bossId = boss.getId();
        Long collectibleId = collectible.getId();
        Long requirementId = requirement.getId();

        entityManager.clear();

        assertThat(storedGameId("stages", stageId))
                .isEqualTo(expectedGameId);

        assertThat(storedGameId("weapons", weaponId))
                .isEqualTo(expectedGameId);

        assertThat(storedGameId("bosses", bossId))
                .isEqualTo(expectedGameId);

        assertThat(storedGameId("collectibles", collectibleId))
                .isEqualTo(expectedGameId);

        assertThat(storedGameId("collectible_requirements", requirementId))
                .isEqualTo(expectedGameId);
    }

    private Long storedGameId(String table, Long entityId) {
        return jdbcTemplate.queryForObject(
                "SELECT game_id FROM " + table + " WHERE id = ?",
                Long.class,
                entityId
        );
    }
}