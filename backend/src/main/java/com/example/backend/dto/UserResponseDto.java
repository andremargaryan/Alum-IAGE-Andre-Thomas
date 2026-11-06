package com.example.backend.dto;

import com.example.backend.entity.Role;
import com.example.backend.entity.StatutUser;

import java.time.LocalDateTime;

public record UserResponseDto(
        Integer id,
        String prenom,
        String nom,
        String email,
        String ville,
        Role role,
        StatutUser statut,
        LocalDateTime createdAt
) {
}
