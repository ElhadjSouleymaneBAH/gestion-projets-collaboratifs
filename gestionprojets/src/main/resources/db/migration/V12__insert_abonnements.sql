INSERT INTO abonnements (nom, prix, duree, type, statut, date_debut, date_fin, utilisateur_id)
SELECT
    'Plan Premium Mensuel',
    10.0,
    1,
    'premium',
    'ACTIF',
    CURDATE(),
    DATE_ADD(CURDATE(), INTERVAL 1 MONTH),
    u.id
FROM utilisateurs u
WHERE u.role = 'CHEF_PROJET';