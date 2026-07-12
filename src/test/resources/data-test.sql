-- Insert test data

-- INSERT DATA INTO TABLE games
INSERT INTO games (code, title, release_order) VALUES
('MMX', 'Mega Man X', 1),
('MMX2', 'Mega Man X2', 2),
('MMX3', 'Mega Man X3', 3),
('MMX4', 'Mega Man X4', 4);

-- INSERT DATA INTO TABLE stages (for MMX)
INSERT INTO stages (game_id, slug, name, stage_order, image_asset_key, base_difficulty, estimated_minutes) VALUES
(1, 'chill-penguin', 'Chill Penguin Stage', 1, 'mmx.stage.chill-penguin', 45, 12),
(1, 'spark-mandrill', 'Spark Mandrill Stage', 2, 'mmx.stage.spark-mandrill', 68, 16),
(1, 'storm-eagle', 'Storm Eagle Stage', 3, 'mmx.stage.storm-eagle', 50, 14);

-- INSERT DATA INTO TABLE bosses
INSERT INTO bosses (stage_id, slug, name, image_asset_key, weakness_weapon) VALUES
(1, 'chill-penguin', 'Chill Penguin', 'mmx.boss.chill-penguin', 'flame-wave'),
(2, 'spark-mandrill', 'Spark Mandrill', 'mmx.boss.spark-mandrill', 'shotgun-ice'),
(3, 'storm-eagle', 'Storm Eagle', 'mmx.boss.storm-eagle', 'electric-spark');

-- INSERT DATA INTO TABLE weapons
INSERT INTO weapons (game_id, obtained_from_stage_id, slug, name, description, image_asset_key) VALUES
(1, 1, 'shotgun-ice', 'Shotgun Ice', 'Fires ice projectiles.', 'mmx.weapon.shotgun-ice'),
(1, 2, 'electric-spark', 'Electric Spark', 'Fires electrical bolts.', 'mmx.weapon.electric-spark'),
(1, 3, 'storm-tornado', 'Storm Tornado', 'Creates a tornado attack.', 'mmx.weapon.storm-tornado');

-- INSERT DATA INTO TABLE collectibles
INSERT INTO collectibles (stage_id, slug, name, type, description, image_asset_key, sort_order) VALUES
(1, 'chill-penguin-heart-tank', 'Heart Tank', 'HEART_TANK', 'Increases maximum health.', 'mmx.collectible.heart-tank', 1),
(1, 'leg-upgrade-capsule', 'Leg Upgrade', 'ARMOR_UPGRADE', 'Unlocks dash movement.', 'mmx.collectible.leg-upgrade', 2),
(2, 'spark-mandrill-sub-tank', 'Sub Tank', 'SUB_TANK', 'Provides extra health reserve.', 'mmx.collectible.sub-tank', 1),
(3, 'storm-eagle-armor', 'Armor Upgrade', 'ARMOR_UPGRADE', 'Increases defense.', 'mmx.collectible.armor-upgrade', 1);

INSERT INTO collectible_requirements (collectible_id, requirement_type, required_key, description) VALUES
(4, 'COLLECTIBLE', 'leg-upgrade-capsule', 'Requires dash from leg upgrade.');
