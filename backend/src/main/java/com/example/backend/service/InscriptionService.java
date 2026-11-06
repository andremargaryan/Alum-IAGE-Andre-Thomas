package com.example.backend.service;

import com.example.backend.dto.response.InscriptionResponseDto;
import com.example.backend.dto.response.ParticipantsResponseDto;
import com.example.backend.entity.EvenementInscription;
import com.example.backend.entity.EvenementInscriptionId;
import com.example.backend.entity.PostEvenement;
import com.example.backend.entity.StatutInscription;
import com.example.backend.entity.StatutPost;
import com.example.backend.entity.StatutUser;
import com.example.backend.entity.User;
import com.example.backend.repository.EvenementInscriptionRepository;
import com.example.backend.repository.PostEvenementRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final PostEvenementRepository evenements;
    private final EvenementInscriptionRepository inscriptions;
    private final UserRepository utilisateurs;

    @Transactional
    public InscriptionResponseDto inscrire(Integer evenementId, Integer userId) {
        PostEvenement evenement = verrouillerEvenement(evenementId);
        User user = utilisateurActif(userId);
        verifierEvenementOuvert(evenement);

        EvenementInscriptionId id = new EvenementInscriptionId(evenementId, userId);
        EvenementInscription inscription = inscriptions.findById(id).orElse(null);
        if (inscription != null && inscription.getStatut() != StatutInscription.DESINSCRIT) {
            return statutEtRang(inscription);
        }

        Integer capacite = evenement.getCapaciteMax();
        boolean complet = capacite != null
                && inscriptions.countByEvenementIdAndStatut(evenementId, StatutInscription.INSCRIT) >= capacite;
        StatutInscription statut = complet ? StatutInscription.EN_ATTENTE : StatutInscription.INSCRIT;

        if (inscription == null) {
            inscription = new EvenementInscription(evenement, user, statut);
        }
        inscription.setStatut(statut);
        inscription.setDateInscription(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        return statutEtRang(inscriptions.save(inscription));
    }

    @Transactional
    public void desinscrire(Integer evenementId, Integer userId) {
        PostEvenement evenement = verrouillerEvenement(evenementId);
        utilisateurActif(userId);
        verifierEvenementOuvert(evenement);

        EvenementInscription inscription = inscriptions
                .findById(new EvenementInscriptionId(evenementId, userId)).orElse(null);
        if (inscription == null || inscription.getStatut() == StatutInscription.DESINSCRIT) {
            return;
        }

        boolean libereUnePlace = inscription.getStatut() == StatutInscription.INSCRIT;
        inscription.setStatut(StatutInscription.DESINSCRIT);
        if (libereUnePlace) {
            fileAttente(evenementId).stream().findFirst()
                    .ifPresent(premier -> premier.setStatut(StatutInscription.INSCRIT));
        }
    }

    @Transactional(readOnly = true)
    public InscriptionResponseDto consulter(Integer evenementId, Integer userId) {
        utilisateurActif(userId);
        verifierExistenceEvenement(evenementId);
        return inscriptions.findById(new EvenementInscriptionId(evenementId, userId))
                .map(this::statutEtRang)
                .orElseGet(() -> new InscriptionResponseDto(null, null));
    }

    @Transactional(readOnly = true)
    public ParticipantsResponseDto listerParticipants(Integer evenementId, Integer userId) {
        utilisateurActif(userId);
        verifierExistenceEvenement(evenementId);
        List<ParticipantsResponseDto.Participant> participants = inscriptions
                .findByEvenementIdAndStatutAndUserStatutOrderByIdUserIdAsc(
                        evenementId, StatutInscription.INSCRIT, StatutUser.ACTIF)
                .stream()
                .map(EvenementInscription::getUser)
                .map(user -> new ParticipantsResponseDto.Participant(user.getNom(), user.getPrenom()))
                .toList();
        long nombreEnAttente = inscriptions.countByEvenementIdAndStatut(evenementId, StatutInscription.EN_ATTENTE);
        return new ParticipantsResponseDto(participants, nombreEnAttente);
    }

    private PostEvenement verrouillerEvenement(Integer id) {
        // Le verrou précède les lectures pour sérialiser les écritures sur un même événement.
        return evenements.findByIdForUpdate(id).orElseThrow(() -> evenementIntrouvable(id));
    }

    private void verifierExistenceEvenement(Integer id) {
        if (!evenements.existsById(id)) {
            throw evenementIntrouvable(id);
        }
    }

    private ResponseStatusException evenementIntrouvable(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Événement introuvable : " + id);
    }

    private User utilisateurActif(Integer id) {
        return utilisateurs.findById(id)
                .filter(user -> user.getStatut() == StatutUser.ACTIF)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu ou inactif"));
    }

    private void verifierEvenementOuvert(PostEvenement evenement) {
        if (evenement.getStatut() != StatutPost.PUBLIE || !evenement.getDateEvenement().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Les inscriptions à cet événement sont fermées");
        }
    }

    private List<EvenementInscription> fileAttente(Integer evenementId) {
        return inscriptions.findByEvenementIdAndStatutOrderByDateInscriptionAscIdUserIdAsc(
                evenementId, StatutInscription.EN_ATTENTE);
    }

    private InscriptionResponseDto statutEtRang(EvenementInscription inscription) {
        Integer rang = null;
        if (inscription.getStatut() == StatutInscription.EN_ATTENTE) {
            // La transaction renvoie la même instance JPA lors de la lecture de la file.
            rang = fileAttente(inscription.getId().getPostId()).indexOf(inscription) + 1;
        }
        return new InscriptionResponseDto(inscription.getStatut(), rang);
    }
}
