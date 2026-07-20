package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stages")
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Stage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@Column(nullable = false)
	private String slug;

	@Column(nullable = false)
	private String name;

	@Column(name = "stage_order", nullable = false)
	private Integer stageOrder;

	@Column(name = "image_asset_key")
	private String imageAssetKey;

	@Column(name = "base_difficulty", nullable = false)
	private Integer baseDifficulty = 50;

	@Column(name = "estimated_minutes", nullable = false)
	private Integer estimatedMinutes = 15;

	@OneToOne(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
	private Boss boss;

	@OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Collectible> collectibles = new ArrayList<>();
}
