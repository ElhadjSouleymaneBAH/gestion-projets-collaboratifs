INSERT INTO taches (titre, description, id_projet, id_assigne, statut, date_creation) VALUES
-- Tâches pour projet 1 (Application Mobile E-commerce)
('Analyse des besoins fonctionnels', 'Définir les fonctionnalités principales de l''app e-commerce', 1, 2, 'TERMINE', '2024-01-15 10:00:00'),
('Conception interface utilisateur', 'Créer les maquettes et wireframes de l''application', 1, 3, 'TERMINE', '2024-01-16 09:15:00'),
('Développement authentification', 'Implémenter connexion/inscription utilisateurs', 1, 2, 'EN_ATTENTE_VALIDATION', '2024-01-18 14:30:00'),
('Intégration API paiement', 'Connecter Stripe pour paiements sécurisés', 1, 4, 'BROUILLON', '2024-01-20 11:45:00'),
('Tests fonctionnels app', 'Validation de toutes les fonctionnalités', 1, 3, 'BROUILLON', '2024-01-22 16:20:00'),

-- Tâches pour projet 2 (Site Web Portfolio Architecte)
('Benchmark sites concurrents', 'Analyser 10 sites d''architectes reconnus', 2, 1, 'TERMINE', '2024-01-21 08:30:00'),
('Création charte graphique', 'Définir couleurs, typographies et style', 2, 2, 'TERMINE', '2024-01-22 13:15:00'),
('Développement galerie photos', 'Système d''affichage projets avec lightbox', 2, 4, 'EN_ATTENTE_VALIDATION', '2024-01-24 10:45:00'),
('Optimisation SEO', 'Référencement naturel et meta-tags', 2, 1, 'BROUILLON', '2024-01-26 15:30:00'),
('Formation client CMS', 'Apprentissage gestion contenu site', 2, 3, 'BROUILLON', '2024-01-28 09:20:00'),

-- Tâches pour projet 3 (Système de Gestion RH)
('Modélisation base de données', 'Conception tables employés, congés, paie', 3, 2, 'TERMINE', '2024-02-02 09:00:00'),
('Interface gestion employés', 'CRUD employés avec droits d''accès', 3, 4, 'TERMINE', '2024-02-04 14:15:00'),
('Module demande congés', 'Workflow validation congés par manager', 3, 1, 'EN_ATTENTE_VALIDATION', '2024-02-06 11:30:00'),
('Calcul automatique paie', 'Algorithme calcul salaires et charges', 3, 3, 'BROUILLON', '2024-02-08 16:45:00'),
('Génération bulletins PDF', 'Export automatique fiches de paie', 3, 2, 'BROUILLON', '2024-02-10 12:10:00'),

-- Tâches pour projet 4 (Plateforme de Formation en Ligne)
('Architecture technique plateforme', 'Choix stack technique et hébergement', 4, 4, 'TERMINE', '2024-02-11 08:45:00'),
('Système upload vidéos', 'Interface téléchargement cours vidéo', 4, 1, 'TERMINE', '2024-02-13 13:20:00'),
('Player vidéo personnalisé', 'Lecteur avec contrôles avancés', 4, 3, 'EN_ATTENTE_VALIDATION', '2024-02-15 10:35:00'),
('Module quiz interactifs', 'Création et passage de quiz', 4, 2, 'BROUILLON', '2024-02-17 15:50:00'),
('Certificats de réussite', 'Génération certificats automatique', 4, 4, 'BROUILLON', '2024-02-19 09:25:00'),

-- Tâches pour projet 5 (Application de Livraison Restaurant)
('Étude marché livraison', 'Analyse concurrence Uber Eats, Deliveroo', 5, 3, 'TERMINE', '2024-01-06 10:15:00'),
('API géolocalisation', 'Intégration Maps pour suivi livraison', 5, 1, 'TERMINE', '2024-01-08 14:40:00'),
('Interface commande restaurant', 'App pour restaurateurs gestion menu', 5, 2, 'TERMINE', '2024-01-10 11:55:00'),
('Système notification push', 'Alertes statut commande temps réel', 5, 4, 'TERMINE', '2024-01-12 16:10:00'),
('Tests utilisateurs beta', 'Validation UX avec vrais utilisateurs', 5, 3, 'TERMINE', '2024-01-14 12:25:00'),

-- Tâches pour projets 6-10
('Recherche tendances mode', 'Analyse styles et couleurs saison', 6, 2, 'EN_ATTENTE_VALIDATION', '2024-02-16 09:30:00'),
('Photographie produits', 'Shooting professionnel articles mode', 6, 1, 'BROUILLON', '2024-02-18 14:45:00'),
('Système essayage virtuel', 'RA pour essayer vêtements', 6, 4, 'BROUILLON', '2024-02-20 10:20:00'),
('Gestion tailles stocks', 'Inventory management par taille/couleur', 6, 3, 'BROUILLON', '2024-02-22 15:35:00'),
('Programme fidélité client', 'Points de fidélité et récompenses', 6, 2, 'BROUILLON', '2024-02-24 11:50:00'),

('Conformité réglementation', 'Respect normes pharmaceutiques', 7, 4, 'TERMINE', '2024-01-26 08:15:00'),
('Scanner codes-barres', 'Lecture automatique médicaments', 7, 1, 'EN_ATTENTE_VALIDATION', '2024-01-28 13:30:00'),
('Alertes péremption', 'Notification dates expiration', 7, 3, 'BROUILLON', '2024-01-30 10:45:00'),
('Interface commande fournisseurs', 'Gestion approvisionnement automatique', 7, 2, 'BROUILLON', '2024-02-01 16:00:00'),
('Rapports de vente', 'Statistiques ventes par période', 7, 4, 'BROUILLON', '2024-02-03 12:15:00'),

('Définition personas utilisateurs', 'Profils types étudiants cibles', 8, 3, 'TERMINE', '2024-02-06 09:40:00'),
('Architecture sociale', 'Système amis, groupes, discussions', 8, 2, 'EN_ATTENTE_VALIDATION', '2024-02-08 14:55:00'),
('Modération automatique', 'IA détection contenu inapproprié', 8, 1, 'BROUILLON', '2024-02-10 11:10:00'),
('Système événements campus', 'Création et partage événements', 8, 4, 'BROUILLON', '2024-02-12 16:25:00'),
('Application mobile native', 'Version iOS et Android', 8, 3, 'BROUILLON', '2024-02-14 13:40:00'),

('Algorithme matching trajets', 'IA appariement conducteurs/passagers', 9, 2, 'TERMINE', '2024-01-31 10:20:00'),
('Système évaluation utilisateurs', 'Notes et commentaires mutuels', 9, 4, 'EN_ATTENTE_VALIDATION', '2024-02-02 15:35:00'),
('Calcul coûts partage', 'Répartition équitable frais essence', 9, 1, 'BROUILLON', '2024-02-04 12:50:00'),
('Chat intégré trajet', 'Communication conducteur/passagers', 9, 3, 'BROUILLON', '2024-02-06 09:05:00'),
('Assurance covoiturage', 'Partenariat assureur protection', 9, 2, 'BROUILLON', '2024-02-08 14:20:00'),

('Intégration API hôtels', 'Connexion systèmes réservation', 10, 1, 'TERMINE', '2024-01-11 11:30:00'),
('Système comparaison prix', 'Affichage meilleurs tarifs', 10, 3, 'TERMINE', '2024-01-13 16:45:00'),
('Module avis clients', 'Système notation et commentaires', 10, 4, 'TERMINE', '2024-01-15 13:00:00'),
('Recommandations personnalisées', 'IA suggestions basées historique', 10, 2, 'TERMINE', '2024-01-17 10:15:00'),
('Application check-in mobile', 'Enregistrement sans passage réception', 10, 1, 'TERMINE', '2024-01-19 15:30:00'),

-- Tâches techniques générales (projets 11-20)
('Configuration serveur email', 'Paramétrage SMTP pour notifications', 11, 2, 'TERMINE', '2024-02-14 10:20:00'),
('Tests de charge serveur', 'Validation performance 1000 utilisateurs', 11, 4, 'BROUILLON', '2024-02-23 14:30:00'),
('Documentation API REST', 'Guide développeur endpoints', 12, 1, 'TERMINE', '2024-01-19 11:45:00'),
('Sauvegarde automatique données', 'Backup quotidien base données', 12, 2, 'EN_ATTENTE_VALIDATION', '2024-01-21 16:20:00'),
('Interface administration', 'Panel admin gestion utilisateurs', 13, 4, 'BROUILLON', '2024-02-13 12:35:00'),
('Optimisation base de données', 'Index et requêtes pour performances', 14, 3, 'EN_ATTENTE_VALIDATION', '2024-02-15 08:50:00'),
('Système de cache Redis', 'Mise en cache données fréquentes', 15, 1, 'BROUILLON', '2024-02-17 15:10:00'),
('Tests sécurité application', 'Audit vulnérabilités et failles', 16, 2, 'BROUILLON', '2024-02-19 10:25:00'),
('Formation équipe développement', 'Montée en compétences nouvelles technos', 17, 4, 'EN_ATTENTE_VALIDATION', '2024-02-21 13:40:00'),
('Mise en production serveur', 'Déploiement environnement production', 18, 3, 'BROUILLON', '2024-02-23 09:55:00'),

-- Tâches projets 21-30
('Monitoring applicatif', 'Surveillance performances et erreurs', 19, 1, 'BROUILLON', '2024-02-25 14:15:00'),
('Support client niveau 1', 'Helpdesk utilisateurs finaux', 20, 2, 'EN_ATTENTE_VALIDATION', '2024-02-27 11:30:00'),
('Refactoring code legacy', 'Nettoyage et modernisation code', 21, 4, 'BROUILLON', '2024-01-25 16:45:00'),
('Configuration HTTPS SSL', 'Sécurisation connexions chiffrées', 22, 3, 'TERMINE', '2024-01-27 12:20:00'),
('Intégration CI/CD pipeline', 'Automatisation déploiements', 23, 1, 'EN_ATTENTE_VALIDATION', '2024-01-29 08:35:00'),
('Tests unitaires backend', 'Couverture code minimum 80%', 24, 2, 'BROUILLON', '2024-01-31 15:50:00'),
('Responsive design mobile', 'Adaptation écrans smartphones', 25, 4, 'TERMINE', '2024-02-02 11:05:00'),
('Accessibilité WCAG', 'Conformité standards handicap', 26, 3, 'BROUILLON', '2024-02-04 14:20:00'),
('Analyse logs serveur', 'Monitoring erreurs et performances', 27, 1, 'EN_ATTENTE_VALIDATION', '2024-02-06 10:35:00'),
('Migration cloud AWS', 'Transfert infrastructure cloud', 28, 2, 'BROUILLON', '2024-02-08 16:50:00'),

-- Tâches projets 31-40
('Audit code qualité', 'Révision standards développement', 29, 4, 'BROUILLON', '2024-02-10 13:15:00'),
('Documentation utilisateur', 'Manuel utilisation application', 30, 3, 'EN_ATTENTE_VALIDATION', '2024-02-12 09:30:00'),
('Création logo entreprise', 'Design identité visuelle corporate', 31, 3, 'EN_ATTENTE_VALIDATION', '2024-02-16 14:35:00'),
('Optimisation images', 'Compression automatique photos upload', 32, 1, 'BROUILLON', '2024-02-18 09:45:00'),
('Tests compatibilité navigateurs', 'Validation Chrome, Firefox, Safari', 33, 4, 'BROUILLON', '2024-02-20 15:10:00'),
('Formation utilisateurs finaux', 'Sessions apprentissage application', 34, 2, 'EN_ATTENTE_VALIDATION', '2024-02-22 11:25:00'),
('Maintenance préventive système', 'Vérification régulière composants', 35, 3, 'TERMINE', '2024-02-24 16:40:00'),
('Analyse comportement utilisateurs', 'Étude statistiques navigation', 36, 1, 'BROUILLON', '2024-02-26 12:55:00'),
('Développement plugin WordPress', 'Extension CMS pour intégration', 37, 4, 'BROUILLON', '2024-02-28 08:15:00'),
('Tests régression automatisés', 'Suite tests non-régression', 38, 2, 'EN_ATTENTE_VALIDATION', '2024-01-30 13:30:00'),
('Mise à jour dépendances', 'Update librairies et frameworks', 39, 3, 'TERMINE', '2024-02-01 10:45:00'),

-- Tâches projets 41-50
('Configuration CDN', 'Réseau distribution contenu global', 40, 1, 'BROUILLON', '2024-02-03 15:20:00'),
('Système de logs centralisé', 'Aggregation logs toutes applications', 41, 4, 'BROUILLON', '2024-02-05 11:35:00'),
('Tests performance mobile', 'Optimisation vitesse smartphones', 42, 2, 'EN_ATTENTE_VALIDATION', '2024-02-07 16:50:00'),
('Documentation technique API', 'Spécifications détaillées endpoints', 43, 3, 'TERMINE', '2024-02-09 09:25:00'),
('Système notification email', 'Templates personnalisés mailings', 44, 1, 'BROUILLON', '2024-02-11 14:40:00'),
('Analyse concurrentielle approfondie', 'Benchmark 20 solutions marché', 45, 4, 'BROUILLON', '2024-02-13 12:15:00'),
('Configuration pare-feu applicatif', 'WAF protection attaques web', 46, 2, 'EN_ATTENTE_VALIDATION', '2024-02-15 08:30:00'),
('Développement widget embarquable', 'Composant intégrable sites tiers', 47, 3, 'TERMINE', '2024-02-17 13:45:00'),
('Tests charge base données', 'Simulation 10000 requêtes simultanées', 48, 1, 'BROUILLON', '2024-02-19 10:20:00'),
('Système sauvegarde temps réel', 'Backup continu données critiques', 49, 4, 'BROUILLON', '2024-02-21 15:35:00'),
('Interface gestion contenu', 'CMS pour administration contenu', 50, 2, 'EN_ATTENTE_VALIDATION', '2024-02-23 11:50:00'),
-- Tâches supplémentaires pour atteindre 100+
('Création base de connaissances', 'Documentation complète du projet', 31, 3, 'BROUILLON', '2024-02-24 10:15:00'),
('Tests d''intégration continue', 'Pipeline automatisé de tests', 32, 1, 'EN_ATTENTE_VALIDATION', '2024-02-25 15:30:00'),
('Optimisation requêtes SQL', 'Amélioration performances base', 33, 4, 'BROUILLON', '2024-02-26 11:45:00'),
('Configuration monitoring alertes', 'Système d''alerte automatique', 34, 2, 'TERMINE', '2024-02-27 14:20:00'),
('Formation sécurité équipe', 'Sensibilisation bonnes pratiques', 35, 3, 'BROUILLON', '2024-02-28 09:35:00'),
('Audit accessibilité web', 'Vérification normes WCAG', 36, 1, 'EN_ATTENTE_VALIDATION', '2024-01-15 16:50:00'),
('Développement API mobile', 'Endpoints spécifiques applications', 37, 4, 'BROUILLON', '2024-01-16 12:25:00'),
('Tests de montée en charge', 'Simulation 5000 utilisateurs', 38, 2, 'TERMINE', '2024-01-17 08:40:00');
