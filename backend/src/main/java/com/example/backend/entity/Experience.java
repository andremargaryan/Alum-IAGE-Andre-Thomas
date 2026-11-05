package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "experience")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experience_id")
    private Integer id;

    // LAZY : le User n'est chargé que si on appelle getUser()
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 150)
    private String entreprise;

    @Column(nullable = false, length = 150)
    private String poste;

    @Column(name = "annee_debut", nullable = false)
    private Short anneeDebut;                           // SMALLINT en base -> Short

    @Column(name = "annee_fin")
    private Short anneeFin;                             // null = poste actuel

    @Column(columnDefinition = "TEXT")
    private String commentaire;
}
