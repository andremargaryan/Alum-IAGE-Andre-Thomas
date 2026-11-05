package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;

// Classe fille : un post de forum. Pas de champ propre pour l'instant :
// les likes sont dans la table post_like (une ligne par personne), pas dans un compteur.
@Entity
@Table(name = "post_forum")
@DiscriminatorValue("FORUM")
@PrimaryKeyJoinColumn(name = "post_id")
@NoArgsConstructor
@ToString(callSuper = true)
public class PostForum extends Post {
}
