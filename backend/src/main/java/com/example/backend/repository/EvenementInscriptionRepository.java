package com.example.backend.repository;

import com.example.backend.entity.EvenementInscription;
import com.example.backend.entity.EvenementInscriptionId;
import com.example.backend.entity.StatutInscription;
import com.example.backend.entity.StatutUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementInscriptionRepository extends JpaRepository<EvenementInscription, EvenementInscriptionId> {

    // Nombre de places prises : countByEvenementIdAndStatut(42, StatutInscription.INSCRIT)
    long countByEvenementIdAndStatut(Integer evenementId, StatutInscription statut);

    // File d'attente dans l'ordre d'arrivée : le premier de la liste est le prochain servi
    List<EvenementInscription> findByEvenementIdAndStatutOrderByDateInscriptionAscIdUserIdAsc(
            Integer evenementId, StatutInscription statut);

    @EntityGraph(attributePaths = "user")
    List<EvenementInscription> findByEvenementIdAndStatutAndUserStatutOrderByIdUserIdAsc(
            Integer evenementId, StatutInscription statut, StatutUser statutUser);

    // Inscriptions d'un utilisateur (historique)
    List<EvenementInscription> findByUserId(Integer userId);
}
