package com.example.backend.repository;

import com.example.backend.entity.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Integer> {

    // Les commentaires d'un post, du plus ancien au plus récent
    List<Commentaire> findByPostIdOrderByCreatedAtAsc(Integer postId);
}
