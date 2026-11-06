package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Clé primaire composée de liaison_notification : le couple (notif_id, user_id).
// JPA impose pour ce genre de classe : Serializable + equals/hashCode (fournis par Lombok).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LiaisonNotificationId implements Serializable {

    @Column(name = "notif_id")
    private Integer notifId;

    @Column(name = "user_id")
    private Integer userId;
}
