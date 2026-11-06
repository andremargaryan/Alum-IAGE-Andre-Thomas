package com.example.backend.service;

import com.example.backend.dto.AuthResponseDto;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.entity.StatutUser;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto authenticate(LoginRequestDto loginDto) {
        // 1. Recherche par email
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));

        // 2. Seuls les comptes actifs peuvent se connecter
        if (user.getStatut() != StatutUser.ACTIF) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ce compte est désactivé ou supprimé");
        }

        // 3. Vérification du mot de passe saisi avec BCrypt
        if (!passwordEncoder.matches(loginDto.getMotDePasse(), user.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }

        // 4. Construction de la réponse, avec le jeton JWT signé
        return AuthResponseDto.builder()
                .token(jwtService.genererJeton(user))
                .expireDansMinutes(jwtService.getDureeMinutes())
                .userId(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole())
                .ville(user.getVille())
                .build();
    }
}
