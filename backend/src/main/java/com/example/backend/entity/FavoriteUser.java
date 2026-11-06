package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Un alumni mis en favori par un autre : user met favoriteUser en favori.
// Règle à vérifier dans le service : on ne se met pas soi-même en favori.
@Entity
@Table(name = "favorite_user")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FavoriteUser {

    @EmbeddedId
    private FavoriteUserId id = new FavoriteUserId();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("favoriteUserId")
    @JoinColumn(name = "favorite_user_id")
    private User favoriteUser;

    public FavoriteUser(User user, User favoriteUser) {
        this.user = user;
        this.favoriteUser = favoriteUser;
    }
}
