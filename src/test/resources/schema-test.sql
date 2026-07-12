-- Create test database schema for H2

-- CREATE TABLE games
CREATE TABLE IF NOT EXISTS games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    release_order INT NOT NULL
);

-- CREATE TABLE stages
CREATE TABLE IF NOT EXISTS stages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    slug VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    stage_order INT NOT NULL,
    image_asset_key VARCHAR(255),
    base_difficulty INT NOT NULL DEFAULT 50,
    estimated_minutes INT NOT NULL DEFAULT 15,
    CONSTRAINT fk_stage_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

-- CREATE TABLE bosses
CREATE TABLE IF NOT EXISTS bosses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stage_id BIGINT NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    image_asset_key VARCHAR(255),
    weakness_weapon VARCHAR(100),
    CONSTRAINT fk_boss_stage FOREIGN KEY (stage_id) REFERENCES stages(id) ON DELETE CASCADE
);

-- CREATE TABLE weapons
CREATE TABLE IF NOT EXISTS weapons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    obtained_from_stage_id BIGINT,
    slug VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    image_asset_key VARCHAR(255),
    CONSTRAINT fk_weapon_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_weapon_stage FOREIGN KEY (obtained_from_stage_id) REFERENCES stages(id) ON DELETE SET NULL
);

-- CREATE TABLE collectibles
CREATE TABLE IF NOT EXISTS collectibles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stage_id BIGINT NOT NULL,
    slug VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description LONGTEXT,
    image_asset_key VARCHAR(255),
    sort_order INT,
    CONSTRAINT fk_collectible_stage FOREIGN KEY (stage_id) REFERENCES stages(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS collectible_requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collectible_id BIGINT NOT NULL,
    requirement_type VARCHAR(50) NOT NULL,
    required_key VARCHAR(100) NOT NULL,
    description LONGTEXT,
    CONSTRAINT fk_requirement_collectible FOREIGN KEY (collectible_id) REFERENCES collectibles(id) ON DELETE CASCADE
);
