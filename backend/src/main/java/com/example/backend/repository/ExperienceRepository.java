package com.example.backend.repository;

import com.example.backend.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Integer> {

    // Les expériences d'un utilisateur (Spring génère : WHERE user_id = ?)
    List<Experience> findByUserId(Integer userId);
}
