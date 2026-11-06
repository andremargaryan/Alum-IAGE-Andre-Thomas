package com.example.backend.controller;

import com.example.backend.dto.response.InscriptionResponseDto;
import com.example.backend.dto.response.ParticipantsResponseDto;
import com.example.backend.security.JwtInterceptor;
import com.example.backend.security.UtilisateurConnecte;
import com.example.backend.service.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{id}/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptions;

    @PostMapping
    public InscriptionResponseDto inscrire(@PathVariable("id") Integer id,
            @RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi) {
        return inscriptions.inscrire(id, moi.userId());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desinscrire(@PathVariable("id") Integer id,
            @RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi) {
        inscriptions.desinscrire(id, moi.userId());
    }

    @GetMapping("/me")
    public InscriptionResponseDto consulter(@PathVariable("id") Integer id,
            @RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi) {
        return inscriptions.consulter(id, moi.userId());
    }

    @GetMapping
    public ParticipantsResponseDto lister(@PathVariable("id") Integer id,
            @RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi) {
        return inscriptions.listerParticipants(id, moi.userId());
    }
}
