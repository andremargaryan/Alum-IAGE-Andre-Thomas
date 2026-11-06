package com.example.backend.controller;

import com.example.backend.dto.response.AuditResponseDto;
import com.example.backend.security.JwtInterceptor;
import com.example.backend.security.UtilisateurConnecte;
import com.example.backend.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Consultation du journal d'audit : réservée aux ADMIN (vérifié dans AuditService)
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping                                                           // 200, ou 403
    public List<AuditResponseDto> dernieresLignes(@RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi) {
        return auditService.dernieresLignes(moi);
    }
}
