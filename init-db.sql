-- CREATE DB
CREATE DATABASE IF NOT EXISTS maverick_labs;
USE maverick_labs;

-- CREATE TABLE games
CREATE TABLE IF NOT EXISTS games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    release_order INT NOT NULL,
    CONSTRAINT uk_code UNIQUE KEY (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE stages
CREATE TABLE IF NOT EXISTS stages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    slug VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    stage_order INT NOT NULL,
    image_asset_key VARCHAR(255),
    CONSTRAINT fk_stage_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE bosses
CREATE TABLE IF NOT EXISTS bosses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stage_id BIGINT NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    image_asset_key VARCHAR(255),
    CONSTRAINT fk_boss_stage FOREIGN KEY (stage_id) REFERENCES stages(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- INSERT DATA INTO TABLE games
INSERT INTO games (code, title, release_order) VALUES
('MMX', 'Mega Man X', 1),
('MMX2', 'Mega Man X2', 2),
('MMX3', 'Mega Man X3', 3),
('MMX4', 'Mega Man X4', 4),
('MMX5', 'Mega Man X5', 5);

-- INSERT DATA INTO TABLE stages (for MMX)
INSERT INTO stages (game_id, slug, name, stage_order, image_asset_key) VALUES
(1, 'chill-penguin', 'Chill Penguin Stage', 1, 'mmx.stage.chill-penguin'),
(1, 'storm-eagle', 'Storm Eagle Stage', 2, 'mmx.stage.storm-eagle'),
(1, 'flame-mammoth', 'Flame Mammoth Stage', 3, 'mmx.stage.flame-mammoth'),
(1, 'spark-mandrill', 'Spark Mandrill Stage', 4, 'mmx.stage.spark-mandrill');

-- INSERT DATA INTO TABLE bosses
INSERT INTO bosses (stage_id, slug, name, image_asset_key) VALUES
(1, 'chill-penguin', 'Chill Penguin', 'mmx.boss.chill-penguin'),
(2, 'storm-eagle', 'Storm Eagle', 'mmx.boss.storm-eagle'),
(3, 'flame-mammoth', 'Flame Mammoth', 'mmx.boss.flame-mammoth'),
(4, 'spark-mandrill', 'Spark Mandrill', 'mmx.boss.spark-mandrill');

-- INSERT DATA INTO TABLE weapons
INSERT INTO weapons (game_id, obtained_from_stage_id, slug, name, description, image_asset_key) VALUES
(1, 1, 'shotgun-ice', 'Shotgun Ice', 'Fires ice projectiles.', 'mmx.weapon.shotgun-ice'),
(1, 2, 'storm-tornado', 'Storm Tornado', 'Creates a tornado attack.', 'mmx.weapon.storm-tornado'),
(1, 3, 'flame-wave', 'Flame Wave', 'Creates a fire attack.', 'mmx.weapon.flame-wave'),
(1, 4, 'electric-spark', 'Electric Spark', 'Fires electrical bolts.', 'mmx.weapon.electric-spark');

-- INSERT DATA INTO TABLE collectibles
INSERT INTO collectibles (stage_id, slug, name, type, description, image_asset_key, sort_order) VALUES
(1, 'chill-penguin-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(1, 'leg-upgrade-capsule', 'Leg Upgrade', 'ARMOR_UPGRADE', 'Unlocks dash movement.', 'mmx.collectible.leg-upgrade', 2),
(2, 'storm-eagle-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(2, 'helmet-upgrade-capsule', 'Helmet Upgrade', 'ARMOR_UPGRADE', 'Provides the ability to destroy blocks.', 'mmx.collectible.armor-upgrade', 2),
(2, 'storm-eagle-sub-tank', 'Sub Tank', 'SUB_TANK', 'Provides extra health reserve.', 'mmx.collectible.sub-tank', 3),
(3, 'flame-mammoth-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(3, 'flame-mammoth-sub-tank', 'Sub Tank', 'SUB_TANK', 'Provides extra health reserve.', 'mmx.collectible.sub-tank', 2),
(3, 'x-buster-upgrade-capsule', 'X Buster Upgrade', 'ARMOR_UPGRADE', 'Increases defense.', 'mmx.collectible.armor-upgrade', 3),
(4, 'spark-mandrill-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(4, 'spark-mandrill-sub-tank', 'Sub Tank', 'SUB_TANK', 'Provides extra health reserve.', 'mmx.collectible.sub-tank', 2);

