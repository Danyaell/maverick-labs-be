package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "collectible_requirements")
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class CollectibleRequirement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collectible_id", nullable = false)
	private Collectible collectible;

	@Enumerated(EnumType.STRING)
	@Column(name = "requirement_type", nullable = false)
	private RequirementType requirementType;

	@Column(name = "required_key", nullable = false)
	private String requiredKey;

	@Column(columnDefinition = "TEXT")
	private String description;
}
