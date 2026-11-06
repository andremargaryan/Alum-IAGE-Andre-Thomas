package com.example.backend.dto.response;

import com.example.backend.entity.StatutInscription;

public record InscriptionResponseDto(StatutInscription statut, Integer rang) {
}
