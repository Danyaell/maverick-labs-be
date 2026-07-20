package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "weapons")
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Weapon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "obtained_from_stage_id")
	private Stage obtainedFromStage;

	@Column(nullable = false)
	private String slug;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_asset_key")
	private String imageAssetKey;
}


