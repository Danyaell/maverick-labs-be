INSERT INTO games (
    code,
    title,
    release_order
) VALUES
      ('MMX',  'Mega Man X',  1),
      ('MMX2', 'Mega Man X2', 2),
      ('MMX3', 'Mega Man X3', 3),
      ('MMX4', 'Mega Man X4', 4),
      ('MMX5', 'Mega Man X5', 5),
      ('MMX6', 'Mega Man X6', 6),
      ('MMX7', 'Mega Man X7', 7),
      ('MMX8', 'Mega Man X8', 8);


SET @mmx_game_id := (
    SELECT id
    FROM games
    WHERE code = 'MMX'
);


INSERT INTO stages (
    game_id,
    slug,
    name,
    stage_order,
    image_asset_key,
    base_difficulty,
    estimated_minutes
) VALUES
      (
          @mmx_game_id,
          'chill-penguin',
          'Chill Penguin Stage',
          1,
          'mmx.stage.chill-penguin',
          45,
          12
      ),
      (
          @mmx_game_id,
          'storm-eagle',
          'Storm Eagle Stage',
          2,
          'mmx.stage.storm-eagle',
          50,
          14
      ),
      (
          @mmx_game_id,
          'flame-mammoth',
          'Flame Mammoth Stage',
          3,
          'mmx.stage.flame-mammoth',
          70,
          18
      ),
      (
          @mmx_game_id,
          'spark-mandrill',
          'Spark Mandrill Stage',
          4,
          'mmx.stage.spark-mandrill',
          68,
          16
      ),
      (
          @mmx_game_id,
          'armored-armadillo',
          'Armored Armadillo Stage',
          5,
          'mmx.stage.armored-armadillo',
          70,
          14
      ),
      (
          @mmx_game_id,
          'launch-octopus',
          'Launch Octopus Stage',
          6,
          'mmx.stage.launch-octopus',
          80,
          16
      ),
      (
          @mmx_game_id,
          'boomer-kuwanger',
          'Boomer Kuwanger Stage',
          7,
          'mmx.stage.boomer-kuwanger',
          80,
          16
      ),
      (
          @mmx_game_id,
          'sting-chameleon',
          'Sting Chameleon Stage',
          8,
          'mmx.stage.sting-chameleon',
          70,
          14
      );


SET @chill_penguin_stage_id := (
    SELECT id
    FROM stages
    WHERE game_id = @mmx_game_id
      AND slug = 'chill-penguin'
);

SET @storm_eagle_stage_id := (
    SELECT id
    FROM stages
    WHERE game_id = @mmx_game_id
      AND slug = 'storm-eagle'
);

SET @flame_mammoth_stage_id := (
    SELECT id
    FROM stages
    WHERE game_id = @mmx_game_id
      AND slug = 'flame-mammoth'
);

SET @spark_mandrill_stage_id := (
    SELECT id
    FROM stages
    WHERE game_id = @mmx_game_id
      AND slug = 'spark-mandrill'
);

SET @armored_armadillo_stage_id := (
    SELECT id
    FROM stages
    WHERE game_id = @mmx_game_id
      AND slug = 'armored-armadillo'
);

SET @launch_octopus_stage_id := (
    SELECT id
    FROM stages
    WHERE game_id = @mmx_game_id
      AND slug = 'launch-octopus'
);

SET @boomer_kuwanger_stage_id := (
    SELECT id
    FROM stages
    WHERE game_id = @mmx_game_id
      AND slug = 'boomer-kuwanger'
);

SET @sting_chameleon_stage_id := (
    SELECT id
    FROM stages
    WHERE game_id = @mmx_game_id
      AND slug = 'sting-chameleon'
);


INSERT INTO weapons (
    game_id,
    obtained_from_stage_id,
    slug,
    name,
    description,
    image_asset_key
) VALUES
      (
          @mmx_game_id,
          @chill_penguin_stage_id,
          'shotgun-ice',
          'Shotgun Ice',
          'Fires ice projectiles that split when they hit a target.',
          'mmx.weapon.shotgun-ice'
      ),
      (
          @mmx_game_id,
          @storm_eagle_stage_id,
          'storm-tornado',
          'Storm Tornado',
          'Creates a horizontal tornado that damages enemies repeatedly.',
          'mmx.weapon.storm-tornado'
      ),
      (
          @mmx_game_id,
          @flame_mammoth_stage_id,
          'fire-wave',
          'Fire Wave',
          'Projects a continuous stream of fire.',
          'mmx.weapon.fire-wave'
      ),
      (
          @mmx_game_id,
          @spark_mandrill_stage_id,
          'electric-spark',
          'Electric Spark',
          'Fires an electrical charge that splits when it reaches a wall.',
          'mmx.weapon.electric-spark'
      ),
      (
          @mmx_game_id,
          @armored_armadillo_stage_id,
          'rolling-shield',
          'Rolling Shield',
          'Launches a rolling energy shield along the ground.',
          'mmx.weapon.rolling-shield'
      ),
      (
          @mmx_game_id,
          @launch_octopus_stage_id,
          'homing-torpedo',
          'Homing Torpedo',
          'Fires a torpedo that tracks nearby enemies.',
          'mmx.weapon.homing-torpedo'
      ),
      (
          @mmx_game_id,
          @boomer_kuwanger_stage_id,
          'boomerang-cutter',
          'Boomerang Cutter',
          'Throws a boomerang blade that can retrieve distant items.',
          'mmx.weapon.boomerang-cutter'
      ),
      (
          @mmx_game_id,
          @sting_chameleon_stage_id,
          'chameleon-sting',
          'Chameleon Sting',
          'Fires a beam that splits into three directions.',
          'mmx.weapon.chameleon-sting'
      );


SET @shotgun_ice_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'shotgun-ice'
);

SET @storm_tornado_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'storm-tornado'
);

SET @fire_wave_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'fire-wave'
);

SET @electric_spark_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'electric-spark'
);

SET @rolling_shield_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'rolling-shield'
);

SET @homing_torpedo_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'homing-torpedo'
);

SET @boomerang_cutter_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'boomerang-cutter'
);

SET @chameleon_sting_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'chameleon-sting'
);


INSERT INTO bosses (
    game_id,
    stage_id,
    slug,
    name,
    image_asset_key,
    weakness_weapon_id
) VALUES
      (
          @mmx_game_id,
          @chill_penguin_stage_id,
          'chill-penguin',
          'Chill Penguin',
          'mmx.boss.chill-penguin',
          @fire_wave_weapon_id
      ),
      (
          @mmx_game_id,
          @storm_eagle_stage_id,
          'storm-eagle',
          'Storm Eagle',
          'mmx.boss.storm-eagle',
          @chameleon_sting_weapon_id
      ),
      (
          @mmx_game_id,
          @flame_mammoth_stage_id,
          'flame-mammoth',
          'Flame Mammoth',
          'mmx.boss.flame-mammoth',
          @storm_tornado_weapon_id
      ),
      (
          @mmx_game_id,
          @spark_mandrill_stage_id,
          'spark-mandrill',
          'Spark Mandrill',
          'mmx.boss.spark-mandrill',
          @shotgun_ice_weapon_id
      ),
      (
          @mmx_game_id,
          @armored_armadillo_stage_id,
          'armored-armadillo',
          'Armored Armadillo',
          'mmx.boss.armored-armadillo',
          @electric_spark_weapon_id
      ),
      (
          @mmx_game_id,
          @launch_octopus_stage_id,
          'launch-octopus',
          'Launch Octopus',
          'mmx.boss.launch-octopus',
          @rolling_shield_weapon_id
      ),
      (
          @mmx_game_id,
          @boomer_kuwanger_stage_id,
          'boomer-kuwanger',
          'Boomer Kuwanger',
          'mmx.boss.boomer-kuwanger',
          @homing_torpedo_weapon_id
      ),
      (
          @mmx_game_id,
          @sting_chameleon_stage_id,
          'sting-chameleon',
          'Sting Chameleon',
          'mmx.boss.sting-chameleon',
          @boomerang_cutter_weapon_id
      );


INSERT INTO collectibles (
    game_id,
    stage_id,
    slug,
    name,
    type,
    description,
    image_asset_key,
    sort_order
) VALUES
      (
          @mmx_game_id,
          @chill_penguin_stage_id,
          'leg-upgrade-capsule',
          'Leg Upgrade',
          'ARMOR_UPGRADE',
          'Unlocks dash movement and longer dash jumps.',
          'mmx.collectible.leg-upgrade',
          1
      ),
      (
          @mmx_game_id,
          @chill_penguin_stage_id,
          'chill-penguin-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          2
      ),
      (
          @mmx_game_id,
          @storm_eagle_stage_id,
          'storm-eagle-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @mmx_game_id,
          @storm_eagle_stage_id,
          'helmet-upgrade-capsule',
          'Helmet Upgrade',
          'ARMOR_UPGRADE',
          'Allows X to break specific blocks with a headbutt.',
          'mmx.collectible.helmet-upgrade',
          2
      ),
      (
          @mmx_game_id,
          @storm_eagle_stage_id,
          'storm-eagle-sub-tank',
          'Sub Tank',
          'SUB_TANK',
          'Stores health energy for later use.',
          'mmx.collectible.sub-tank',
          3
      ),
      (
          @mmx_game_id,
          @flame_mammoth_stage_id,
          'flame-mammoth-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @mmx_game_id,
          @flame_mammoth_stage_id,
          'flame-mammoth-sub-tank',
          'Sub Tank',
          'SUB_TANK',
          'Stores health energy for later use.',
          'mmx.collectible.sub-tank',
          2
      ),
      (
          @mmx_game_id,
          @flame_mammoth_stage_id,
          'x-buster-upgrade-capsule',
          'X-Buster Upgrade',
          'ARMOR_UPGRADE',
          'Unlocks a higher X-Buster charge level and charged special weapons.',
          'mmx.collectible.x-buster-upgrade',
          3
      ),
      (
          @mmx_game_id,
          @spark_mandrill_stage_id,
          'spark-mandrill-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @mmx_game_id,
          @spark_mandrill_stage_id,
          'spark-mandrill-sub-tank',
          'Sub Tank',
          'SUB_TANK',
          'Stores health energy for later use.',
          'mmx.collectible.sub-tank',
          2
      ),
      (
          @mmx_game_id,
          @armored_armadillo_stage_id,
          'armored-armadillo-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @mmx_game_id,
          @armored_armadillo_stage_id,
          'armored-armadillo-sub-tank',
          'Sub Tank',
          'SUB_TANK',
          'Stores health energy for later use.',
          'mmx.collectible.sub-tank',
          2
      ),
      (
          @mmx_game_id,
          @armored_armadillo_stage_id,
          'armored-armadillo-hadouken',
          'Hadouken',
          'WEAPON_UPGRADE',
          'Secret technique that defeats most enemies and bosses in one hit.',
          'mmx.collectible.hadouken',
          3
      ),
      (
          @mmx_game_id,
          @launch_octopus_stage_id,
          'launch-octopus-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @mmx_game_id,
          @boomer_kuwanger_stage_id,
          'boomer-kuwanger-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @mmx_game_id,
          @sting_chameleon_stage_id,
          'sting-chameleon-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @mmx_game_id,
          @sting_chameleon_stage_id,
          'body-armor-upgrade-capsule',
          'Body Armor Upgrade',
          'ARMOR_UPGRADE',
          'Reduces damage taken by half.',
          'mmx.collectible.body-armor-upgrade',
          2
      );


SET @chill_penguin_heart_tank_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'chill-penguin-heart-tank'
);

SET @leg_upgrade_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'leg-upgrade-capsule'
);

SET @storm_eagle_heart_tank_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'storm-eagle-heart-tank'
);

SET @helmet_upgrade_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'helmet-upgrade-capsule'
);

SET @flame_mammoth_heart_tank_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'flame-mammoth-heart-tank'
);

SET @flame_mammoth_sub_tank_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'flame-mammoth-sub-tank'
);

SET @x_buster_upgrade_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'x-buster-upgrade-capsule'
);

SET @spark_mandrill_heart_tank_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'spark-mandrill-heart-tank'
);

SET @spark_mandrill_sub_tank_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'spark-mandrill-sub-tank'
);

SET @boomer_kuwanger_heart_tank_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'boomer-kuwanger-heart-tank'
);

SET @sting_chameleon_heart_tank_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'sting-chameleon-heart-tank'
);

SET @body_armor_upgrade_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'body-armor-upgrade-capsule'
);

SET @hadouken_id := (
    SELECT id FROM collectibles
    WHERE game_id = @mmx_game_id
      AND slug = 'armored-armadillo-hadouken'
);


-- Requirements that have one canonical acquisition path in the route model.
-- The service currently combines multiple rows with AND, so optional speedrun
-- alternatives are intentionally not represented as additional rows.
INSERT INTO collectible_requirements (
    game_id,
    collectible_id,
    requirement_type,
    required_weapon_id,
    required_collectible_id,
    required_stage_id,
    description
) VALUES
      (
          @mmx_game_id,
          @chill_penguin_heart_tank_id,
          'WEAPON',
          @fire_wave_weapon_id,
          NULL,
          NULL,
          'Use Fire Wave to destroy the igloo hiding the Heart Tank.'
      ),
      (
          @mmx_game_id,
          @chill_penguin_heart_tank_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Use the Leg Upgrade to reach the area above the Ride Armor section.'
      ),
      (
          @mmx_game_id,
          @storm_eagle_heart_tank_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Use a dash jump from the moving platform to reach the roof.'
      ),
      (
          @mmx_game_id,
          @helmet_upgrade_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Use the Leg Upgrade to dash jump onto the capsule ledge.'
      ),
      (
          @mmx_game_id,
          @flame_mammoth_heart_tank_id,
          'STAGE_CLEARED',
          NULL,
          NULL,
          @chill_penguin_stage_id,
          'Clear Chill Penguin Stage first to freeze the lava in Flame Mammoth Stage.'
      ),
      (
          @mmx_game_id,
          @flame_mammoth_sub_tank_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Use the Leg Upgrade to dash jump to the wall containing the Sub Tank.'
      ),
      (
          @mmx_game_id,
          @x_buster_upgrade_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Use the Leg Upgrade to dash jump to the breakable blocks.'
      ),
      (
          @mmx_game_id,
          @x_buster_upgrade_id,
          'COLLECTIBLE',
          NULL,
          @helmet_upgrade_id,
          NULL,
          'Use the Helmet Upgrade to break the blocks leading to the capsule.'
      ),
      (
          @mmx_game_id,
          @spark_mandrill_heart_tank_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Use a wall dash jump to reach the Heart Tank ledge.'
      ),
      (
          @mmx_game_id,
          @spark_mandrill_sub_tank_id,
          'WEAPON',
          @boomerang_cutter_weapon_id,
          NULL,
          NULL,
          'Use Boomerang Cutter to retrieve the Sub Tank through the wall.'
      ),
      (
          @mmx_game_id,
          @boomer_kuwanger_heart_tank_id,
          'WEAPON',
          @boomerang_cutter_weapon_id,
          NULL,
          NULL,
          'Re-enter the stage and use Boomerang Cutter to retrieve the Heart Tank.'
      ),
      (
          @mmx_game_id,
          @sting_chameleon_heart_tank_id,
          'STAGE_CLEARED',
          NULL,
          NULL,
          @launch_octopus_stage_id,
          'Clear Launch Octopus Stage first so the pit fills with water.'
      ),
      (
          @mmx_game_id,
          @sting_chameleon_heart_tank_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Use the Leg Upgrade to break through the rocks and cross the pit.'
      ),
      (
          @mmx_game_id,
          @body_armor_upgrade_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Use the Leg Upgrade to reach the area above the cave.'
      );


-- Hadouken requires every other collectible in Mega Man X.
INSERT INTO collectible_requirements (
    game_id,
    collectible_id,
    requirement_type,
    required_weapon_id,
    required_collectible_id,
    required_stage_id,
    description
)
SELECT
    @mmx_game_id,
    @hadouken_id,
    'COLLECTIBLE',
    NULL,
    collectible.id,
    NULL,
    CONCAT('Hadouken requires collectible: ', collectible.name, ' (', collectible.slug, ').')
FROM collectibles collectible
WHERE collectible.game_id = @mmx_game_id
  AND collectible.id <> @hadouken_id;


-- Having all eight boss weapons represents having defeated all eight Mavericks.
INSERT INTO collectible_requirements (
    game_id,
    collectible_id,
    requirement_type,
    required_weapon_id,
    required_collectible_id,
    required_stage_id,
    description
)
SELECT
    @mmx_game_id,
    @hadouken_id,
    'WEAPON',
    weapon.id,
    NULL,
    NULL,
    CONCAT('Hadouken requires boss weapon: ', weapon.name, '.')
FROM weapons weapon
WHERE weapon.game_id = @mmx_game_id;


-- The analyzer does not model health, lives or repeated platform visits.
-- OTHER remains deliberately unsatisfied so a one-pass route recommends revisiting.
INSERT INTO collectible_requirements (
    game_id,
    collectible_id,
    requirement_type,
    required_weapon_id,
    required_collectible_id,
    required_stage_id,
    description
) VALUES (
             @mmx_game_id,
             @hadouken_id,
             'OTHER',
             NULL,
             NULL,
             NULL,
             'Reach the hidden ledge near the Armored Armadillo boss door repeatedly; obtain the capsule while at full health.'
         );