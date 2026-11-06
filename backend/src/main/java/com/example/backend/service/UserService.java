package com.example.backend.service;

import com.example.backend.dto.UserRegistrationDto;
import com.example.backend.dto.UserResponseDto;
import com.example.backend.dto.UserUpdateDto;
import com.example.backend.entity.Role;
import com.example.backend.entity.StatutUser;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(UserRegistrationDto registration) {
        if (registration.getRole() != null && registration.getRole() != Role.ETUDIANT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le rôle doit être ETUDIANT.");
        }

        String password = registration.getMotDePasse();
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le mot de passe dépasse 72 octets.");
        }

        String email = registration.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé.");
        }

        User user = new User();
        user.setPrenom(registration.getPrenom());
        user.setNom(registration.getNom());
        user.setEmail(email);
        user.setMotDePasse(passwordEncoder.encode(password));
        user.setDateNaissance(registration.getDateNaissance());
        user.setVille(registration.getVille());

        try {
            return toResponseDto(userRepository.save(user));
        } catch (DataIntegrityViolationException exception) {
            // Deux inscriptions peuvent passer le premier contrôle en même temps.
            if (userRepository.existsByEmail(email)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé.", exception);
            }
            throw exception;
        }
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findByStatut(StatutUser.ACTIF).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public Optional<UserResponseDto> getUserById(Integer id) {
        return userRepository.findById(id)
                .filter(user -> user.getStatut() != StatutUser.SUPPRIME)
                .map(this::toResponseDto);
    }

    @Transactional
    public UserResponseDto updateUser(Integer id, UserUpdateDto update) {
        User user = findUserForWrite(id);
        user.setPrenom(update.prenom());
        user.setNom(update.nom());
        user.setVille(update.ville());
        user.setDateNaissance(update.dateNaissance());
        user.setNotificationsActives(update.notificationsActives());
        return toResponseDto(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = findUserForWrite(id);
        user.setStatut(StatutUser.SUPPRIME);
        user.setDeletedAt(LocalDateTime.now());
    }

    private User findUserForWrite(Integer id) {
        return userRepository.findByIdAndStatutNot(id, StatutUser.SUPPRIME)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));
    }

    private UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getPrenom(),
                user.getNom(),
                user.getEmail(),
                user.getVille(),
                user.getRole(),
                user.getStatut(),
                user.getCreatedAt()
        );
    }
}
