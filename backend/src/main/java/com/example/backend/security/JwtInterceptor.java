package com.example.backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

// Intercepteur : voit passer TOUTES les requêtes avant les contrôleurs (enregistré dans SecurityConfig).
// Il vérifie le jeton "Authorization: Bearer <jeton>" et range l'utilisateur dans la requête.
// Il ne vérifie PAS les rôles : c'est le service qui décide qui a le droit de faire quoi.
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    // Nom de l'attribut où l'utilisateur connecté est rangé
    public static final String UTILISATEUR = "utilisateurConnecte";

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Requête "pré-vol" du navigateur (CORS) : elle n'a jamais de jeton, on la laisse passer
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        if (estRoutePublique(request)) {
            return true;
        }

        String entete = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (entete == null || !entete.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Jeton manquant : connectez-vous puis envoyez l'en-tête Authorization: Bearer <jeton>");
        }

        try {
            UtilisateurConnecte utilisateur = jwtService.lireJeton(entete.substring("Bearer ".length()));
            request.setAttribute(UTILISATEUR, utilisateur);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Jeton expiré : reconnectez-vous");
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Jeton invalide");
        }
    }

    // Les seules routes accessibles sans être connecté : se connecter et créer son compte
    private boolean estRoutePublique(HttpServletRequest request) {
        String methode = request.getMethod();
        String chemin = request.getRequestURI();
        return ("POST".equals(methode) && "/api/auth/login".equals(chemin))
                || ("POST".equals(methode) && "/users".equals(chemin));
    }
}
