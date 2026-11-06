package com.example.backend.dto;

import com.example.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private Integer userId;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private String ville;
}