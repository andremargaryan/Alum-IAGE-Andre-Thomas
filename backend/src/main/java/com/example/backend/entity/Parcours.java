package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Un parcours de Master MIAGE d'une université (ex : IBI et SBI à Paris 1)
@Entity
@Table(name = "parcours")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Parcours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parcours_id")
    private Integer id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etablissement_id")
    private Etablissement etablissement;

    @Column(nullable = false, length = 20)
    private String code;                                // "IBI"

    @Column(nullable = false, length = 150)
    private String intitule;                            // "Intelligent Business Informatics"

    @Column(nullable = false)
    private boolean actif = true;                       // false = parcours fermé (gardé pour les anciennes promos)
}
