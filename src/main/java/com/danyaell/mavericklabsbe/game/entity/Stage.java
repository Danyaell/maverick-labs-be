package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

	@OneToOne(mappedBy = "stage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Boss boss;

	@OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Collectible> collectibles = new ArrayList<>();
}


