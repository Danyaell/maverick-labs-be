package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bosses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boss {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stage_id", nullable = false)
	@lombok.ToString.Exclude
	@lombok.EqualsAndHashCode.Exclude
	private Stage stage;

	@Column(nullable = false)
	private String slug;

	@Column(nullable = false)
	private String name;

	@Column(name = "image_asset_key")
	private String imageAssetKey;

	@Column(name = "weakness_weapon")
	@lombok.ToString.Exclude
	@lombok.EqualsAndHashCode.Exclude
	private String weaknessWeapon;
}

