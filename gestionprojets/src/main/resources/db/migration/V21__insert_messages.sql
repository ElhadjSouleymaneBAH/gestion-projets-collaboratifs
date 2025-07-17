-- Insertion de messages de test conformes aux VRAIS membres des projets
INSERT INTO messages (contenu, utilisateur_id, projet_id, type, statut, date_envoi) VALUES

-- Projet 1 : Application Mobile E-commerce

('Bienvenue dans le chat du projet Application Mobile E-commerce !', 1, 1, 'SYSTEM', 'ENVOYE', '2024-01-15 09:30:00'),
('Excellente analyse des besoins pour l e-commerce mobile !', 1, 1, 'TEXT', 'ENVOYE', '2024-01-15 11:30:00'),
('Il faut ajouter la gestion des avis clients', 2, 1, 'TEXT', 'ENVOYE', '2024-01-15 14:20:00'),
('D accord, essentiel pour la confiance', 3, 1, 'TEXT', 'ENVOYE', '2024-01-15 16:45:00'),
('J ai ajouté des suggestions d amélioration', 4, 1, 'TEXT', 'ENVOYE', '2024-01-16 15:30:00'),

-- Projet 2 : Site Web Portfolio Architecte

('Discussion Portfolio Architecte ouverte', 2, 2, 'SYSTEM', 'ENVOYE', '2024-01-20 14:30:00'),
('Les maquettes sont vraiment réussies !', 5, 2, 'TEXT', 'ENVOYE', '2024-01-16 10:15:00'),
('J ai ajouté des suggestions d amélioration', 6, 2, 'TEXT', 'ENVOYE', '2024-01-16 15:30:00'),
('La charte graphique respecte l identité', 2, 2, 'TEXT', 'ENVOYE', '2024-01-22 15:45:00'),

-- Projet 3 : Système de Gestion RH

('Équipe Système de Gestion RH au complet !', 1, 3, 'SYSTEM', 'ENVOYE', '2024-02-01 08:30:00'),
('Base de données RH bien structurée', 7, 3, 'TEXT', 'ENVOYE', '2024-02-02 11:15:00'),
('Interface très intuitive pour les RH', 8, 3, 'TEXT', 'ENVOYE', '2024-02-04 16:30:00'),
('Le workflow de validation est parfait', 9, 3, 'TEXT', 'ENVOYE', '2024-02-06 14:45:00'),
('Calcul automatique paie fonctionne bien', 10, 3, 'TEXT', 'ENVOYE', '2024-02-08 16:45:00'),

-- Projet 4 : Plateforme de Formation en Ligne

('Chat projet Plateforme de Formation en Ligne actif', 3, 4, 'SYSTEM', 'ENVOYE', '2024-02-10 11:00:00'),
('Architecture technique plateforme excellente', 11, 4, 'TEXT', 'ENVOYE', '2024-02-11 08:45:00'),
('Upload vidéo très rapide !', 12, 4, 'TEXT', 'ENVOYE', '2024-02-13 15:35:00'),
('Player vidéo personnalisé vraiment professionnel', 13, 4, 'TEXT', 'ENVOYE', '2024-02-15 12:50:00'),

-- Projet 5 : Application de Livraison Restaurant

('Application de Livraison Restaurant - Let s go !', 2, 5, 'SYSTEM', 'ENVOYE', '2024-01-05 16:35:00'),
('Étude de marché très approfondie', 14, 5, 'TEXT', 'ENVOYE', '2024-01-06 12:30:00'),
('Géolocalisation précise au mètre près !', 15, 5, 'TEXT', 'ENVOYE', '2024-01-08 16:45:00'),
('Interface restaurant très ergonomique', 16, 5, 'TEXT', 'ENVOYE', '2024-01-10 14:20:00'),
('Tests utilisateurs très concluants !', 2, 5, 'TEXT', 'ENVOYE', '2024-01-14 13:25:00'),

-- Projet 6 : Boutique en Ligne Vêtements

('Boutique en Ligne Vêtements - Team chat', 1, 6, 'SYSTEM', 'ENVOYE', '2024-02-15 11:45:00'),
('Recherche tendances mode en cours', 17, 6, 'TEXT', 'ENVOYE', '2024-02-16 09:30:00'),
('Photographie produits terminée', 18, 6, 'TEXT', 'ENVOYE', '2024-02-18 14:45:00'),
('Système essayage virtuel impressionnant', 19, 6, 'TEXT', 'ENVOYE', '2024-02-20 10:20:00'),

-- Projet 8 : Réseau Social Étudiants

('Réseau Social Étudiants - Collaboration active', 3, 8, 'SYSTEM', 'ENVOYE', '2024-02-05 09:35:00'),
('Définition personas utilisateurs terminée', 22, 8, 'TEXT', 'ENVOYE', '2024-02-06 10:00:00'),
('Architecture sociale bien pensée', 23, 8, 'TEXT', 'ENVOYE', '2024-02-08 15:15:00'),
('Modération automatique IA fonctionnelle', 24, 8, 'TEXT', 'ENVOYE', '2024-02-10 11:10:00'),
('Application mobile native en cours', 25, 8, 'TEXT', 'ENVOYE', '2024-02-14 13:40:00'),

-- Projet 9 : Application de Covoiturage

('Application de Covoiturage - Chat en direct', 2, 9, 'SYSTEM', 'ENVOYE', '2024-01-30 15:25:00'),
('Algorithme matching trajets au top !', 26, 9, 'TEXT', 'ENVOYE', '2024-01-31 10:20:00'),
('Système évaluation utilisateurs impeccable', 27, 9, 'TEXT', 'ENVOYE', '2024-02-02 15:35:00'),

-- Projet 10 : Site de Réservation Hôtel

('Site de Réservation Hôtel - Team discussion', 1, 10, 'SYSTEM', 'ENVOYE', '2024-01-10 12:15:00'),
('Intégration API hôtels réussie', 28, 10, 'TEXT', 'ENVOYE', '2024-01-11 11:30:00'),
('Système comparaison prix excellent', 29, 10, 'TEXT', 'ENVOYE', '2024-01-13 16:00:00'),
('Tests utilisateurs très concluants !', 30, 10, 'TEXT', 'ENVOYE', '2024-01-17 10:15:00'),

-- Messages récents pour démonstration F9 (collaboration temps réel)
('Excellente collaboration cette semaine !', 1, 1, 'TEXT', 'ENVOYE', '2024-12-14 17:00:00'),
('L équipe est vraiment soudée ', 2, 1, 'TEXT', 'ENVOYE', '2024-12-14 17:05:00'),
('Nouvelle tâche Backend API créée', 3, 4, 'NOTIFICATION', 'ENVOYE', '2024-12-12 17:45:00'),
('Commentaire résolu sur Database Design', 7, 3, 'TEXT', 'ENVOYE', '2024-12-12 16:20:00'),
('Tests automatisés OK sur tous navigateurs', 12, 4, 'TEXT', 'ENVOYE', '2024-12-11 16:35:00');

