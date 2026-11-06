package com.example.backend.dto.response;

import com.example.backend.entity.PostForum;
import com.example.backend.entity.StatutPost;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Ce que l'API renvoie pour un sujet de forum.
@Data
@NoArgsConstructor
public class PostForumResponseDto {
    private Integer id;
    private String titre;
    private String description;
    private Integer categorieId;
    private String categorieLibelle;
    private StatutPost statut;
    private Integer auteurId;
    private String auteurNom;               // "Hugo Dubois"
    private LocalDateTime createdAt;

    // Calculés à partir de la table post_like (jamais stockés en base)
    private long nombreLikes;
    private boolean likeParMoi;             // l'utilisateur connecté a-t-il liké ? (pour colorer le bouton côté front)

    // A appeler dans une méthode @Transactional : categorie et user sont en LAZY.
    public static PostForumResponseDto from(PostForum post, long nombreLikes, boolean likeParMoi) {
        PostForumResponseDto dto = new PostForumResponseDto();
        dto.setId(post.getId());
        dto.setTitre(post.getTitre());
        dto.setDescription(post.getDescription());
        dto.setCategorieId(post.getCategorie().getId());
        dto.setCategorieLibelle(post.getCategorie().getLibelle());
        dto.setStatut(post.getStatut());
        dto.setAuteurId(post.getUser().getId());
        dto.setAuteurNom(post.getUser().getPrenom() + " " + post.getUser().getNom());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setNombreLikes(nombreLikes);
        dto.setLikeParMoi(likeParMoi);
        return dto;
    }
}
