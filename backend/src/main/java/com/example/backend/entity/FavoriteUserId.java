package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Clé primaire composée de favorite_user : le couple (user_id, favorite_user_id).
// JPA impose pour ce genre de classe : Serializable + equals/hashCode (fournis par Lombok).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FavoriteUserId implements Serializable {

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "favorite_user_id")
    private Integer favoriteUserId;
}
