package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(
		name = "collectible_requirements",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_requirements_required_weapon",
						columnNames = {"collectible_id", "required_weapon_id"}
				),
				@UniqueConstraint(
						name = "uk_requirements_required_collectible",
						columnNames = {"collectible_id", "required_collectible_id"}
				),
				@UniqueConstraint(
						name = "uk_requirements_required_stage",
						columnNames = {"collectible_id", "required_stage_id"}
				)
		}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectibleRequirement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@lombok.Setter(lombok.AccessLevel.NONE)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "collectible_id",
			nullable = false
	)
	private Collectible collectible;

	@Enumerated(EnumType.STRING)
	@Column(name = "requirement_type", nullable = false, length = 50)
	private RequirementType requirementType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "required_weapon_id")
	private Weapon requiredWeapon;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "required_collectible_id")
	private Collectible requiredCollectible;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "required_stage_id")
	private Stage requiredStage;

	@Column(columnDefinition = "TEXT")
	private String description;

	public void setCollectible(Collectible collectible) {
		this.collectible = Objects.requireNonNull(
				collectible,
				"A requirement must belong to a collectible"
		);

		this.game = Objects.requireNonNull(
				collectible.getGame(),
				"The collectible must belong to a game before assigning requirements"
		);
	}
}