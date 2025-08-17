-- V12__insert_abonnements.sql
-- Insertion des abonnements pour les Chefs de Projet
-- IMPORTANT: Utiliser les noms de colonnes de la table V11

INSERT INTO abonnements (nom, prix, duree, type, statut, date_debut, date_fin, utilisateur_id)
VALUES
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 17),
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 28),
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 41),
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 57),
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 60),
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 78),
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 82),
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 87),
    ('Plan Premium Mensuel', 10.0, 1, 'premium', 'ACTIF', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 92);