package com.danyaell.mavericklabsbe.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@MySqlIntegrationTest
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

        Long games = count("games");
        Long stages = count("stages");
        Long bosses = count("bosses");
        Long weapons = count("weapons");
        Long collectibles = count("collectibles");
        Long requirements = count("collectible_requirements");

        assertThat(migrations).isEqualTo(2);
        assertThat(games).isEqualTo(5);
        assertThat(stages).isEqualTo(4);
        assertThat(bosses).isEqualTo(4);
        assertThat(weapons).isEqualTo(4);
        assertThat(collectibles).isEqualTo(10);
        assertThat(requirements).isEqualTo(2);
    }

    private Long count(String table) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + table,
                Long.class
        );
    }
}