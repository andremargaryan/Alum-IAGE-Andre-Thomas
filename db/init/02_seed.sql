-- =====================================================================
--  PROJET ALUMNI — Données de test (schéma v6)
--  Mot de passe de tous les comptes : "password" (hash BCrypt, compatible Spring Security)
-- =====================================================================
SET NAMES utf8mb4;   -- les fichiers sont en UTF-8 : sans cette ligne, les accents sont mal lus
USE alumni;

INSERT INTO users (user_id, prenom, nom, date_naissance, email, mot_de_passe, role, statut, ville, deleted_at) VALUES
 (1, 'Super',   'Admin',   NULL,         'admin@alumni.test',   '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN',        'ACTIF',    'Paris',    NULL),
 (2, 'Léa',     'Martin',  '1998-04-12', 'lea@alumni.test',     '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ORGANISATEUR', 'ACTIF',    'Paris',    NULL),
 (3, 'Hugo',    'Dubois',  '2001-09-30', 'hugo@alumni.test',    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ETUDIANT',     'ACTIF',    'Lille',    NULL),
 (4, 'Sarah',   'Nguyen',  '1996-01-05', 'sarah@alumni.test',   '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ETUDIANT',     'ACTIF',    'Lyon',     NULL),
 (5, 'Yanis',   'Bernard', '2000-07-21', 'yanis@alumni.test',   '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ETUDIANT',     'ACTIF',    'Paris',    NULL),
 (6, 'Camille', 'Petit',   '1995-11-02', 'camille@alumni.test', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ETUDIANT',     'ACTIF',    'Bordeaux', NULL),
 (7, 'Inès',    'Roux',    '1999-03-14', 'ines@alumni.test',    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ETUDIANT',     'SUSPENDU', 'Nantes',   NULL),
 -- compte supprimé (suppression logique, données personnelles anonymisées) : son commentaire reste visible
 (8, 'Utilisateur', 'supprimé', NULL,    'supprime-8@alumni.invalid', '!', 'ETUDIANT', 'SUPPRIME', NULL, '2026-10-01 10:00:00');

-- Référentiel : les 21 universités du réseau MIAGE (liste Mon Master 2026)
INSERT INTO etablissement (etablissement_id, nom, ville) VALUES
 (1,  'Université Paris 1 Panthéon-Sorbonne', 'Paris'),
 (2,  'Université Paris Dauphine-PSL',        'Paris'),
 (3,  'Université Paris Cité',                'Paris'),
 (4,  'Université Paris Nanterre',            'Nanterre'),
 (5,  'Université Paris-Saclay',              NULL),
 (6,  'Université de Lille',                  'Lille'),
 (7,  'Université de Picardie Jules Verne',   'Amiens'),
 (8,  'Université de Lorraine',               'Nancy'),
 (9,  'Université de Haute-Alsace',           'Mulhouse'),
 (10, 'Université d''Orléans',                'Orléans'),
 (11, 'Université de Rennes',                 'Rennes'),
 (12, 'Nantes Université',                    'Nantes'),
 (13, 'Université de Bordeaux',               'Bordeaux'),
 (14, 'Université de Toulouse',               'Toulouse'),
 (15, 'Université Toulouse Capitole',         'Toulouse'),
 (16, 'Université Claude Bernard Lyon 1',     'Lyon'),
 (17, 'Université Grenoble Alpes',            'Grenoble'),
 (18, 'Aix-Marseille Université',             NULL),
 (19, 'Université Côte d''Azur',              'Nice'),
 (20, 'Université des Antilles',              NULL),
 (21, 'Université de la Nouvelle-Calédonie',  'Nouméa');

-- Parcours de Paris 1 (source : règlement des études master MIAGE 2025-2029 et fiches formation)
-- Les autres universités : à compléter par l'admin
INSERT INTO parcours (parcours_id, etablissement_id, code, intitule, actif) VALUES
 (1, 1, 'IBI',   'Intelligent Business Informatics',                           TRUE),
 (2, 1, 'SBI',   'Sustainable Business Informatics',                           TRUE),
 (3, 1, 'S2I',   'Systèmes d''Information et Innovation',                      FALSE),
 (4, 1, 'IKSEM', 'Information & Knowledge Systems: Engineering and Management', FALSE);

-- Cursus Master MIAGE (parcours vide = pas encore dans la liste de cette université)
INSERT INTO formation (user_id, etablissement_id, parcours_id, annee_debut, annee_fin, commentaire) VALUES
 (2, 1,  4,    2014, 2016, NULL),
 (3, 1,  1,    2026, 2028, 'En alternance'),
 (4, 1,  3,    2018, 2020, NULL),
 (5, 2,  NULL, 2022, 2024, NULL),
 (6, 6,  NULL, 2017, 2019, NULL),
 (7, 12, NULL, 2020, 2022, NULL);

INSERT INTO experience (user_id, entreprise, poste, annee_debut, annee_fin, commentaire) VALUES
 (3, 'Valiuz',       'Data Analyst junior', 2024, NULL, 'Analyses retail media, KPI campagnes.'),
 (4, 'Capgemini',    'Consultante SI',      2020, NULL, NULL),
 (5, 'Sopra Steria', 'Développeur Java',    2022, NULL, 'Spring Boot, microservices.'),
 (6, 'Decathlon',    'Product Owner',       2019, 2023, NULL),
 (6, 'Doctolib',     'Product Owner',       2023, NULL, NULL);

INSERT INTO favorite_user (user_id, favorite_user_id) VALUES (3, 4), (3, 6), (5, 4);

-- Catégories (provisoires, à valider par l'équipe)
INSERT INTO categorie (categorie_id, libelle, type_post) VALUES
 (1, 'Soirée',      'EVENEMENT'),
 (2, 'Conférence',  'EVENEMENT'),
 (3, 'Sport',       'EVENEMENT'),
 (4, 'Atelier',     'EVENEMENT'),
 (5, 'Emploi',      'FORUM'),
 (6, 'Entraide',    'FORUM'),
 (7, 'Discussion',  'FORUM');

-- Posts : 1 et 2 = événements, 3 et 4 = forum
INSERT INTO post (post_id, user_id, type_post, categorie_id, titre, description, statut) VALUES
 (1, 2, 'EVENEMENT', 1, 'Afterwork alumni de rentrée',               'On se retrouve pour fêter la rentrée !', 'PUBLIE'),
 (2, 2, 'EVENEMENT', 2, 'Conférence : l''IA agentique en entreprise', 'Retours d''expérience de 3 alumnis.',    'PUBLIE'),
 (3, 5, 'FORUM',     5, 'Sopra recrute 2 alternants Java',           'Envoyez-moi vos CV en MP.',              'PUBLIE'),
 (4, 3, 'FORUM',     6, 'Quelqu''un a des ressources sur JMS ?',     'Je galère sur ActiveMQ Artemis…',        'PUBLIE');

-- Événement 1 : capacité 3, complet, 1 personne en attente -> teste la liste d'attente
INSERT INTO post_evenement (post_id, date_evenement, lieu, adresse, capacite_max) VALUES
 (1, '2026-11-20 19:00:00', 'Bar Le Tolbiac',      '12 rue de Tolbiac, 75013 Paris', 3),
 (2, '2026-12-10 18:30:00', 'Centre PMF, amphi N', '90 rue de Tolbiac, 75013 Paris', NULL);

INSERT INTO post_forum (post_id) VALUES (3), (4);

INSERT INTO evenement_inscription (post_id, user_id, statut, date_inscription) VALUES
 (1, 3, 'INSCRIT',    '2026-10-20 10:00:00.000'),
 (1, 4, 'INSCRIT',    '2026-10-20 11:00:00.000'),
 (1, 5, 'INSCRIT',    '2026-10-21 09:00:00.000'),
 (1, 6, 'EN_ATTENTE', '2026-10-22 14:00:00.000'),
 (2, 3, 'INSCRIT',    '2026-10-23 08:00:00.000');

INSERT INTO post_like (user_id, post_id) VALUES (3, 3), (4, 3), (5, 4);

INSERT INTO post_favori (user_id, post_id) VALUES (3, 3), (4, 2);

INSERT INTO commentaire (user_id, post_id, texte) VALUES
 (3, 1, 'Trop hâte !'),
 (5, 4, 'Regarde la doc Spring JMS, section @JmsListener.'),
 (4, 3, 'C''est pour quelle équipe ?'),
 (3, 4, 'Merci, je regarde ça !'),
 (8, 4, 'Commentaire d''un compte supprimé : il reste visible, auteur anonymisé.');

INSERT INTO notification (notif_id, titre, message, post_id) VALUES
 (1, 'Nouvel événement',    'Afterwork alumni de rentrée le 20 novembre', 1),
 (2, 'Nouveau commentaire', 'Yanis a répondu à votre post', 4),
 (3, 'Liste d''attente',    'Vous êtes 1er sur la liste d''attente de l''afterwork', 1),
 (4, 'Bienvenue',           'Bienvenue sur la plateforme Alumni !', NULL);

INSERT INTO liaison_notification (notif_id, user_id, is_read) VALUES
 (1, 3, TRUE), (1, 4, FALSE), (1, 5, FALSE), (1, 6, FALSE),
 (2, 3, FALSE),
 (3, 6, FALSE),
 (4, 3, TRUE);
