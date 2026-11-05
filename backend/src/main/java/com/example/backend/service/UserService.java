package com.example.backend.service;

import com.example.backend.dto.UserResponseDto;
import com.example.backend.entity.StatutUser;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
