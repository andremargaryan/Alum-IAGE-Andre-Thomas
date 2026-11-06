package com.example.backend.service;

import com.example.backend.dto.response.AuditResponseDto;
import com.example.backend.entity.JournalAudit;
import com.example.backend.repository.JournalAuditRepository;
import com.example.backend.security.UtilisateurConnecte;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Écriture et lecture du journal d'audit (table journal_audit).
@Service
@RequiredArgsConstructor
public class AuditService {

    // Types d'actions enregistrées
    public static final String REQUETE_API = "REQUETE_API";
    public static final String USER_CREATED = "USER_CREATED";

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final JournalAuditRepository journalAuditRepository;

    // Ajoute une ligne au journal.
    // REQUIRES_NEW : l'audit a sa propre transaction, il ne peut pas annuler l'action qu'il trace.
    // Et si l'écriture échoue, on le signale dans les logs sans faire échouer la requête de l'utilisateur.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enregistrer(String action, Integer userId, String methode, String route, Integer statutHttp, String details) {
        try {
            JournalAudit ligne = new JournalAudit();
            ligne.setAction(action);
            ligne.setUserId(userId);
            ligne.setMethode(methode);
            ligne.setRoute(couper(route));
            ligne.setStatutHttp(statutHttp == null ? null : statutHttp.shortValue());
            ligne.setDetails(couper(details));
            journalAuditRepository.save(ligne);
        } catch (RuntimeException e) {
            log.warn("Écriture du journal d'audit impossible ({} {})", methode, route, e);
        }
    }

    // Lecture réservée aux administrateurs
    @Transactional(readOnly = true)
    public List<AuditResponseDto> dernieresLignes(UtilisateurConnecte moi) {
        if (!moi.estAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le journal d'audit est réservé aux administrateurs");
        }
        return journalAuditRepository.findTop100ByOrderByIdDesc().stream()
                .map(AuditResponseDto::from)
                .toList();
    }

    // Les colonnes route et details font 255 caractères au maximum
    private String couper(String texte) {
        return (texte == null || texte.length() <= 255) ? texte : texte.substring(0, 255);
    }
}
