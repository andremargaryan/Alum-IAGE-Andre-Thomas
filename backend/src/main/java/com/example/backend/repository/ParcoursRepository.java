package com.example.backend.repository;

import com.example.backend.entity.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcoursRepository extends JpaRepository<Parcours, Integer> {
}
