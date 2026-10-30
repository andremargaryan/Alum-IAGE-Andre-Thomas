-- =====================================================================
--  PROJET ALUMNI — Schéma v6
--  MySQL 8.4 (testé sur MySQL 8.0) — InnoDB — utf8mb4
--  Exécuté automatiquement par MySQL au 1er démarrage du conteneur (volume vide).
--  Documentation complète : "Alumni — Documentation de la base de données".
--
--  v6 : formation = cursus Master MIAGE uniquement (université + parcours, listes fermées) ;
--       catégories en table (une liste par type de post) ; type de post + triggers
--       qui protègent l'héritage ; compteurs supprimés (COUNT / post_like) ;
--       liste d'attente ordonnée par date ; suppression logique des comptes.
-- =====================================================================

SET NAMES utf8mb4;   -- les fichiers sont en UTF-8 : sans cette ligne, les accents sont mal lus
USE alumni;

-- ---------------------------------------------------------------------
-- UTILISATEURS
-- ---------------------------------------------------------------------

-- "users" et non "user" : USER est réservé en PostgreSQL/H2 et c'est une table système MySQL
CREATE TABLE users (
    user_id               INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    prenom                VARCHAR(80)   NOT NULL,
    nom                   VARCHAR(80)   NOT NULL,
    date_naissance        DATE          NULL,
    email                 VARCHAR(180)  NOT NULL,
    mot_de_passe          VARCHAR(255)  NOT NULL,              -- hash BCrypt
    role                  ENUM('ETUDIANT','ORGANISATEUR','ADMIN') NOT NULL DEFAULT 'ETUDIANT',
    statut                ENUM('ACTIF','SUSPENDU','SUPPRIME')     NOT NULL DEFAULT 'ACTIF',
    notifications_actives BOOLEAN       NOT NULL DEFAULT TRUE,
    ville                 VARCHAR(100)  NULL,
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at            DATETIME      NULL,                  -- rempli quand statut passe à SUPPRIME
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email),
    KEY idx_users_ville (ville),
    KEY idx_users_statut (statut)
) ENGINE=InnoDB;

CREATE TABLE favorite_user (
    user_id           INT UNSIGNED NOT NULL,
    favorite_user_id  INT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, favorite_user_id),
    KEY idx_fav_user_dst (favorite_user_id),
    CONSTRAINT fk_fav_user_src FOREIGN KEY (user_id)          REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_user_dst FOREIGN KEY (favorite_user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PARCOURS
-- ---------------------------------------------------------------------

CREATE TABLE experience (
    experience_id  INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    user_id        INT UNSIGNED  NOT NULL,
    entreprise     VARCHAR(150)  NOT NULL,
    poste          VARCHAR(150)  NOT NULL,
    annee_debut    SMALLINT      NOT NULL,
    annee_fin      SMALLINT      NULL,                         -- NULL = poste actuel
    commentaire    TEXT          NULL,
    PRIMARY KEY (experience_id),
    KEY idx_experience_entreprise (entreprise),
    CONSTRAINT fk_experience_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT ck_experience_annees CHECK (annee_fin IS NULL OR annee_fin >= annee_debut)
) ENGINE=InnoDB;

-- Référentiel : universités du réseau MIAGE (géré par l'admin)
CREATE TABLE etablissement (
    etablissement_id  INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    nom               VARCHAR(150)  NOT NULL,
    ville             VARCHAR(100)  NULL,
    PRIMARY KEY (etablissement_id),
    UNIQUE KEY uk_etablissement_nom (nom)
) ENGINE=InnoDB;

-- Référentiel : parcours du Master MIAGE, propres à chaque université (géré par l'admin)
CREATE TABLE parcours (
    parcours_id       INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    etablissement_id  INT UNSIGNED  NOT NULL,
    code              VARCHAR(20)   NOT NULL,          -- "IBI"
    intitule          VARCHAR(150)  NOT NULL,          -- "Intelligent Business Informatics"
    actif             BOOLEAN       NOT NULL DEFAULT TRUE,  -- FALSE = parcours fermé, gardé pour les anciennes promos
    PRIMARY KEY (parcours_id),
    UNIQUE KEY uk_parcours_code (etablissement_id, code),
    UNIQUE KEY uk_parcours_etab (parcours_id, etablissement_id),   -- cible de la clé étrangère composée de formation
    CONSTRAINT fk_parcours_etab FOREIGN KEY (etablissement_id) REFERENCES etablissement (etablissement_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Formation = le cursus Master MIAGE d'un utilisateur (0..n : M1 et M2 peuvent se faire dans deux universités)
CREATE TABLE formation (
    formation_id      INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    user_id           INT UNSIGNED  NOT NULL,
    etablissement_id  INT UNSIGNED  NOT NULL,
    parcours_id       INT UNSIGNED  NULL,              -- NULL si le parcours n'est pas (encore) dans la liste
    annee_debut       SMALLINT      NOT NULL,
    annee_fin         SMALLINT      NULL,              -- NULL = en cours ; sinon = promo
    commentaire       TEXT          NULL,
    PRIMARY KEY (formation_id),
    KEY idx_formation_user (user_id),
    KEY idx_formation_etab (etablissement_id),
    KEY idx_formation_promo (annee_fin),
    CONSTRAINT fk_formation_user FOREIGN KEY (user_id)          REFERENCES users (user_id)                  ON DELETE CASCADE,
    CONSTRAINT fk_formation_etab FOREIGN KEY (etablissement_id) REFERENCES etablissement (etablissement_id) ON DELETE RESTRICT,
    -- le parcours doit appartenir à l'université choisie
    CONSTRAINT fk_formation_parcours FOREIGN KEY (parcours_id, etablissement_id)
        REFERENCES parcours (parcours_id, etablissement_id) ON DELETE RESTRICT,
    CONSTRAINT ck_formation_annees CHECK (annee_fin IS NULL OR annee_fin >= annee_debut)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- POSTS : POST parent (+ type_post), POST_EVENEMENT / POST_FORUM enfants
-- JPA : @Inheritance(JOINED) + @DiscriminatorColumn(name = "type_post")
-- ---------------------------------------------------------------------

-- Catégories gérées par l'admin, une liste par type de post
CREATE TABLE categorie (
    categorie_id  INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    libelle       VARCHAR(60)   NOT NULL,
    type_post     ENUM('EVENEMENT','FORUM') NOT NULL,
    PRIMARY KEY (categorie_id),
    UNIQUE KEY uk_categorie_libelle (libelle, type_post),
    UNIQUE KEY uk_categorie_type (categorie_id, type_post)    -- cible de la clé étrangère composée de post
) ENGINE=InnoDB;

CREATE TABLE post (
    post_id       INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    user_id       INT UNSIGNED  NOT NULL,
    type_post     ENUM('EVENEMENT','FORUM') NOT NULL,          -- discriminant de l'héritage
    categorie_id  INT UNSIGNED  NOT NULL,
    titre         VARCHAR(200)  NOT NULL,
    description   TEXT          NOT NULL,
    statut        ENUM('PUBLIE','ANNULE','ARCHIVE') NOT NULL DEFAULT 'PUBLIE',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id),
    KEY idx_post_type_date (type_post, created_at),
    KEY idx_post_user (user_id),
    -- RESTRICT : un compte qui a publié ne peut pas être supprimé physiquement (suppression logique)
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT,
    -- la catégorie doit être du même type que le post (un événement ne peut pas être classé "Emploi")
    CONSTRAINT fk_post_categorie FOREIGN KEY (categorie_id, type_post)
        REFERENCES categorie (categorie_id, type_post) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE post_evenement (
    post_id         INT UNSIGNED  NOT NULL,
    date_evenement  DATETIME      NOT NULL,
    lieu            VARCHAR(150)  NOT NULL,
    adresse         VARCHAR(255)  NOT NULL,
    capacite_max    INT UNSIGNED  NULL,                         -- NULL = illimité
    version         INT UNSIGNED  NOT NULL DEFAULT 0,           -- verrou optimiste JPA (@Version)
    PRIMARY KEY (post_id),
    KEY idx_evt_date (date_evenement),
    CONSTRAINT fk_evt_post FOREIGN KEY (post_id) REFERENCES post (post_id) ON DELETE CASCADE,
    CONSTRAINT ck_evt_capacite CHECK (capacite_max IS NULL OR capacite_max > 0)
) ENGINE=InnoDB;

CREATE TABLE post_forum (
    post_id  INT UNSIGNED NOT NULL,
    PRIMARY KEY (post_id),
    CONSTRAINT fk_forum_post FOREIGN KEY (post_id) REFERENCES post (post_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Héritage protégé : un post n'entre que dans la table enfant de son type, et ne change jamais de type
DELIMITER //
CREATE TRIGGER trg_post_evenement_type BEFORE INSERT ON post_evenement FOR EACH ROW
BEGIN
    IF (SELECT type_post FROM post WHERE post_id = NEW.post_id) <> 'EVENEMENT' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'post_evenement : ce post n''est pas de type EVENEMENT';
    END IF;
END//
CREATE TRIGGER trg_post_forum_type BEFORE INSERT ON post_forum FOR EACH ROW
BEGIN
    IF (SELECT type_post FROM post WHERE post_id = NEW.post_id) <> 'FORUM' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'post_forum : ce post n''est pas de type FORUM';
    END IF;
END//
CREATE TRIGGER trg_post_type_fige BEFORE UPDATE ON post FOR EACH ROW
BEGIN
    IF NEW.type_post <> OLD.type_post THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'post : le type d''un post ne peut pas changer';
    END IF;
END//
DELIMITER ;

-- ---------------------------------------------------------------------
-- INTERACTIONS
-- ---------------------------------------------------------------------

-- Ordre de la liste d'attente = date_inscription (précision milliseconde)
CREATE TABLE evenement_inscription (
    post_id           INT UNSIGNED NOT NULL,
    user_id           INT UNSIGNED NOT NULL,
    statut            ENUM('INSCRIT','EN_ATTENTE','DESINSCRIT') NOT NULL DEFAULT 'INSCRIT',
    date_inscription  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (post_id, user_id),
    KEY idx_inscr_file (post_id, statut, date_inscription),
    KEY idx_inscr_user (user_id),
    CONSTRAINT fk_inscr_evt  FOREIGN KEY (post_id) REFERENCES post_evenement (post_id) ON DELETE CASCADE,
    CONSTRAINT fk_inscr_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Un like par personne et par post de forum
CREATE TABLE post_like (
    user_id     INT UNSIGNED NOT NULL,
    post_id     INT UNSIGNED NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    KEY idx_like_post (post_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES post_forum (post_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE post_favori (
    user_id  INT UNSIGNED NOT NULL,
    post_id  INT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, post_id),
    KEY idx_pfav_post (post_id),
    CONSTRAINT fk_pfav_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_pfav_post FOREIGN KEY (post_id) REFERENCES post (post_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE commentaire (
    commentaire_id  INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id         INT UNSIGNED NOT NULL,
    post_id         INT UNSIGNED NOT NULL,
    texte           TEXT         NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (commentaire_id),
    KEY idx_com_post (post_id, created_at),
    KEY idx_com_user (user_id),
    CONSTRAINT fk_com_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT,
    CONSTRAINT fk_com_post FOREIGN KEY (post_id) REFERENCES post (post_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- NOTIFICATIONS
-- ---------------------------------------------------------------------

CREATE TABLE notification (
    notif_id    INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    titre       VARCHAR(150)  NOT NULL,
    message     VARCHAR(500)  NOT NULL,
    post_id     INT UNSIGNED  NULL,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notif_id),
    CONSTRAINT fk_notif_post FOREIGN KEY (post_id) REFERENCES post (post_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE liaison_notification (
    notif_id  INT UNSIGNED NOT NULL,
    user_id   INT UNSIGNED NOT NULL,
    is_read   BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (notif_id, user_id),
    KEY idx_liaison_user_read (user_id, is_read),
    CONSTRAINT fk_liaison_notif FOREIGN KEY (notif_id) REFERENCES notification (notif_id) ON DELETE CASCADE,
    CONSTRAINT fk_liaison_user  FOREIGN KEY (user_id)  REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB;
