package com.example.backend.repository;

import com.example.backend.entity.Formation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, Integer> {

    // Les formations d'un utilisateur (Spring génère : WHERE user_id = ?)
    List<Formation> findByUserId(Integer userId);
}
