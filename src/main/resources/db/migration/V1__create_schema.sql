CREATE TABLE games (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       code VARCHAR(50) NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       release_order INT NOT NULL,

                       PRIMARY KEY (id),

                       CONSTRAINT uk_games_code
                           UNIQUE (code),

                       CONSTRAINT uk_games_release_order
                           UNIQUE (release_order),

                       CONSTRAINT chk_games_code_not_blank
                           CHECK (CHAR_LENGTH(TRIM(code)) > 0),

                       CONSTRAINT chk_games_title_not_blank
                           CHECK (CHAR_LENGTH(TRIM(title)) > 0),

                       CONSTRAINT chk_games_release_order_positive
                           CHECK (release_order > 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE stages (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        game_id BIGINT NOT NULL,
                        slug VARCHAR(100) NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        stage_order INT NOT NULL,
                        image_asset_key VARCHAR(255),
                        base_difficulty INT NOT NULL DEFAULT 50,
                        estimated_minutes INT NOT NULL DEFAULT 15,

                        PRIMARY KEY (id),

                        CONSTRAINT uk_stages_game_slug
                            UNIQUE (game_id, slug),

                        CONSTRAINT uk_stages_game_order
                            UNIQUE (game_id, stage_order),

                        CONSTRAINT uk_stages_id_game
                            UNIQUE (id, game_id),

                        CONSTRAINT fk_stages_game
                            FOREIGN KEY (game_id)
                                REFERENCES games (id)
                                ON DELETE CASCADE,

                        CONSTRAINT chk_stages_slug_not_blank
                            CHECK (CHAR_LENGTH(TRIM(slug)) > 0),

                        CONSTRAINT chk_stages_name_not_blank
                            CHECK (CHAR_LENGTH(TRIM(name)) > 0),

                        CONSTRAINT chk_stages_order_positive
                            CHECK (stage_order > 0),

                        CONSTRAINT chk_stages_difficulty_range
                            CHECK (base_difficulty BETWEEN 0 AND 100),

                        CONSTRAINT chk_stages_estimated_minutes_positive
                            CHECK (estimated_minutes > 0),

                        CONSTRAINT chk_stages_image_asset_key
                            CHECK (
                                image_asset_key IS NULL
                                    OR CHAR_LENGTH(TRIM(image_asset_key)) > 0
                                )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE weapons (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         game_id BIGINT NOT NULL,
                         obtained_from_stage_id BIGINT,
                         slug VARCHAR(100) NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         description TEXT,
                         image_asset_key VARCHAR(255),

                         PRIMARY KEY (id),

                         CONSTRAINT uk_weapons_game_slug
                             UNIQUE (game_id, slug),

                         CONSTRAINT uk_weapons_id_game
                             UNIQUE (id, game_id),

    -- A stage can award at most one weapon. MySQL permits multiple NULLs,
    -- so non-stage weapons can still be represented later.
                         CONSTRAINT uk_weapons_obtained_stage
                             UNIQUE (obtained_from_stage_id),

                         KEY idx_weapons_obtained_stage_game (
        obtained_from_stage_id,
        game_id
    ),

                         CONSTRAINT fk_weapons_game
                             FOREIGN KEY (game_id)
                                 REFERENCES games (id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_weapons_obtained_stage_game
                             FOREIGN KEY (obtained_from_stage_id, game_id)
                                 REFERENCES stages (id, game_id)
                                 ON DELETE RESTRICT,

                         CONSTRAINT chk_weapons_slug_not_blank
                             CHECK (CHAR_LENGTH(TRIM(slug)) > 0),

                         CONSTRAINT chk_weapons_name_not_blank
                             CHECK (CHAR_LENGTH(TRIM(name)) > 0),

                         CONSTRAINT chk_weapons_image_asset_key
                             CHECK (
                                 image_asset_key IS NULL
                                     OR CHAR_LENGTH(TRIM(image_asset_key)) > 0
                                 )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE bosses (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        game_id BIGINT NOT NULL,
                        stage_id BIGINT NOT NULL,
                        slug VARCHAR(100) NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        image_asset_key VARCHAR(255),
                        weakness_weapon_id BIGINT,

                        PRIMARY KEY (id),

                        CONSTRAINT uk_bosses_stage
                            UNIQUE (stage_id),

                        CONSTRAINT uk_bosses_game_slug
                            UNIQUE (game_id, slug),

                        KEY idx_bosses_stage_game (
        stage_id,
        game_id
    ),

                        KEY idx_bosses_weakness_weapon_game (
        weakness_weapon_id,
        game_id
    ),

                        CONSTRAINT fk_bosses_stage_game
                            FOREIGN KEY (stage_id, game_id)
                                REFERENCES stages (id, game_id)
                                ON DELETE CASCADE,

                        CONSTRAINT fk_bosses_weakness_weapon_game
                            FOREIGN KEY (weakness_weapon_id, game_id)
                                REFERENCES weapons (id, game_id)
                                ON DELETE RESTRICT,

                        CONSTRAINT chk_bosses_slug_not_blank
                            CHECK (CHAR_LENGTH(TRIM(slug)) > 0),

                        CONSTRAINT chk_bosses_name_not_blank
                            CHECK (CHAR_LENGTH(TRIM(name)) > 0),

                        CONSTRAINT chk_bosses_image_asset_key
                            CHECK (
                                image_asset_key IS NULL
                                    OR CHAR_LENGTH(TRIM(image_asset_key)) > 0
                                )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE collectibles (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              game_id BIGINT NOT NULL,
                              stage_id BIGINT NOT NULL,
                              slug VARCHAR(100) NOT NULL,
                              name VARCHAR(255) NOT NULL,
                              type VARCHAR(50) NOT NULL,
                              description TEXT,
                              image_asset_key VARCHAR(255),
                              sort_order INT,

                              PRIMARY KEY (id),

                              CONSTRAINT uk_collectibles_stage_slug
                                  UNIQUE (stage_id, slug),

                              CONSTRAINT uk_collectibles_stage_sort_order
                                  UNIQUE (stage_id, sort_order),

                              CONSTRAINT uk_collectibles_id_game
                                  UNIQUE (id, game_id),

                              KEY idx_collectibles_stage_game (
        stage_id,
        game_id
    ),

                              CONSTRAINT fk_collectibles_stage_game
                                  FOREIGN KEY (stage_id, game_id)
                                      REFERENCES stages (id, game_id)
                                      ON DELETE CASCADE,

                              CONSTRAINT chk_collectibles_slug_not_blank
                                  CHECK (CHAR_LENGTH(TRIM(slug)) > 0),

                              CONSTRAINT chk_collectibles_name_not_blank
                                  CHECK (CHAR_LENGTH(TRIM(name)) > 0),

                              CONSTRAINT chk_collectibles_type
                                  CHECK (
                                      type IN (
                                               'HEART_TANK',
                                               'SUB_TANK',
                                               'ARMOR_UPGRADE',
                                               'WEAPON_UPGRADE',
                                               'RIDE_ARMOR',
                                               'PART',
                                               'LIFE_UP',
                                               'OTHER'
                                          )
                                      ),

                              CONSTRAINT chk_collectibles_sort_order
                                  CHECK (
                                      sort_order IS NULL
                                          OR sort_order > 0
                                      ),

                              CONSTRAINT chk_collectibles_image_asset_key
                                  CHECK (
                                      image_asset_key IS NULL
                                          OR CHAR_LENGTH(TRIM(image_asset_key)) > 0
                                      )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE TABLE collectible_requirements (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          game_id BIGINT NOT NULL,
                                          collectible_id BIGINT NOT NULL,
                                          requirement_type VARCHAR(50) NOT NULL,

                                          required_weapon_id BIGINT,
                                          required_collectible_id BIGINT,
                                          required_stage_id BIGINT,

                                          description TEXT,

                                          PRIMARY KEY (id),

                                          CONSTRAINT uk_requirements_collectible_weapon
                                              UNIQUE (collectible_id, required_weapon_id),

                                          CONSTRAINT uk_requirements_collectible_collectible
                                              UNIQUE (collectible_id, required_collectible_id),

                                          CONSTRAINT uk_requirements_collectible_stage
                                              UNIQUE (collectible_id, required_stage_id),

                                          KEY idx_requirements_collectible_game (
        collectible_id,
        game_id
    ),

                                          KEY idx_requirements_weapon_game (
        required_weapon_id,
        game_id
    ),

                                          KEY idx_requirements_required_collectible_game (
        required_collectible_id,
        game_id
    ),

                                          KEY idx_requirements_stage_game (
        required_stage_id,
        game_id
    ),

                                          CONSTRAINT fk_requirements_collectible_game
                                              FOREIGN KEY (collectible_id, game_id)
                                                  REFERENCES collectibles (id, game_id)
                                                  ON DELETE CASCADE,

                                          CONSTRAINT fk_requirements_weapon_game
                                              FOREIGN KEY (required_weapon_id, game_id)
                                                  REFERENCES weapons (id, game_id)
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT fk_requirements_required_collectible_game
                                              FOREIGN KEY (required_collectible_id, game_id)
                                                  REFERENCES collectibles (id, game_id)
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT fk_requirements_stage_game
                                              FOREIGN KEY (required_stage_id, game_id)
                                                  REFERENCES stages (id, game_id)
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT chk_requirements_type
                                              CHECK (
                                                  requirement_type IN (
                                                                       'WEAPON',
                                                                       'COLLECTIBLE',
                                                                       'STAGE_CLEARED',
                                                                       'OTHER'
                                                      )
                                                  ),

                                          CONSTRAINT chk_requirements_target
                                              CHECK (
                                                  (
                                                      requirement_type = 'WEAPON'
                                                          AND required_weapon_id IS NOT NULL
                                                          AND required_collectible_id IS NULL
                                                          AND required_stage_id IS NULL
                                                      )
                                                      OR
                                                  (
                                                      requirement_type = 'COLLECTIBLE'
                                                          AND required_weapon_id IS NULL
                                                          AND required_collectible_id IS NOT NULL
                                                          AND required_stage_id IS NULL
                                                      )
                                                      OR
                                                  (
                                                      requirement_type = 'STAGE_CLEARED'
                                                          AND required_weapon_id IS NULL
                                                          AND required_collectible_id IS NULL
                                                          AND required_stage_id IS NOT NULL
                                                      )
                                                      OR
                                                  (
                                                      requirement_type = 'OTHER'
                                                          AND required_weapon_id IS NULL
                                                          AND required_collectible_id IS NULL
                                                          AND required_stage_id IS NULL
                                                      )
                                                  ),

                                          CONSTRAINT chk_requirements_not_self_reference
                                              CHECK (
                                                  required_collectible_id IS NULL
                                                      OR required_collectible_id <> collectible_id
                                                  ),

                                          CONSTRAINT chk_requirements_other_description
                                              CHECK (
                                                  requirement_type <> 'OTHER'
                                                      OR (
                                                      description IS NOT NULL
                                                          AND CHAR_LENGTH(TRIM(description)) > 0
                                                      )
                                                  )
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;