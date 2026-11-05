package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

// Classe fille : un événement. Ses champs propres sont dans la table post_evenement.
// Le nombre d'inscrits n'est pas stocké : il se calcule avec EvenementInscriptionRepository.countBy...
@Entity
@Table(name = "post_evenement")
@DiscriminatorValue("EVENEMENT")
@PrimaryKeyJoinColumn(name = "post_id")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class PostEvenement extends Post {

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime dateEvenement;                // date ET heure

    @Column(nullable = false, length = 150)
    private String lieu;                                // "Bar Le Tolbiac"

    @Column(nullable = false)
    private String adresse;

    @Column(name = "capacite_max")
    private Integer capaciteMax;                        // null = places illimitées
}
