package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "collectibles")
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Collectible {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@lombok.Setter(lombok.AccessLevel.NONE)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stage_id", nullable = false)
	private Stage stage;

	@Column(nullable = false, length = 100)
	private String slug;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CollectibleType type;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_asset_key", length = 255)
	private String imageAssetKey;

	@Column(name = "sort_order")
	private Integer sortOrder;

	@OneToMany(mappedBy = "collectible", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CollectibleRequirement> requirements = new ArrayList<>();

	public void setStage(Stage stage) {
		this.stage = Objects.requireNonNull(
				stage,
				"A collectible must belong to a stage"
		);

		this.game = Objects.requireNonNull(
				stage.getGame(),
				"The stage must belong to a game before assigning it to a collectible"
		);
	}

	public void addRequirement(CollectibleRequirement requirement) {
		requirements.add(requirement);
		requirement.setCollectible(this);
	}
}

