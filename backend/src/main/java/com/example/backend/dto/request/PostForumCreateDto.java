package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

// Ce que le front envoie pour publier un sujet de forum (POST /api/posts).
// L'auteur n'est pas dans le JSON : c'est l'utilisateur du jeton JWT.
@Data
@NoArgsConstructor
public class PostForumCreateDto {

    @NotBlank
    @Size(max = 200)
    private String titre;

    @NotBlank
    private String description;

    @NotNull
    private Integer categorieId;            // une catégorie de type FORUM (Emploi, Entraide...)
}
