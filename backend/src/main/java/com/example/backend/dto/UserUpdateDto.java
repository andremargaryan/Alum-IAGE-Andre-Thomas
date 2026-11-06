package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDto(
        @NotBlank @Size(max = 80) String prenom,
        @NotBlank @Size(max = 80) String nom,
        @Size(max = 100) String ville,
        LocalDate dateNaissance,
        @NotNull Boolean notificationsActives
) {
}
