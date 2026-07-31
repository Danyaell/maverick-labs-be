INSERT INTO games (
    code,
    title,
    release_order
) VALUES
      ('MMX',  'Mega Man X',  1),
      ('MMX2', 'Mega Man X2', 2),
      ('MMX3', 'Mega Man X3', 3),
      ('MMX4', 'Mega Man X4', 4),
      ('MMX5', 'Mega Man X5', 5);


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
          'Fires ice projectiles.',
          'mmx.weapon.shotgun-ice'
      ),
      (
          @mmx_game_id,
          @storm_eagle_stage_id,
          'storm-tornado',
          'Storm Tornado',
          'Creates a tornado attack.',
          'mmx.weapon.storm-tornado'
      ),
      (
          @mmx_game_id,
          @flame_mammoth_stage_id,
          'flame-wave',
          'Flame Wave',
          'Creates a fire attack.',
          'mmx.weapon.flame-wave'
      ),
      (
          @mmx_game_id,
          @spark_mandrill_stage_id,
          'electric-spark',
          'Electric Spark',
          'Fires electrical bolts.',
          'mmx.weapon.electric-spark'
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

SET @flame_wave_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'flame-wave'
);

SET @electric_spark_weapon_id := (
    SELECT id
    FROM weapons
    WHERE game_id = @mmx_game_id
      AND slug = 'electric-spark'
);


INSERT INTO bosses (
    stage_id,
    slug,
    name,
    image_asset_key,
    weakness_weapon_id
) VALUES
      (
          @chill_penguin_stage_id,
          'chill-penguin',
          'Chill Penguin',
          'mmx.boss.chill-penguin',
          @flame_wave_weapon_id
      ),
      (
          @storm_eagle_stage_id,
          'storm-eagle',
          'Storm Eagle',
          'mmx.boss.storm-eagle',
          @electric_spark_weapon_id
      ),
      (
          @flame_mammoth_stage_id,
          'flame-mammoth',
          'Flame Mammoth',
          'mmx.boss.flame-mammoth',
          @storm_tornado_weapon_id
      ),
      (
          @spark_mandrill_stage_id,
          'spark-mandrill',
          'Spark Mandrill',
          'mmx.boss.spark-mandrill',
          @shotgun_ice_weapon_id
      );


INSERT INTO collectibles (
    stage_id,
    slug,
    name,
    type,
    description,
    image_asset_key,
    sort_order
) VALUES
      (
          @chill_penguin_stage_id,
          'chill-penguin-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @chill_penguin_stage_id,
          'leg-upgrade-capsule',
          'Leg Upgrade',
          'ARMOR_UPGRADE',
          'Unlocks dash movement.',
          'mmx.collectible.leg-upgrade',
          2
      ),
      (
          @storm_eagle_stage_id,
          'storm-eagle-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @storm_eagle_stage_id,
          'helmet-upgrade-capsule',
          'Helmet Upgrade',
          'ARMOR_UPGRADE',
          'Provides the ability to destroy blocks.',
          'mmx.collectible.armor-upgrade',
          2
      ),
      (
          @storm_eagle_stage_id,
          'storm-eagle-sub-tank',
          'Sub Tank',
          'SUB_TANK',
          'Provides extra health reserve.',
          'mmx.collectible.sub-tank',
          3
      ),
      (
          @flame_mammoth_stage_id,
          'flame-mammoth-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @flame_mammoth_stage_id,
          'flame-mammoth-sub-tank',
          'Sub Tank',
          'SUB_TANK',
          'Provides extra health reserve.',
          'mmx.collectible.sub-tank',
          2
      ),
      (
          @flame_mammoth_stage_id,
          'x-buster-upgrade-capsule',
          'X Buster Upgrade',
          'ARMOR_UPGRADE',
          'Increases defense.',
          'mmx.collectible.armor-upgrade',
          3
      ),
      (
          @spark_mandrill_stage_id,
          'spark-mandrill-heart-tank',
          'Heart Tank',
          'HEART_TANK',
          'Increases maximum health.',
          'mmx.collectible.heart-tank',
          1
      ),
      (
          @spark_mandrill_stage_id,
          'spark-mandrill-sub-tank',
          'Sub Tank',
          'SUB_TANK',
          'Provides extra health reserve.',
          'mmx.collectible.sub-tank',
          2
      );


SET @flame_mammoth_heart_tank_id := (
    SELECT id
    FROM collectibles
    WHERE stage_id = @flame_mammoth_stage_id
      AND slug = 'flame-mammoth-heart-tank'
);

SET @x_buster_upgrade_id := (
    SELECT id
    FROM collectibles
    WHERE stage_id = @flame_mammoth_stage_id
      AND slug = 'x-buster-upgrade-capsule'
);

SET @leg_upgrade_id := (
    SELECT id
    FROM collectibles
    WHERE stage_id = @chill_penguin_stage_id
      AND slug = 'leg-upgrade-capsule'
);


INSERT INTO collectible_requirements (
    collectible_id,
    requirement_type,
    required_weapon_id,
    required_collectible_id,
    required_stage_id,
    description
) VALUES
      (
          @flame_mammoth_heart_tank_id,
          'WEAPON',
          @storm_tornado_weapon_id,
          NULL,
          NULL,
          'Use Storm Tornado to break the chimney and reach this item.'
      ),
      (
          @x_buster_upgrade_id,
          'COLLECTIBLE',
          NULL,
          @leg_upgrade_id,
          NULL,
          'Requires dash movement from the leg upgrade.'
      );