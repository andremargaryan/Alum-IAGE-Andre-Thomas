package com.example.backend.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

// Format UNIQUE de toutes les erreurs renvoyées par l'API (construit par GlobalExceptionHandler).
// details : seulement pour les erreurs de validation, sinon null. Ex : {"email": "Format d'email invalide"}
public record ErreurDto(
        LocalDateTime timestamp,
        int status,
        String erreur,
        String message,
        String chemin,
        Map<String, String> details
) {
}
