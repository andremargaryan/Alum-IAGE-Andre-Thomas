package com.example.backend.repository;

import com.example.backend.entity.Categorie;
import com.example.backend.entity.TypePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategorieRepository extends JpaRepository<Categorie, Integer> {

    // Pour les listes déroulantes : findByTypePost(TypePost.EVENEMENT)
    List<Categorie> findByTypePost(TypePost typePost);
}
