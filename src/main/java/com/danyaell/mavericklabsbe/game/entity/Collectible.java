package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collectibles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collectible {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stage_id", nullable = false)
	private Stage stage;

	@Column(nullable = false)
	private String slug;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CollectibleType type;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_asset_key")
	private String imageAssetKey;

	@Column(name = "sort_order")
	private Integer sortOrder;

	@OneToMany(mappedBy = "collectible", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CollectibleRequirement> requirements = new ArrayList<>();
}

