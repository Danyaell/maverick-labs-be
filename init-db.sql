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
    base_difficulty INT NOT NULL,
    estimated_minutes INT NOT NULL,
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
    weakness_weapon VARCHAR(100) NOT NULL,
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

CREATE TABLE IF NOT EXISTS collectible_requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collectible_id BIGINT NOT NULL,
    requirement_type VARCHAR(50) NOT NULL,
    required_key VARCHAR(100) NOT NULL,
    CONSTRAINT fk_collectible_requirement FOREIGN KEY (collectible_id) REFERENCES collectibles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- INSERT DATA INTO TABLE games
INSERT INTO games (code, title, release_order) VALUES
('MMX', 'Mega Man X', 1),
('MMX2', 'Mega Man X2', 2),
('MMX3', 'Mega Man X3', 3),
('MMX4', 'Mega Man X4', 4),
('MMX5', 'Mega Man X5', 5),
('MMX6', 'Mega Man X6', 6),
('MMX7', 'Mega Man X7', 7),
('MMX8', 'Mega Man X8', 8);

-- INSERT DATA INTO TABLE stages (for MMX)
INSERT INTO stages (game_id, slug, name, stage_order, base_difficulty, estimated_minutes, image_asset_key) VALUES
(1, 'chill-penguin', 'Chill Penguin Stage', 1, 2, 5,'mmx.stage.chill-penguin'),
(1, 'storm-eagle', 'Storm Eagle Stage', 2, 3, 6, 'mmx.stage.storm-eagle'),
(1, 'flame-mammoth', 'Flame Mammoth Stage', 3, 4, 6, 'mmx.stage.flame-mammoth'),
(1, 'spark-mandrill', 'Spark Mandrill Stage', 4, 6, 7, 'mmx.stage.spark-mandrill'),
(1, 'armored-armadillo', 'Armored Armadillo Stage', 5, 5, 7, 'mmx.stage.armored-armadillo'),
(1, 'launch-octopus', 'Launch Octopus Stage', 6, 7, 8, 'mmx.stage.launch-octopus'),
(1, 'boomer-kuwanger', 'Boomer Kuwanger Stage', 7, 7, 8, 'mmx.stage.boomer-kuwanger'),
(1, 'sting-chameleon', 'Sting Chameleon Stage', 8, 6, 8, 'mmx.stage.sting-chameleon');

-- INSERT DATA INTO TABLE bosses
INSERT INTO bosses (stage_id, slug, name, image_asset_key, weakness_weapon) VALUES
(1, 'chill-penguin', 'Chill Penguin', 'mmx.boss.chill-penguin', 'fire-wave'),
(2, 'storm-eagle', 'Storm Eagle', 'mmx.boss.storm-eagle', 'chameleon-sting'),
(3, 'flame-mammoth', 'Flame Mammoth', 'mmx.boss.flame-mammoth', 'storm-tornado'),
(4, 'spark-mandrill', 'Spark Mandrill', 'mmx.boss.spark-mandrill', 'shotgun-ice'),
(5, 'armored-armadillo', 'Armored Armadillo', 'mmx.boss.armored-armadillo', 'electric-spark'),
(6, 'launch-octopus', 'Launch Octopus', 'mmx.boss.launch-octopus', 'rolling-shield'),
(7, 'boomer-kuwanger', 'Boomer Kuwanger', 'mmx.boss.boomer-kuwanger', 'homing-torpedo'),
(8, 'sting-chameleon', 'Sting Chameleon', 'mmx.boss.sting-chameleon', 'boomerang-cutter');

-- INSERT DATA INTO TABLE weapons
INSERT INTO weapons (game_id, obtained_from_stage_id, slug, name, description, image_asset_key) VALUES
(1, 1, 'shotgun-ice', 'Shotgun Ice', 'Fires ice projectiles.', 'mmx.weapon.shotgun-ice'),
(1, 2, 'storm-tornado', 'Storm Tornado', 'Creates a tornado attack.', 'mmx.weapon.storm-tornado'),
(1, 3, 'fire-wave', 'Fire Wave', 'Creates a fire attack.', 'mmx.weapon.fire-wave'),
(1, 4, 'electric-spark', 'Electric Spark', 'Fires electrical bolts.', 'mmx.weapon.electric-spark'),
(1, 5, 'rolling-shield', 'Rolling Shield', 'Provides a protective barrier.', 'mmx.weapon.rolling-shield'),
(1, 6, 'homing-torpedo', 'Homing Torpedo', 'Fires a homing projectile.', 'mmx.weapon.homing-torpedo'),
(1, 7, 'boomerang-cutter', 'Boomerang Cutter', 'Fires a boomerang attack.', 'mmx.weapon.boomerang-cutter'),
(1, 8, 'chameleon-sting', 'Chameleon Sting', 'Fires a sting attack.', 'mmx.weapon.chameleon-sting');

-- INSERT DATA INTO TABLE collectibles
INSERT INTO collectibles (stage_id, slug, name, type, description, image_asset_key, sort_order) VALUES
(1, 'chill-penguin-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(1, 'leg-upgrade-capsule', 'Leg Upgrade', 'ARMOR_UPGRADE', 'Unlocks dash movement.', 'mmx.collectible.armor-upgrade', 2),
(2, 'storm-eagle-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(2, 'helmet-upgrade-capsule', 'Helmet Upgrade', 'ARMOR_UPGRADE', 'Provides the ability to destroy blocks.', 'mmx.collectible.armor-upgrade', 2),
(2, 'storm-eagle-sub-tank', 'Sub Tank', 'SUB_TANK', 'Provides extra health reserve.', 'mmx.collectible.sub-tank', 3),
(3, 'flame-mammoth-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(3, 'flame-mammoth-sub-tank', 'Sub Tank', 'SUB_TANK', 'Provides extra health reserve.', 'mmx.collectible.sub-tank', 2),
(3, 'x-buster-upgrade-capsule', 'X Buster Upgrade', 'ARMOR_UPGRADE', 'Allows stronger charged shots and charged special weapons.', 'mmx.collectible.armor-upgrade', 3),
(4, 'spark-mandrill-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(4, 'spark-mandrill-sub-tank', 'Sub Tank', 'SUB_TANK', 'Provides extra health reserve.', 'mmx.collectible.sub-tank', 2),
(5, 'armored-armadillo-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(5, 'armored-armadillo-sub-tank', 'Sub Tank', 'SUB_TANK', 'Provides extra health reserve.', 'mmx.collectible.sub-tank', 2),
(5, 'armored-armadillo-hadouken', 'Hadouken', 'OTHER', 'Provides a powerful projectile attack.', 'mmx.collectible.hadouken', 3),
(6, 'launch-octopus-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(7, 'boomer-kuwanger-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(8, 'sting-chameleon-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(8, 'armor-upgrade-capsule', 'Armor Upgrade', 'ARMOR_UPGRADE', 'Increases defenses.', 'mmx.collectible.armor-upgrade', 1);

-- INSERT DATA INTO TABLE collectible_requirements
INSERT INTO collectible_requirements (collectible_id, requirement_type, required_key) VALUES
    -- Chill Penguin Heart Tank
    -- Requires Fire Wave to destroy the igloo.
    (1, 'WEAPON', 'fire-wave'),

    -- Storm Eagle Heart Tank
    -- Requires dash jump.
    (3, 'COLLECTIBLE', 'leg-upgrade-capsule'),

    -- Helmet Upgrade Capsule
    -- Requires dash jump.
    (4, 'COLLECTIBLE', 'leg-upgrade-capsule'),

    -- Flame Mammoth Heart Tank
    -- Intended route requires Chill Penguin cleared so the lava/fire state changes.
    (6, 'STAGE_CLEAR', 'chill-penguin'),

    -- Flame Mammoth Sub Tank
    -- Requires dash movement.
    (7, 'COLLECTIBLE', 'leg-upgrade-capsule'),

    -- X Buster Upgrade Capsule
    -- Requires dash + helmet in Flame Mammoth stage.
    (8, 'COLLECTIBLE', 'leg-upgrade-capsule'),
    (8, 'COLLECTIBLE', 'helmet-upgrade-capsule'),

    -- Spark Mandrill Heart Tank
    -- Can also be obtained with Boomerang Cutter, but dash boots are the cleaner canonical requirement.
    (9, 'COLLECTIBLE', 'leg-upgrade-capsule'),

    -- Spark Mandrill Sub Tank
    -- Requires Boomerang Cutter.
    (10, 'WEAPON', 'boomerang-cutter'),

    -- Boomer Kuwanger Heart Tank
    -- Requires Boomerang Cutter.
    (15, 'WEAPON', 'boomerang-cutter'),

    -- Sting Chameleon Heart Tank
    -- Requires dash movement to reach the platform after breaking the wall.
    (16, 'COLLECTIBLE', 'leg-upgrade-capsule'),

    -- Armor Upgrade Capsule
    -- Requires dash movement to reach the upper mini-boss area.
    (17, 'COLLECTIBLE', 'leg-upgrade-capsule'),

    -- Hadouken
    -- Requires all Heart Tanks.
    (13, 'COLLECTIBLE', 'chill-penguin-heart-tank'),
    (13, 'COLLECTIBLE', 'storm-eagle-heart-tank'),
    (13, 'COLLECTIBLE', 'flame-mammoth-heart-tank'),
    (13, 'COLLECTIBLE', 'spark-mandrill-heart-tank'),
    (13, 'COLLECTIBLE', 'armored-armadillo-heart-tank'),
    (13, 'COLLECTIBLE', 'launch-octopus-heart-tank'),
    (13, 'COLLECTIBLE', 'boomer-kuwanger-heart-tank'),
    (13, 'COLLECTIBLE', 'sting-chameleon-heart-tank'),
    -- Requires all Sub Tanks.
    (13, 'COLLECTIBLE', 'storm-eagle-sub-tank'),
    (13, 'COLLECTIBLE', 'flame-mammoth-sub-tank'),
    (13, 'COLLECTIBLE', 'spark-mandrill-sub-tank'),
    (13, 'COLLECTIBLE', 'armored-armadillo-sub-tank'),
    -- Requires all Armor Upgrades.
    (13, 'COLLECTIBLE', 'leg-upgrade-capsule'),
    (13, 'COLLECTIBLE', 'helmet-upgrade-capsule'),
    (13, 'COLLECTIBLE', 'x-buster-upgrade-capsule'),
    (13, 'COLLECTIBLE', 'armor-upgrade-capsule'),
    -- Requires all boss weapons / all 8 Mavericks defeated.
    (13, 'WEAPON', 'shotgun-ice'),
    (13, 'WEAPON', 'storm-tornado'),
    (13, 'WEAPON', 'fire-wave'),
    (13, 'WEAPON', 'electric-spark'),
    (13, 'WEAPON', 'rolling-shield'),
    (13, 'WEAPON', 'homing-torpedo'),
    (13, 'WEAPON', 'boomerang-cutter'),
    (13, 'WEAPON', 'chameleon-sting');