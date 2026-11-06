package com.example.backend.service;

import com.example.backend.dto.request.PostEvenementCreateDto;
import com.example.backend.dto.response.PostEvenementResponseDto;
import com.example.backend.entity.Categorie;
import com.example.backend.entity.PostEvenement;
import com.example.backend.entity.Role;
import com.example.backend.entity.StatutInscription;
import com.example.backend.entity.StatutPost;
import com.example.backend.entity.StatutUser;
import com.example.backend.entity.TypePost;
import com.example.backend.entity.User;
import com.example.backend.repository.CategorieRepository;
import com.example.backend.repository.EvenementInscriptionRepository;
import com.example.backend.repository.PostEvenementRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

// Logique métier des événements : création (réservée aux organisateurs et admins),
// recherche avec filtres, et calcul de la jauge de places.
@Service
@RequiredArgsConstructor
public class EvenementService {

    private final PostEvenementRepository postEvenementRepository;
    private final EvenementInscriptionRepository evenementInscriptionRepository;
    private final CategorieRepository categorieRepository;
    private final UserRepository userRepository;

    // ------------------------------------------------------------ Création

    @Transactional
    public PostEvenementResponseDto creerEvenement(Integer auteurId, PostEvenementCreateDto dto) {
        User auteur = trouverAuteurAutorise(auteurId);

        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Catégorie inconnue : " + dto.getCategorieId()));
        // La base le refuserait aussi (clé étrangère composée), mais avec une erreur 500 : on vérifie avant
        if (categorie.getTypePost() != TypePost.EVENEMENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La catégorie " + categorie.getLibelle() + " n'est pas une catégorie d'événement");
        }

        PostEvenement evenement = new PostEvenement();
        evenement.setUser(auteur);
        evenement.setCategorie(categorie);
        evenement.setTitre(dto.getTitre());
        evenement.setDescription(dto.getDescription());
        evenement.setDateEvenement(dto.getDateEvenement());
        evenement.setLieu(dto.getLieu());
        evenement.setAdresse(dto.getAdresse());
        evenement.setCapaciteMax(dto.getCapaciteMax());
        // statut = PUBLIE par défaut (voir Post), type_post = EVENEMENT écrit par Hibernate

        PostEvenement enregistre = postEvenementRepository.save(evenement);
        return PostEvenementResponseDto.from(enregistre, 0);   // personne n'est encore inscrit
    }

    // ------------------------------------------------------------ Consultation

    // aVenir = true : seulement les événements à partir de maintenant (par défaut)
    @Transactional(readOnly = true)
    public List<PostEvenementResponseDto> rechercher(String motCle, String lieu, boolean aVenir) {
        LocalDateTime depuis = aVenir ? LocalDateTime.now() : null;
        return postEvenementRepository.rechercher(StatutPost.PUBLIE, videEnNull(motCle), videEnNull(lieu), depuis)
                .stream()
                .map(evenement -> PostEvenementResponseDto.from(evenement, compterInscrits(evenement.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public PostEvenementResponseDto getEvenement(Integer id) {
        PostEvenement evenement = postEvenementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Événement introuvable : " + id));
        return PostEvenementResponseDto.from(evenement, compterInscrits(id));
    }

    // ------------------------------------------------------------ Outils

    // Le nombre d'inscrits n'est jamais stocké : on le compte à chaque lecture, il ne peut donc pas être faux
    private long compterInscrits(Integer evenementId) {
        return evenementInscriptionRepository.countByEvenementIdAndStatut(evenementId, StatutInscription.INSCRIT);
    }

    // Seuls les comptes actifs ORGANISATEUR ou ADMIN peuvent créer un événement.
    // On relit le rôle en base (et pas dans le jeton) : un compte suspendu ou rétrogradé
    // depuis sa connexion perd ses droits tout de suite, sans attendre l'expiration du jeton.
    private User trouverAuteurAutorise(Integer auteurId) {
        User auteur = userRepository.findById(auteurId)
                .filter(user -> user.getStatut() == StatutUser.ACTIF)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Utilisateur inconnu ou inactif : " + auteurId));
        if (auteur.getRole() != Role.ORGANISATEUR && auteur.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seuls les organisateurs et les administrateurs peuvent créer un événement");
        }
        return auteur;
    }

    // "?motCle=" (vide) doit se comporter comme "pas de filtre"
    private String videEnNull(String texte) {
        return (texte == null || texte.isBlank()) ? null : texte.trim();
    }
}
