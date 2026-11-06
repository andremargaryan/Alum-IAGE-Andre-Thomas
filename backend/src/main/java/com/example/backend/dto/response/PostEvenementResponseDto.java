package com.example.backend.dto.response;

import com.example.backend.entity.PostEvenement;
import com.example.backend.entity.StatutPost;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Ce que l'API renvoie pour un événement, avec la jauge de remplissage calculée.
@Data
@NoArgsConstructor
public class PostEvenementResponseDto {
    private Integer id;
    private String titre;
    private String description;
    private Integer categorieId;
    private String categorieLibelle;
    private StatutPost statut;
    private Integer auteurId;
    private String auteurNom;               // "Léa Martin"
    private LocalDateTime dateEvenement;
    private String lieu;
    private String adresse;
    private LocalDateTime createdAt;

    // Jauge (calculée, jamais stockée en base)
    private Integer capaciteMax;            // null = illimité
    private long nombreInscrits;
    private Integer placesRestantes;        // null = illimité
    private boolean complet;
    private Integer tauxRemplissage;        // en %, null si illimité

    // Conversion entité -> DTO. nombreInscrits est compté par le service (table evenement_inscription).
    // A appeler dans une méthode @Transactional : categorie et user sont en LAZY.
    public static PostEvenementResponseDto from(PostEvenement evenement, long nombreInscrits) {
        PostEvenementResponseDto dto = new PostEvenementResponseDto();
        dto.setId(evenement.getId());
        dto.setTitre(evenement.getTitre());
        dto.setDescription(evenement.getDescription());
        dto.setCategorieId(evenement.getCategorie().getId());
        dto.setCategorieLibelle(evenement.getCategorie().getLibelle());
        dto.setStatut(evenement.getStatut());
        dto.setAuteurId(evenement.getUser().getId());
        dto.setAuteurNom(evenement.getUser().getPrenom() + " " + evenement.getUser().getNom());
        dto.setDateEvenement(evenement.getDateEvenement());
        dto.setLieu(evenement.getLieu());
        dto.setAdresse(evenement.getAdresse());
        dto.setCreatedAt(evenement.getCreatedAt());

        dto.setCapaciteMax(evenement.getCapaciteMax());
        dto.setNombreInscrits(nombreInscrits);
        if (evenement.getCapaciteMax() != null) {
            int capacite = evenement.getCapaciteMax();
            dto.setPlacesRestantes((int) Math.max(0, capacite - nombreInscrits));
            dto.setComplet(nombreInscrits >= capacite);
            dto.setTauxRemplissage((int) Math.min(100, nombreInscrits * 100 / capacite));
        }
        return dto;
    }
}
