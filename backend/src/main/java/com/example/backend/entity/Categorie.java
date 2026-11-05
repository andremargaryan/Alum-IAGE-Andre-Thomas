package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Catégories gérées par l'admin, une liste par type de post (ex : "Soirée" pour les événements, "Emploi" pour le forum)
@Entity
@Table(name = "categorie")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categorie_id")
    private Integer id;

    @Column(nullable = false, length = 60)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_post", nullable = false)
    private TypePost typePost;
}
