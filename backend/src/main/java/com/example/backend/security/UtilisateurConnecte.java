package com.example.backend.security;

import com.example.backend.entity.Role;

// Qui fait la requête, d'après le jeton JWT. Rempli par JwtInterceptor, lu par les contrôleurs :
//   public ... maMethode(@RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi)
public record UtilisateurConnecte(Integer userId, Role role) {

    public boolean estAdmin() {
        return role == Role.ADMIN;
    }

    // ex : moi.aUnDesRoles(Role.ORGANISATEUR, Role.ADMIN)
    public boolean aUnDesRoles(Role... roles) {
        for (Role r : roles) {
            if (r == role) {
                return true;
            }
        }
        return false;
    }
}
