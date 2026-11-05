package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Clé primaire composée de evenement_inscription : le couple (post_id, user_id).
// JPA impose pour ce genre de classe : Serializable + equals/hashCode (fournis par Lombok).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EvenementInscriptionId implements Serializable {

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "user_id")
    private Integer userId;
}
