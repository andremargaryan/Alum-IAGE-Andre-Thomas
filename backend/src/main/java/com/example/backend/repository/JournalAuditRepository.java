package com.example.backend.repository;

import com.example.backend.entity.JournalAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalAuditRepository extends JpaRepository<JournalAudit, Integer> {

    // Les 100 dernières lignes, de la plus récente à la plus ancienne
    List<JournalAudit> findTop100ByOrderByIdDesc();
}
