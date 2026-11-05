package com.example.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

// Ce que le front envoie pour ajouter une expérience (POST /api/users/{userId}/experiences)
@Data
@NoArgsConstructor
public class ExperienceDto {
    private String entreprise;      // obligatoire
    private String poste;           // obligatoire
    private Short anneeDebut;       // obligatoire
    private Short anneeFin;         // facultatif : null = poste actuel
    private String commentaire;     // facultatif
}
