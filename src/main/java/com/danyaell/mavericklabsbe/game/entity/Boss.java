package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bosses")
@Data
@NoArgsConstructor
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

	public Boss(Long id, Stage stage, String slug, String name, String imageAssetKey) {
		this.id = id;
		this.stage = stage;
		this.slug = slug;
		this.name = name;
		this.imageAssetKey = imageAssetKey;
	}
}

