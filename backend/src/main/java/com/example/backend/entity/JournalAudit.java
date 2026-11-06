package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

// Journal d'audit : une ligne par requête sur l'API, et une par compte créé (via JMS).
// On n'y modifie ni n'y supprime jamais rien : on ne fait qu'ajouter des lignes.
@Entity
@Table(name = "journal_audit")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Integer id;

    // Remplie par MySQL (DEFAULT CURRENT_TIMESTAMP(3)) : Java ne l'écrit jamais
    @Column(name = "date_action", insertable = false, updatable = false)
    private LocalDateTime dateAction;

    // Un simple nombre, pas une relation vers User : le journal doit survivre à la suppression du compte
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, length = 30)
    private String action;              // REQUETE_API ou USER_CREATED

    @Column(length = 10)
    private String methode;             // GET, POST...

    @Column(length = 255)
    private String route;               // /api/events

    @Column(name = "statut_http")
    private Short statutHttp;           // 200, 401...

    @Column(length = 255)
    private String details;             // texte libre
}
