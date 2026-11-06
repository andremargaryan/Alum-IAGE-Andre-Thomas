package com.example.backend.repository;

import com.example.backend.entity.PostEvenement;
import com.example.backend.entity.StatutPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostEvenementRepository extends JpaRepository<PostEvenement, Integer> {

    // Les événements à venir, du plus proche au plus lointain
    List<PostEvenement> findByDateEvenementAfterOrderByDateEvenementAsc(LocalDateTime date);

    // Recherche avec filtres facultatifs : un filtre à null est ignoré.
    //  - motCle : cherché dans le titre et la description
    //  - lieu   : cherché dans le lieu et l'adresse (donc aussi la ville)
    //  - depuis : seulement les événements à partir de cette date
    // JOIN FETCH charge la catégorie et l'auteur dans la même requête (sinon une requête de plus par événement).
    @Query("""
            SELECT e FROM PostEvenement e
            JOIN FETCH e.categorie
            JOIN FETCH e.user
            WHERE e.statut = :statut
              AND (:motCle IS NULL
                   OR LOWER(e.titre) LIKE LOWER(CONCAT('%', :motCle, '%'))
                   OR LOWER(e.description) LIKE LOWER(CONCAT('%', :motCle, '%')))
              AND (:lieu IS NULL
                   OR LOWER(e.lieu) LIKE LOWER(CONCAT('%', :lieu, '%'))
                   OR LOWER(e.adresse) LIKE LOWER(CONCAT('%', :lieu, '%')))
              AND (:depuis IS NULL OR e.dateEvenement >= :depuis)
            ORDER BY e.dateEvenement ASC
            """)
    List<PostEvenement> rechercher(@Param("statut") StatutPost statut,
                                   @Param("motCle") String motCle,
                                   @Param("lieu") String lieu,
                                   @Param("depuis") LocalDateTime depuis);
}
