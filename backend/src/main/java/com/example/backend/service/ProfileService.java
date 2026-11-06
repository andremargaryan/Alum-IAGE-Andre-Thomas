package com.example.backend.service;

import com.example.backend.dto.request.ExperienceDto;
import com.example.backend.dto.request.FormationDto;
import com.example.backend.dto.response.ExperienceResponseDto;
import com.example.backend.dto.response.FormationResponseDto;
import com.example.backend.entity.Etablissement;
import com.example.backend.entity.Experience;
import com.example.backend.entity.Formation;
import com.example.backend.entity.Parcours;
import com.example.backend.entity.StatutUser;
import com.example.backend.entity.User;
import com.example.backend.repository.EtablissementRepository;
import com.example.backend.repository.ExperienceRepository;
import com.example.backend.repository.FormationRepository;
import com.example.backend.repository.ParcoursRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Logique métier du profil alumni : expériences professionnelles et cursus MIAGE.
// Les erreurs sont levées avec ResponseStatusException : Spring les transforme en réponse HTTP (404, 400...).
@Service
@RequiredArgsConstructor   // Lombok : génère le constructeur qui injecte les champs final ci-dessous
public class ProfileService {

    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;
    private final FormationRepository formationRepository;
    private final EtablissementRepository etablissementRepository;
    private final ParcoursRepository parcoursRepository;

    // ------------------------------------------------------------ Expériences

    @Transactional
    public ExperienceResponseDto addExperience(Integer userId, ExperienceDto dto) {
        User user = trouverUtilisateur(userId);
        verifierTexteObligatoire(dto.getEntreprise(), "entreprise");
        verifierTexteObligatoire(dto.getPoste(), "poste");
        verifierAnnees(dto.getAnneeDebut(), dto.getAnneeFin());

        Experience experience = new Experience();
        experience.setUser(user);
        experience.setEntreprise(dto.getEntreprise());
        experience.setPoste(dto.getPoste());
        experience.setAnneeDebut(dto.getAnneeDebut());
        experience.setAnneeFin(dto.getAnneeFin());
        experience.setCommentaire(dto.getCommentaire());

        return ExperienceResponseDto.from(experienceRepository.save(experience));
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponseDto> getExperiencesByUser(Integer userId) {
        trouverUtilisateur(userId);   // 404 si l'utilisateur n'existe pas
        return experienceRepository.findByUserId(userId).stream()
                .map(ExperienceResponseDto::from)
                .toList();
    }

    // ------------------------------------------------------------ Formations (cursus MIAGE)

    @Transactional
    public FormationResponseDto addFormation(Integer userId, FormationDto dto) {
        User user = trouverUtilisateur(userId);
        verifierAnnees(dto.getAnneeDebut(), dto.getAnneeFin());

        if (dto.getEtablissementId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'université (etablissementId) est obligatoire");
        }
        Etablissement etablissement = etablissementRepository.findById(dto.getEtablissementId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Université introuvable : " + dto.getEtablissementId()));

        Parcours parcours = null;
        if (dto.getParcoursId() != null) {
            parcours = parcoursRepository.findById(dto.getParcoursId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Parcours introuvable : " + dto.getParcoursId()));
            // La base le refuserait aussi, mais avec une erreur 500 illisible : on vérifie avant
            if (!parcours.getEtablissement().getId().equals(etablissement.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le parcours " + parcours.getCode() + " n'existe pas dans " + etablissement.getNom());
            }
        }

        Formation formation = new Formation();
        formation.setUser(user);
        formation.setEtablissement(etablissement);
        formation.setParcours(parcours);
        formation.setAnneeDebut(dto.getAnneeDebut());
        formation.setAnneeFin(dto.getAnneeFin());
        formation.setCommentaire(dto.getCommentaire());

        return FormationResponseDto.from(formationRepository.save(formation));
    }

    @Transactional(readOnly = true)
    public List<FormationResponseDto> getFormationsByUser(Integer userId) {
        trouverUtilisateur(userId);
        return formationRepository.findByUserId(userId).stream()
                .map(FormationResponseDto::from)
                .toList();
    }

    // ------------------------------------------------------------ Vérifications communes

    // Un compte supprimé (suppression logique) est traité comme introuvable
    private User trouverUtilisateur(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Utilisateur introuvable : " + userId));
        if (user.getStatut() == StatutUser.SUPPRIME) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable : " + userId);
        }
        return user;
    }

    private void verifierTexteObligatoire(String valeur, String nomDuChamp) {
        if (valeur == null || valeur.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le champ " + nomDuChamp + " est obligatoire");
        }
    }

    private void verifierAnnees(Short anneeDebut, Short anneeFin) {
        if (anneeDebut == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le champ anneeDebut est obligatoire");
        }
        if (anneeFin != null && anneeFin < anneeDebut) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "anneeFin ne peut pas être avant anneeDebut");
        }
    }
}
