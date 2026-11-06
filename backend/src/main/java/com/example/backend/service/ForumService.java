package com.example.backend.service;

import com.example.backend.dto.request.PostForumCreateDto;
import com.example.backend.dto.response.LikeResponseDto;
import com.example.backend.dto.response.PostForumResponseDto;
import com.example.backend.entity.Categorie;
import com.example.backend.entity.PostForum;
import com.example.backend.entity.PostLike;
import com.example.backend.entity.PostLikeId;
import com.example.backend.entity.StatutPost;
import com.example.backend.entity.StatutUser;
import com.example.backend.entity.TypePost;
import com.example.backend.entity.User;
import com.example.backend.repository.CategorieRepository;
import com.example.backend.repository.PostForumRepository;
import com.example.backend.repository.PostLikeRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Logique métier du forum : publication d'un sujet (tout utilisateur actif),
// recherche par thématique, et like / unlike.
@Service
@RequiredArgsConstructor
public class ForumService {

    private final PostForumRepository postForumRepository;
    private final PostLikeRepository postLikeRepository;
    private final CategorieRepository categorieRepository;
    private final UserRepository userRepository;

    // ------------------------------------------------------------ Publication

    @Transactional
    public PostForumResponseDto publierSujet(Integer auteurId, PostForumCreateDto dto) {
        User auteur = trouverUtilisateurActif(auteurId);

        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Catégorie inconnue : " + dto.getCategorieId()));
        if (categorie.getTypePost() != TypePost.FORUM) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La catégorie " + categorie.getLibelle() + " n'est pas une catégorie du forum");
        }

        PostForum post = new PostForum();
        post.setUser(auteur);
        post.setCategorie(categorie);
        post.setTitre(dto.getTitre());
        post.setDescription(dto.getDescription());
        // statut = PUBLIE par défaut (voir Post), type_post = FORUM écrit par Hibernate

        PostForum enregistre = postForumRepository.save(post);
        return PostForumResponseDto.from(enregistre, 0, false);   // personne n'a encore liké
    }

    // ------------------------------------------------------------ Consultation

    // Du plus récent au plus ancien, filtres facultatifs
    @Transactional(readOnly = true)
    public List<PostForumResponseDto> rechercher(Integer moiId, Integer categorieId, String motCle) {
        return postForumRepository.rechercher(StatutPost.PUBLIE, categorieId, videEnNull(motCle))
                .stream()
                .map(post -> versDto(post, moiId))
                .toList();
    }

    @Transactional(readOnly = true)
    public PostForumResponseDto getSujet(Integer moiId, Integer postId) {
        return versDto(trouverSujetPublie(postId), moiId);
    }

    // ------------------------------------------------------------ Like / unlike

    // Un clic sur "J'aime" : ajoute le like s'il n'existe pas, le retire sinon.
    // La clé primaire (user_id, post_id) de post_like garantit qu'on ne peut pas liker deux fois.
    @Transactional
    public LikeResponseDto basculerLike(Integer moiId, Integer postId) {
        User moi = trouverUtilisateurActif(moiId);
        PostForum post = trouverSujetPublie(postId);

        PostLikeId cle = new PostLikeId(moi.getId(), post.getId());
        boolean aime;
        if (postLikeRepository.existsById(cle)) {
            postLikeRepository.deleteById(cle);
            aime = false;
        } else {
            PostLike like = new PostLike(moi, post);
            like.setId(cle);
            postLikeRepository.save(like);
            aime = true;
        }
        // flush : on écrit tout de suite en base pour que le comptage tienne compte du clic
        postLikeRepository.flush();
        return new LikeResponseDto(post.getId(), aime, postLikeRepository.countByPostId(post.getId()));
    }

    // ------------------------------------------------------------ Outils

    private PostForumResponseDto versDto(PostForum post, Integer moiId) {
        long nombreLikes = postLikeRepository.countByPostId(post.getId());
        boolean likeParMoi = postLikeRepository.existsById(new PostLikeId(moiId, post.getId()));
        return PostForumResponseDto.from(post, nombreLikes, likeParMoi);
    }

    // Un événement (même id, autre type) ou un sujet annulé/archivé répond 404
    private PostForum trouverSujetPublie(Integer postId) {
        return postForumRepository.findById(postId)
                .filter(post -> post.getStatut() == StatutPost.PUBLIE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sujet de forum introuvable : " + postId));
    }

    // On relit le compte en base : un compte suspendu depuis sa connexion ne peut plus agir
    private User trouverUtilisateurActif(Integer userId) {
        return userRepository.findById(userId)
                .filter(user -> user.getStatut() == StatutUser.ACTIF)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Utilisateur inconnu ou inactif : " + userId));
    }

    // "?motCle=" (vide) doit se comporter comme "pas de filtre"
    private String videEnNull(String texte) {
        return (texte == null || texte.isBlank()) ? null : texte.trim();
    }
}
