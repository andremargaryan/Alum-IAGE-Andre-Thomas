package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Le cursus Master MIAGE d'un utilisateur : université + parcours + années
@Entity
@Table(name = "formation")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "formation_id")
    private Integer id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etablissement_id")
    private Etablissement etablissement;

    // Facultatif. La base refuse un parcours qui n'appartient pas à l'université choisie.
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcours_id")
    private Parcours parcours;

    @Column(name = "annee_debut", nullable = false)
    private Short anneeDebut;

    @Column(name = "annee_fin")
    private Short anneeFin;                             // null = en cours, sinon = la promo

    @Column(columnDefinition = "TEXT")
    private String commentaire;
}
