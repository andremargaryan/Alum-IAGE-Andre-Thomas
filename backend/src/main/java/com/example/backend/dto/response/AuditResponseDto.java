package com.example.backend.dto.response;

import com.example.backend.entity.JournalAudit;

import java.time.LocalDateTime;

// Une ligne du journal d'audit, telle que renvoyée par GET /api/audit
public record AuditResponseDto(
        Integer id,
        LocalDateTime dateAction,
        Integer userId,
        String action,
        String methode,
        String route,
        Short statutHttp,
        String details
) {
    public static AuditResponseDto from(JournalAudit ligne) {
        return new AuditResponseDto(ligne.getId(), ligne.getDateAction(), ligne.getUserId(), ligne.getAction(),
                ligne.getMethode(), ligne.getRoute(), ligne.getStatutHttp(), ligne.getDetails());
    }
}
