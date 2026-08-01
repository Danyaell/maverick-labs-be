package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "bosses")
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Boss {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@lombok.Setter(lombok.AccessLevel.NONE)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "stage_id",
			nullable = false,
			unique = true
	)
	private Stage stage;

	@Column(nullable = false, length = 100)
	private String slug;

	@Column(nullable = false)
	private String name;

	@Column(name = "image_asset_key", length = 255)
	private String imageAssetKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "weakness_weapon_id")
	private Weapon weaknessWeapon;

	public void setStage(Stage stage) {
		this.stage = Objects.requireNonNull(
				stage,
				"A boss must belong to a stage"
		);

		this.game = Objects.requireNonNull(
				stage.getGame(),
				"The stage must belong to a game before assigning it to a boss"
		);
	}
}

