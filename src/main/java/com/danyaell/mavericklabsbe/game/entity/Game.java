package com.danyaell.mavericklabsbe.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer releaseOrder;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stageOrder ASC")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private List<Stage> stages = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private List<Weapon> weapons = new ArrayList<>();

    public Game(Long id, String code, String title, Integer releaseOrder, List<Stage> stages) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.releaseOrder = releaseOrder;
        this.stages = stages;
        this.weapons = new ArrayList<>();
    }
}
