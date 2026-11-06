package com.example.backend.dto.response;

import com.example.backend.entity.Formation;
import lombok.Data;
import lombok.NoArgsConstructor;

// Ce que l'API renvoie pour une formation : on aplatit l'université et le parcours
// pour que le front n'ait pas à faire d'autres appels pour afficher "Paris 1 - IBI".
@Data
@NoArgsConstructor
public class FormationResponseDto {
    private Integer id;
    private Integer etablissementId;
    private String etablissementNom;
    private Integer parcoursId;         // null si pas de parcours
    private String parcoursCode;        // "IBI"
    private String parcoursIntitule;    // "Intelligent Business Informatics"
    private Short anneeDebut;
    private Short anneeFin;
    private String commentaire;

    // Conversion entité -> DTO (à appeler dans une méthode @Transactional : etablissement et parcours sont en LAZY)
    public static FormationResponseDto from(Formation formation) {
        FormationResponseDto dto = new FormationResponseDto();
        dto.setId(formation.getId());
        dto.setEtablissementId(formation.getEtablissement().getId());
        dto.setEtablissementNom(formation.getEtablissement().getNom());
        if (formation.getParcours() != null) {
            dto.setParcoursId(formation.getParcours().getId());
            dto.setParcoursCode(formation.getParcours().getCode());
            dto.setParcoursIntitule(formation.getParcours().getIntitule());
        }
        dto.setAnneeDebut(formation.getAnneeDebut());
        dto.setAnneeFin(formation.getAnneeFin());
        dto.setCommentaire(formation.getCommentaire());
        return dto;
    }
}
