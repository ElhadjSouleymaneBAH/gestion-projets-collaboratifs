UPDATE utilisateurs u
    JOIN (SELECT DISTINCT id_createur FROM projets) p ON p.id_createur = u.id
    SET u.role = 'CHEF_PROJET'
WHERE u.role <> 'ADMINISTRATEUR';
