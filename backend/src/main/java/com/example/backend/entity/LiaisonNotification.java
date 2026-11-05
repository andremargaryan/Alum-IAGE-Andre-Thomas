package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Qui reçoit quelle notification, et s'il l'a lue
@Entity
@Table(name = "liaison_notification")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LiaisonNotification {

    @EmbeddedId
    private LiaisonNotificationId id = new LiaisonNotificationId();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("notifId")
    @JoinColumn(name = "notif_id")
    private Notification notification;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;                       // Lombok génère isRead() / setRead()

    // Constructeur pratique : new LiaisonNotification(notification, user, false)
    public LiaisonNotification(Notification notification, User user, boolean read) {
        this.notification = notification;
        this.user = user;
        this.read = read;
    }
}
