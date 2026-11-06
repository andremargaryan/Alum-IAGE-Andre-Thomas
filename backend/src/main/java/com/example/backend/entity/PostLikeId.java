package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Clé primaire composée de post_like : le couple (user_id, post_id).
// JPA impose pour ce genre de classe : Serializable + equals/hashCode (fournis par Lombok).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostLikeId implements Serializable {

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "post_id")
    private Integer postId;
}
