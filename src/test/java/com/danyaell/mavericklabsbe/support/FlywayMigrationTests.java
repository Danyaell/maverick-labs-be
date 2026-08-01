package com.danyaell.mavericklabsbe.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MySqlIntegrationTest
@Transactional
class FlywayMigrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldApplySchemaAndSeedMigrations() {
        Long migrations = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE success = 1
                """,
                Long.class
        );

        assertThat(migrations).isEqualTo(2);
        assertThat(count("games")).isEqualTo(8);
        assertThat(count("stages")).isEqualTo(8);
        assertThat(count("bosses")).isEqualTo(8);
        assertThat(count("weapons")).isEqualTo(8);
        assertThat(count("collectibles")).isEqualTo(17);
        assertThat(count("collectible_requirements")).isEqualTo(39);
    }

    @Test
    void shouldRejectSecondWeaponForSameStage() {
        Long gameId = gameId("MMX");
        Long stageId = stageId(gameId, "chill-penguin");

        assertThatThrownBy(() -> jdbcTemplate.update(
                """
                INSERT INTO weapons (
                    game_id,
                    obtained_from_stage_id,
                    slug,
                    name
                ) VALUES (?, ?, ?, ?)
                """,
                gameId,
                stageId,
                "duplicate-stage-weapon",
                "Duplicate Stage Weapon"
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectBossWeaknessFromAnotherGame() {
        Long mmxGameId = gameId("MMX");
        Long mmx2GameId = gameId("MMX2");
        Long fireWaveId = weaponId(mmxGameId, "fire-wave");
        Long mmx2StageId = createStage(mmx2GameId, "constraint-test-stage", 1);

        assertThatThrownBy(() -> jdbcTemplate.update(
                """
                INSERT INTO bosses (
                    game_id,
                    stage_id,
                    slug,
                    name,
                    weakness_weapon_id
                ) VALUES (?, ?, ?, ?, ?)
                """,
                mmx2GameId,
                mmx2StageId,
                "constraint-test-boss",
                "Constraint Test Boss",
                fireWaveId
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectCollectibleWhoseGameDoesNotMatchItsStage() {
        Long mmxGameId = gameId("MMX");
        Long mmx2GameId = gameId("MMX2");
        Long mmxStageId = stageId(mmxGameId, "chill-penguin");

        assertThatThrownBy(() -> jdbcTemplate.update(
                """
                INSERT INTO collectibles (
                    game_id,
                    stage_id,
                    slug,
                    name,
                    type,
                    sort_order
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                mmx2GameId,
                mmxStageId,
                "cross-game-collectible",
                "Cross-game Collectible",
                "HEART_TANK",
                99
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectRequirementTargetFromAnotherGame() {
        Long mmxGameId = gameId("MMX");
        Long mmx2GameId = gameId("MMX2");
        Long legUpgradeId = collectibleId(mmxGameId, "leg-upgrade-capsule");
        Long mmx2StageId = createStage(mmx2GameId, "requirement-test-stage", 1);

        assertThatThrownBy(() -> jdbcTemplate.update(
                """
                INSERT INTO collectible_requirements (
                    game_id,
                    collectible_id,
                    requirement_type,
                    required_stage_id,
                    description
                ) VALUES (?, ?, ?, ?, ?)
                """,
                mmxGameId,
                legUpgradeId,
                "STAGE_CLEARED",
                mmx2StageId,
                "Invalid cross-game requirement"
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectWeaponRequirementWithoutWeaponTarget() {
        Long mmxGameId = gameId("MMX");
        Long legUpgradeId = collectibleId(mmxGameId, "leg-upgrade-capsule");

        assertThatThrownBy(() -> jdbcTemplate.update(
                """
                INSERT INTO collectible_requirements (
                    game_id,
                    collectible_id,
                    requirement_type,
                    description
                ) VALUES (?, ?, ?, ?)
                """,
                mmxGameId,
                legUpgradeId,
                "WEAPON",
                "Missing weapon target"
        ))
                .isInstanceOf(DataAccessException.class)
                .hasRootCauseInstanceOf(java.sql.SQLException.class)
                .hasMessageContaining("chk_requirements_target");
    }

    @Test
    void shouldRejectOtherRequirementWithBlankDescription() {
        Long mmxGameId = gameId("MMX");
        Long legUpgradeId = collectibleId(mmxGameId, "leg-upgrade-capsule");

        assertThatThrownBy(() -> jdbcTemplate.update(
                """
                INSERT INTO collectible_requirements (
                    game_id,
                    collectible_id,
                    requirement_type,
                    description
                ) VALUES (?, ?, ?, ?)
                """,
                mmxGameId,
                legUpgradeId,
                "OTHER",
                "   "
        ))
                .isInstanceOf(DataAccessException.class)
                .hasRootCauseInstanceOf(java.sql.SQLException.class)
                .hasMessageContaining("chk_requirements_other_description");
    }

    private Long count(String table) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + table,
                Long.class
        );
    }

    private Long gameId(String code) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM games WHERE code = ?",
                Long.class,
                code
        );
    }

    private Long stageId(Long gameId, String slug) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM stages WHERE game_id = ? AND slug = ?",
                Long.class,
                gameId,
                slug
        );
    }

    private Long weaponId(Long gameId, String slug) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM weapons WHERE game_id = ? AND slug = ?",
                Long.class,
                gameId,
                slug
        );
    }

    private Long collectibleId(Long gameId, String slug) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM collectibles WHERE game_id = ? AND slug = ?",
                Long.class,
                gameId,
                slug
        );
    }

    private Long createStage(Long gameId, String slug, int stageOrder) {
        jdbcTemplate.update(
                """
                INSERT INTO stages (
                    game_id,
                    slug,
                    name,
                    stage_order,
                    base_difficulty,
                    estimated_minutes
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                gameId,
                slug,
                "Constraint Test Stage",
                stageOrder,
                50,
                15
        );

        return stageId(gameId, slug);
    }
}