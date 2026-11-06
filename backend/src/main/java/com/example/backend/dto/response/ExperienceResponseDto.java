package com.example.backend.dto.response;

import com.example.backend.entity.Experience;
import lombok.Data;
import lombok.NoArgsConstructor;

// Ce que l'API renvoie pour une expérience.
// On ne renvoie jamais l'entité directement : elle contient le User (et son mot de passe) en LAZY.
@Data
@NoArgsConstructor
public class ExperienceResponseDto {
    private Integer id;
    private String entreprise;
    private String poste;
    private Short anneeDebut;
    private Short anneeFin;
    private String commentaire;

    // Conversion entité -> DTO
    public static ExperienceResponseDto from(Experience experience) {
        ExperienceResponseDto dto = new ExperienceResponseDto();
        dto.setId(experience.getId());
        dto.setEntreprise(experience.getEntreprise());
        dto.setPoste(experience.getPoste());
        dto.setAnneeDebut(experience.getAnneeDebut());
        dto.setAnneeFin(experience.getAnneeFin());
        dto.setCommentaire(experience.getCommentaire());
        return dto;
    }
}
