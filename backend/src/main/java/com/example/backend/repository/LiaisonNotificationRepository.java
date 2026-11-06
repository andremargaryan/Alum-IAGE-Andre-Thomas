package com.example.backend.repository;

import com.example.backend.entity.LiaisonNotification;
import com.example.backend.entity.LiaisonNotificationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiaisonNotificationRepository extends JpaRepository<LiaisonNotification, LiaisonNotificationId> {

    // Toutes les notifications d'un utilisateur
    List<LiaisonNotification> findByUserId(Integer userId);

    // Compteur de la cloche : nombre de notifications non lues
    long countByUserIdAndReadFalse(Integer userId);
}
