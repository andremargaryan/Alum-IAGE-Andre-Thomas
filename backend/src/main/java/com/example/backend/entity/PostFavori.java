package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Un post (événement ou forum) mis en favori par un utilisateur
@Entity
@Table(name = "post_favori")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PostFavori {

    @EmbeddedId
    private PostFavoriId id = new PostFavoriId();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    public PostFavori(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
