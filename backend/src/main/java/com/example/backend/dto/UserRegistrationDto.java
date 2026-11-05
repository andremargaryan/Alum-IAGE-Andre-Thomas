package com.example.backend.dto;

import com.example.backend.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRegistrationDto {

    @NotBlank
    @Size(max = 80)
    private String prenom;

    @NotBlank
    @Size(max = 80)
    private String nom;

    @NotBlank
    @Email
    @Size(max = 180)
    private String email;

    @NotBlank
    @Size(min = 6)
    private String motDePasse;

    private LocalDate dateNaissance;

    @Size(max = 100)
    private String ville;

    private Role role;
}
