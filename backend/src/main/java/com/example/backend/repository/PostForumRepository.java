package com.example.backend.repository;

import com.example.backend.entity.PostForum;
import com.example.backend.entity.StatutPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostForumRepository extends JpaRepository<PostForum, Integer> {

    // Les posts du forum, du plus récent au plus ancien
    List<PostForum> findAllByOrderByCreatedAtDesc();

    // Recherche avec filtres facultatifs : un filtre à null est ignoré.
    //  - categorieId : une seule thématique (Emploi, Entraide...)
    //  - motCle      : cherché dans le titre et la description
    // JOIN FETCH charge la catégorie et l'auteur dans la même requête.
    @Query("""
            SELECT p FROM PostForum p
            JOIN FETCH p.categorie
            JOIN FETCH p.user
            WHERE p.statut = :statut
              AND (:categorieId IS NULL OR p.categorie.id = :categorieId)
              AND (:motCle IS NULL
                   OR LOWER(p.titre) LIKE LOWER(CONCAT('%', :motCle, '%'))
                   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :motCle, '%')))
            ORDER BY p.createdAt DESC
            """)
    List<PostForum> rechercher(@Param("statut") StatutPost statut,
                               @Param("categorieId") Integer categorieId,
                               @Param("motCle") String motCle);
}
