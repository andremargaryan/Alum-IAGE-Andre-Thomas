package com.example.backend.repository;

import com.example.backend.entity.PostEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostEvenementRepository extends JpaRepository<PostEvenement, Integer> {

    // Les événements à venir, du plus proche au plus lointain
    List<PostEvenement> findByDateEvenementAfterOrderByDateEvenementAsc(LocalDateTime date);
}
