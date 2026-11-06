package com.example.backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Ce que le front envoie pour créer un événement (POST /api/events).
// Les annotations sont vérifiées par @Valid dans le contrôleur : en cas d'erreur, réponse 400 sans entrer dans le service.
@Data
@NoArgsConstructor
public class PostEvenementCreateDto {

    @NotBlank
    @Size(max = 200)
    private String titre;

    @NotBlank
    private String description;

    @NotNull
    private Integer categorieId;            // une catégorie de type EVENEMENT (Soirée, Conférence...)

    @NotNull
    @Future                                 // on ne crée pas un événement dans le passé
    private LocalDateTime dateEvenement;    // format JSON : "2026-12-10T18:30:00"

    @NotBlank
    @Size(max = 150)
    private String lieu;

    @NotBlank
    @Size(max = 255)
    private String adresse;

    @Positive
    private Integer capaciteMax;            // facultatif : null = places illimitées
}
