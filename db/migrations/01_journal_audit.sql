-- =====================================================================
--  Migration : ajout de la table journal_audit à une base déjà créée.
--  A exécuter UNE fois dans phpMyAdmin (onglet SQL sur la base alumni).
--  Inutile après un "docker compose down -v" : 01_schema.sql la crée déjà.
-- =====================================================================
SET NAMES utf8mb4;
USE alumni;

-- Journal d'audit : une ligne par requête sur l'API (écrite par AuditInterceptor)
-- et une par compte créé (écrite par le consumer JMS EventConsumer).
-- Pas de clé étrangère vers users : le journal doit garder ses lignes même si le compte disparaît.
CREATE TABLE IF NOT EXISTS journal_audit (
    audit_id     INT UNSIGNED      NOT NULL AUTO_INCREMENT,
    date_action  DATETIME(3)       NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    user_id      INT UNSIGNED      NULL,                -- null = personne de connecté
    action       VARCHAR(30)       NOT NULL,            -- REQUETE_API, USER_CREATED
    methode      VARCHAR(10)       NULL,                -- GET, POST...
    route        VARCHAR(255)      NULL,                -- /api/events
    statut_http  SMALLINT UNSIGNED NULL,                -- 200, 401...
    details      VARCHAR(255)      NULL,
    PRIMARY KEY (audit_id),
    KEY idx_audit_date (date_action),
    KEY idx_audit_user (user_id)
) ENGINE=InnoDB;
