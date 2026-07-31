package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "games")
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer releaseOrder;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stageOrder ASC")
    private List<Stage> stages = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Weapon> weapons = new ArrayList<>();

    public Game(Long id, String code, String title, Integer releaseOrder, List<Stage> stages) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.releaseOrder = releaseOrder;
        this.stages = stages;
        this.weapons = new ArrayList<>();
    }

    public void addStage(Stage stage) {
        Objects.requireNonNull(stage, "stage is required");
        stage.setGame(this);
        stages.add(stage);
    }

    public void addWeapon(Weapon weapon) {
        Objects.requireNonNull(weapon, "weapon is required");
        weapon.setGame(this);
        weapons.add(weapon);
    }
}
