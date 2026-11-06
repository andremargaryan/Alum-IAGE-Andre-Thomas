package com.example.backend.controller;

import com.example.backend.dto.request.PostEvenementCreateDto;
import com.example.backend.dto.response.PostEvenementResponseDto;
import com.example.backend.security.JwtInterceptor;
import com.example.backend.security.UtilisateurConnecte;
import com.example.backend.service.EvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Événements : création (organisateurs et admins) et consultation (tout utilisateur connecté).
// Toutes ces routes demandent le jeton JWT (vérifié par JwtInterceptor).
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EvenementController {

    private final EvenementService evenementService;

    // L'auteur est l'utilisateur du jeton : impossible de créer un événement au nom de quelqu'un d'autre.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)                                   // 201, ou 400 / 401 / 403
    public PostEvenementResponseDto creer(@RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi,
                                          @Valid @RequestBody PostEvenementCreateDto dto) {
        return evenementService.creerEvenement(moi.userId(), dto);
    }

    // Exemples : GET /api/events            -> tous les événements à venir
    //            GET /api/events?motCle=ia  -> ceux qui parlent d'IA
    //            GET /api/events?lieu=Paris&aVenir=false
    @GetMapping                                                           // 200
    public List<PostEvenementResponseDto> lister(@RequestParam(name = "motCle", required = false) String motCle,
                                                 @RequestParam(name = "lieu", required = false) String lieu,
                                                 @RequestParam(name = "aVenir", defaultValue = "true") boolean aVenir) {
        return evenementService.rechercher(motCle, lieu, aVenir);
    }

    @GetMapping("/{id}")                                                  // 200, ou 404
    public PostEvenementResponseDto detail(@PathVariable("id") Integer id) {
        return evenementService.getEvenement(id);
    }
}
