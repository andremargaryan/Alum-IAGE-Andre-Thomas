package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

// Inscription d'un utilisateur à un événement. Une seule table pour : inscrit, liste d'attente, désinscrit, historique.
// Rang en liste d'attente = ordre de date_inscription (pas de champ position).
@Entity
@Table(name = "evenement_inscription")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class EvenementInscription {

    @EmbeddedId
    private EvenementInscriptionId id = new EvenementInscriptionId();

    // @MapsId : la colonne post_id sert à la fois de morceau de clé primaire et de clé étrangère
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private PostEvenement evenement;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInscription statut = StatutInscription.INSCRIT;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    // Constructeur pratique : new EvenementInscription(evenement, user, StatutInscription.EN_ATTENTE)
    public EvenementInscription(PostEvenement evenement, User user, StatutInscription statut) {
        this.evenement = evenement;
        this.user = user;
        this.statut = statut;
    }
}
