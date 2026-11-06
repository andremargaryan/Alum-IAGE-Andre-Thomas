package com.example.backend.dto.response;

import java.util.List;

public record ParticipantsResponseDto(List<Participant> participants, long nombreEnAttente) {
    public record Participant(String nom, String prenom) {
    }
}
