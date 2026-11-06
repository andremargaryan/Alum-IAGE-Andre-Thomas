package com.example.backend.service;

import com.example.backend.dto.AuthResponseDto;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto authenticate(LoginRequestDto loginDto) {
        // 1. Recherche par email
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));

        // 2. Vérification de l'état du compte (si un champ/méthode statut existe)
        if (user.getStatut() != null) {
            String statut = user.getStatut().name();
            if ("SUSPENDU".equalsIgnoreCase(statut) || "SUPPRIME".equalsIgnoreCase(statut)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ce compte est désactivé ou supprimé");
            }
        }

        // 3. Vérification du mot de passe haché avec BCrypt
        if (!passwordEncoder.matches(loginDto.getMotDePasse(), user.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }

        // 4. Construction de la réponse
        return AuthResponseDto.builder()
                .userId(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole())
                .ville(user.getVille())
                .build();
    }
}