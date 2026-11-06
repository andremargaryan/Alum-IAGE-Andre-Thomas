package com.example.backend.messaging;

import com.example.backend.config.JmsConfig;
import com.example.backend.dto.event.UserCreatedEvent;
import com.example.backend.entity.LiaisonNotification;
import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.repository.LiaisonNotificationRepository;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AuditService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EventConsumer {

    private final NotificationRepository notificationRepository;
    private final LiaisonNotificationRepository liaisonNotificationRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public EventConsumer(NotificationRepository notificationRepository,
                         LiaisonNotificationRepository liaisonNotificationRepository,
                         UserRepository userRepository,
                         AuditService auditService) {
        this.notificationRepository = notificationRepository;
        this.liaisonNotificationRepository = liaisonNotificationRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @JmsListener(destination = JmsConfig.ALUMNI_QUEUE)
    @Transactional
    public void onUserCreated(UserCreatedEvent event) {
        // 1. Créer la notification
        Notification notif = new Notification();
        notif.setTitre("Bienvenue sur Alumni !");
        notif.setMessage("Ravi de vous compter parmi nous, " + event.getPrenom() + " !");
        Notification savedNotif = notificationRepository.save(notif);

        // 2. Créer la liaison pour cet utilisateur
        User user = userRepository.findById(event.getUserId()).orElse(null);
        if (user != null) {
            LiaisonNotification liaison = new LiaisonNotification(savedNotif, user, false);
            liaisonNotificationRepository.save(liaison);
        }

        // 3. Tracer la création dans le journal d'audit : "l'utilisateur X a été créé"
        auditService.enregistrer(AuditService.USER_CREATED, event.getUserId(), null, null, null,
                "Utilisateur " + event.getUserId() + " créé : " + event.getPrenom() + " " + event.getNom()
                        + " (" + event.getEmail() + ", " + event.getRole() + ")");
    }
}
