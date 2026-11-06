package com.example.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

// Ce que le front envoie pour ajouter un cursus MIAGE (POST /api/users/{userId}/formations)
@Data
@NoArgsConstructor
public class FormationDto {
    private Integer etablissementId;    // obligatoire : une des universités de la liste
    private Integer parcoursId;         // facultatif : doit appartenir à cette université
    private Short anneeDebut;           // obligatoire
    private Short anneeFin;             // facultatif : null = en cours
    private String commentaire;         // facultatif
}
