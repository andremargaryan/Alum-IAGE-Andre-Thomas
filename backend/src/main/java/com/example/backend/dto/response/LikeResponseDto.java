package com.example.backend.dto.response;

// Réponse de POST /api/posts/{id}/like : l'état du like APRÈS le clic.
//   aime = true  -> le like vient d'être ajouté
//   aime = false -> le like vient d'être retiré
public record LikeResponseDto(Integer postId, boolean aime, long nombreLikes) {
}
