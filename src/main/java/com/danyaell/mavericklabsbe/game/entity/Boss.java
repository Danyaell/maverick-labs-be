package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;

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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stage_id", nullable = false)
	private Stage stage;

	@Column(nullable = false)
	private String slug;

	@Column(nullable = false)
	private String name;

	@Column(name = "image_asset_key")
	private String imageAssetKey;

	@Column(name = "weakness_weapon")
	private String weaknessWeapon;
}

