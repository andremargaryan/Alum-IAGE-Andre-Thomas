package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Table "users" (et non "user", mot réservé en SQL)
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "user_id")
    private Integer id;

    @Column(nullable = false, length = 80)
    private String prenom;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @ToString.Exclude                                   // jamais le mot de passe dans les logs
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;                          // hash BCrypt, jamais en clair

    @Enumerated(EnumType.STRING)                        // stocke "ETUDIANT", pas 0
    @Column(nullable = false)
    private Role role = Role.ETUDIANT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutUser statut = StatutUser.ACTIF;

    @Column(name = "notifications_actives", nullable = false)
    private boolean notificationsActives = true;

    @Column(length = 100)
    private String ville;

    @CreationTimestamp                                  // rempli automatiquement à la création
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relations LAZY : les listes ne sont chargées qu'au premier appel de getExperiences() / getFormations().
    // mappedBy = "user" : la clé étrangère est dans Experience.user (colonne experience.user_id).
    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Formation> formations = new ArrayList<>();

    // A utiliser pour ajouter une expérience : remplit aussi experience.user
    public void addExperience(Experience experience) {
        experiences.add(experience);
        experience.setUser(this);
    }

    public void addFormation(Formation formation) {
        formations.add(formation);
        formation.setUser(this);
    }
}
