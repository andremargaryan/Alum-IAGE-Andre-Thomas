package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Les universités du réseau MIAGE (liste gérée par l'admin)
@Entity
@Table(name = "etablissement")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "etablissement_id")
    private Integer id;

    @Column(nullable = false, unique = true, length = 150)
    private String nom;

    @Column(length = 100)
    private String ville;
}
