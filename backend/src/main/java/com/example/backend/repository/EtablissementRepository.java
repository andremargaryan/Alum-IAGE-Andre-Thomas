package com.example.backend.repository;

import com.example.backend.entity.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtablissementRepository extends JpaRepository<Etablissement, Integer> {
}
