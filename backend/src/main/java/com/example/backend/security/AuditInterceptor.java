package com.example.backend.security;

import com.example.backend.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// Deuxième intercepteur : une fois la réponse prête, il écrit une ligne dans journal_audit
// (qui, quelle route, quel code de retour). Il est enregistré AVANT JwtInterceptor dans SecurityConfig :
// il trace donc aussi les requêtes refusées par JwtInterceptor (401).
@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    private final AuditService auditService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Les requêtes "pré-vol" CORS du navigateur ne sont pas des actions de l'utilisateur
        if ("OPTIONS".equals(request.getMethod())) {
            return;
        }
        // Rangé par JwtInterceptor ; null si la route est publique ou le jeton refusé
        UtilisateurConnecte utilisateur = (UtilisateurConnecte) request.getAttribute(JwtInterceptor.UTILISATEUR);
        auditService.enregistrer(AuditService.REQUETE_API,
                utilisateur == null ? null : utilisateur.userId(),
                request.getMethod(),
                request.getRequestURI(),           // sans les paramètres ?..., pour ne rien stocker de sensible
                response.getStatus(),
                null);
    }
}
