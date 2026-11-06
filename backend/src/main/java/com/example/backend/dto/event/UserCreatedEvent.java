package com.example.backend.dto.event;

import com.example.backend.entity.Role;
import java.io.Serializable;
import java.time.LocalDateTime;

public class UserCreatedEvent implements Serializable {

    private Integer userId;
    private LocalDateTime timestamp;
    private String eventType;
    private String nom;
    private String prenom;
    private String email;
    private Role role;

    public UserCreatedEvent() {
    }

    public UserCreatedEvent(Integer userId, LocalDateTime timestamp, String eventType, 
                            String nom, String prenom, String email, Role role) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}