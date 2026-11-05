package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// Classe mère de l'héritage JOINED (pattern de persistance "stratégie d'héritage JPA") :
//  - les champs communs sont dans la table post,
//  - PostEvenement ajoute les siens dans post_evenement, PostForum dans post_forum,
//  - les trois tables partagent la même clé post_id.
// abstract : on ne crée jamais un Post "tout court", toujours un PostEvenement ou un PostForum.
@Entity
@Table(name = "post")
@Inheritance(strategy = InheritanceType.JOINED)
// La colonne type_post dit si la ligne est un EVENEMENT ou un FORUM. Hibernate la remplit tout seul.
@DiscriminatorColumn(name = "type_post", discriminatorType = DiscriminatorType.STRING,
        columnDefinition = "enum('EVENEMENT','FORUM')")
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer id;

    // L'auteur
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // La base vérifie que la catégorie est du même type que le post (un événement ne peut pas être "Emploi")
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPost statut = StatutPost.PUBLIE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
