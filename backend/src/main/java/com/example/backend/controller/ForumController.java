package com.example.backend.controller;

import com.example.backend.dto.request.PostForumCreateDto;
import com.example.backend.dto.response.LikeResponseDto;
import com.example.backend.dto.response.PostForumResponseDto;
import com.example.backend.security.JwtInterceptor;
import com.example.backend.security.UtilisateurConnecte;
import com.example.backend.service.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Forum : publication de sujets, consultation par thématique, likes.
// Toutes ces routes demandent le jeton JWT (vérifié par JwtInterceptor).
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    // L'auteur est l'utilisateur du jeton. Tout utilisateur actif peut publier.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)                                   // 201, ou 400 / 401
    public PostForumResponseDto publier(@RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi,
                                        @Valid @RequestBody PostForumCreateDto dto) {
        return forumService.publierSujet(moi.userId(), dto);
    }

    // Exemples : GET /api/posts                 -> tous les sujets, du plus récent au plus ancien
    //            GET /api/posts?categorieId=5   -> seulement la thématique "Emploi"
    //            GET /api/posts?motCle=java
    @GetMapping                                                           // 200
    public List<PostForumResponseDto> lister(@RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi,
                                             @RequestParam(name = "categorieId", required = false) Integer categorieId,
                                             @RequestParam(name = "motCle", required = false) String motCle) {
        return forumService.rechercher(moi.userId(), categorieId, motCle);
    }

    @GetMapping("/{id}")                                                  // 200, ou 404
    public PostForumResponseDto detail(@RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi,
                                       @PathVariable("id") Integer id) {
        return forumService.getSujet(moi.userId(), id);
    }

    // Un clic = like si pas encore liké, unlike sinon. La réponse donne l'état après le clic.
    @PostMapping("/{id}/like")                                            // 200, ou 404
    public LikeResponseDto basculerLike(@RequestAttribute(JwtInterceptor.UTILISATEUR) UtilisateurConnecte moi,
                                        @PathVariable("id") Integer id) {
        return forumService.basculerLike(moi.userId(), id);
    }
}
