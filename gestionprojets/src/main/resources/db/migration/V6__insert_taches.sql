INSERT INTO `taches` (`id`, `titre`, `description`, `id_projet`, `id_assigne`, `statut`, `priorite`, `date_creation`, `date_echeance`) VALUES

-- ============================================================================
-- PROJETS 1-30: Ont des MEMBRES - Tâches assignées aux membres
-- ============================================================================

-- Projet 1: Application Mobile E-commerce (chef=2, membres=6,7,8)
(1, 'Analyse des besoins fonctionnels', 'Définir les fonctionnalités principales de l\'app e-commerce', 1, 6, 'TERMINE', 'NORMALE', '2025-01-15 10:00:00', NULL),
(2, 'Conception interface utilisateur', 'Créer les maquettes et wireframes de l\'application', 1, 7, 'TERMINE', 'NORMALE', '2025-01-16 09:15:00', NULL),
(3, 'Développement authentification', 'Implémenter connexion/inscription utilisateurs', 1, 8, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-01-18 14:30:00', NULL),
(4, 'Intégration API paiement', 'Connecter Stripe pour paiements sécurisés', 1, NULL, 'BROUILLON', 'HAUTE', '2025-01-20 11:45:00', NULL),
(5, 'Tests fonctionnels app', 'Validation de toutes les fonctionnalités', 1, NULL, 'BROUILLON', 'NORMALE', '2025-01-22 16:20:00', NULL),

-- Projet 2: Site Portfolio Architecte (chef=3, membres=9,10)
(6, 'Benchmark sites concurrents', 'Analyser 10 sites d\'architectes reconnus', 2, 9, 'TERMINE', 'NORMALE', '2025-01-21 08:30:00', NULL),
(7, 'Création charte graphique', 'Définir couleurs, typographies et style', 2, 10, 'TERMINE', 'NORMALE', '2025-01-22 13:15:00', NULL),
(8, 'Développement galerie photos', 'Système d\'affichage projets avec lightbox', 2, 9, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-01-24 10:45:00', NULL),
(9, 'Optimisation SEO', 'Référencement naturel et meta-tags', 2, NULL, 'BROUILLON', 'HAUTE', '2025-01-26 15:30:00', NULL),
(10, 'Formation client CMS', 'Apprentissage gestion contenu site', 2, NULL, 'BROUILLON', 'NORMALE', '2025-01-28 09:20:00', NULL),

-- Projet 3: Système de Gestion RH (chef=4, membres=11,12,13)
(11, 'Modélisation base de données', 'Conception tables employés, congés, paie', 3, 11, 'TERMINE', 'NORMALE', '2025-02-02 09:00:00', NULL),
(12, 'Interface gestion employés', 'CRUD employés avec droits d\'accès', 3, 12, 'TERMINE', 'NORMALE', '2025-02-04 14:15:00', NULL),
(13, 'Module demande congés', 'Workflow validation congés par manager', 3, 13, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-06 11:30:00', NULL),
(14, 'Calcul automatique paie', 'Algorithme calcul salaires et charges', 3, NULL, 'BROUILLON', 'HAUTE', '2025-02-08 16:45:00', NULL),
(15, 'Génération bulletins PDF', 'Export automatique fiches de paie', 3, 11, 'BROUILLON', 'NORMALE', '2025-02-10 12:10:00', NULL),

-- Projet 4: Plateforme E-learning (chef=5, membres=14,15,16)
(16, 'Architecture technique plateforme', 'Choix stack technique et hébergement', 4, 14, 'TERMINE', 'NORMALE', '2025-02-11 08:45:00', NULL),
(17, 'Système upload vidéos', 'Interface téléchargement cours vidéo', 4, 15, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-13 13:20:00', NULL),
(18, 'Player vidéo personnalisé', 'Lecteur avec contrôles avancés', 4, 16, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-15 10:35:00', NULL),
(19, 'Module quiz interactifs', 'Création et passage de quiz', 4, NULL, 'BROUILLON', 'HAUTE', '2025-02-17 15:50:00', NULL),
(20, 'Certificats de réussite', 'Génération certificats automatique', 4, NULL, 'BROUILLON', 'NORMALE', '2025-02-19 09:25:00', NULL),

-- Projet 5: App Livraison Repas (chef=17, membres=18,19,20)
(21, 'Étude marché livraison', 'Analyse concurrence Uber Eats, Deliveroo', 5, 18, 'TERMINE', 'NORMALE', '2025-01-06 10:15:00', NULL),
(22, 'API géolocalisation', 'Intégration Maps pour suivi livraison', 5, 19, 'TERMINE', 'NORMALE', '2025-01-08 14:40:00', NULL),
(23, 'Interface commande restaurant', 'App pour restaurateurs gestion menu', 5, 20, 'TERMINE', 'NORMALE', '2025-01-10 11:55:00', NULL),
(24, 'Système notification push', 'Alertes statut commande temps réel', 5, 18, 'TERMINE', 'NORMALE', '2025-01-12 16:10:00', NULL),
(25, 'Tests utilisateurs beta', 'Validation UX avec vrais utilisateurs', 5, 19, 'TERMINE', 'NORMALE', '2025-01-14 12:25:00', NULL),

-- Projet 6: Boutique en Ligne Vêtements (chef=28, membres=21,22,23)
(26, 'Recherche tendances mode', 'Analyse styles et couleurs saison', 6, 21, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-16 09:30:00', NULL),
(27, 'Photographie produits', 'Shooting professionnel articles mode', 6, NULL, 'BROUILLON', 'HAUTE', '2025-02-18 14:45:00', NULL),
(28, 'Système essayage virtuel', 'RA pour essayer vêtements', 6, NULL, 'BROUILLON', 'HAUTE', '2025-02-20 10:20:00', NULL),
(29, 'Gestion tailles stocks', 'Inventory management par taille/couleur', 6, NULL, 'BROUILLON', 'NORMALE', '2025-02-22 15:35:00', NULL),
(30, 'Programme fidélité client', 'Points de fidélité et récompenses', 6, 22, 'BROUILLON', 'NORMALE', '2025-02-24 11:50:00', NULL),

-- Projet 7: Logiciel Gestion Pharmacie (chef=41, membres=25,27)
(31, 'Conformité réglementation', 'Respect normes pharmaceutiques', 7, 25, 'TERMINE', 'NORMALE', '2025-01-26 08:15:00', NULL),
(32, 'Scanner codes-barres', 'Lecture automatique médicaments', 7, 27, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-01-28 13:30:00', NULL),
(33, 'Alertes péremption', 'Notification dates expiration', 7, NULL, 'BROUILLON', 'HAUTE', '2025-01-30 10:45:00', NULL),
(34, 'Interface commande fournisseurs', 'Gestion approvisionnement automatique', 7, NULL, 'BROUILLON', 'NORMALE', '2025-02-01 16:00:00', NULL),
(35, 'Rapports de vente', 'Statistiques ventes par période', 7, 25, 'BROUILLON', 'NORMALE', '2025-02-03 12:15:00', NULL),

-- Projet 8: Réseau Social Étudiants (chef=57, membres=29,30,31)
(36, 'Définition personas utilisateurs', 'Profils types étudiants cibles', 8, 29, 'TERMINE', 'NORMALE', '2025-02-06 09:40:00', NULL),
(37, 'Architecture sociale', 'Système amis, groupes, discussions', 8, 30, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-08 14:55:00', NULL),
(38, 'Modération automatique', 'IA détection contenu inapproprié', 8, 30, 'BROUILLON', 'HAUTE', '2025-02-10 11:10:00', NULL),
(39, 'Système événements campus', 'Création et partage événements', 8, NULL, 'BROUILLON', 'NORMALE', '2025-02-12 16:25:00', NULL),
(40, 'Application mobile native', 'Version iOS et Android', 8, 31, 'BROUILLON', 'NORMALE', '2025-02-14 13:40:00', NULL),

-- Projet 9: Plateforme Covoiturage (chef=60, membres=33,34)
(41, 'Algorithme matching trajets', 'IA appariement conducteurs/passagers', 9, 33, 'TERMINE', 'NORMALE', '2025-01-31 10:20:00', NULL),
(42, 'Système évaluation utilisateurs', 'Notes et commentaires mutuels', 9, 34, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-02 15:35:00', NULL),
(43, 'Calcul coûts partage', 'Répartition équitable frais essence', 9, NULL, 'BROUILLON', 'HAUTE', '2025-02-04 12:50:00', NULL),
(44, 'Chat intégré trajet', 'Communication conducteur/passagers', 9, 33, 'BROUILLON', 'NORMALE', '2025-02-06 09:05:00', NULL),
(45, 'Assurance covoiturage', 'Partenariat assureur protection', 9, 34, 'BROUILLON', 'NORMALE', '2025-02-08 14:20:00', NULL),

-- Projet 10: Site Comparateur Hôtels (chef=78, membres=35,36,37)
(46, 'Intégration API hôtels', 'Connexion systèmes réservation', 10, 35, 'TERMINE', 'NORMALE', '2025-01-11 11:30:00', NULL),
(47, 'Système comparaison prix', 'Affichage meilleurs tarifs', 10, 36, 'TERMINE', 'NORMALE', '2025-01-13 16:45:00', NULL),
(48, 'Module avis clients', 'Système notation et commentaires', 10, 37, 'TERMINE', 'NORMALE', '2025-01-15 13:00:00', NULL),
(49, 'Recommandations personnalisées', 'IA suggestions basées historique', 10, 35, 'TERMINE', 'NORMALE', '2025-01-17 10:15:00', NULL),
(50, 'Tests utilisateurs finaux', 'Validation expérience utilisateur complète', 10, 36, 'TERMINE', 'NORMALE', '2025-01-19 15:30:00', NULL),

-- Projet 11: CRM Immobilier (chef=82, membres=38,39)
(51, 'Configuration serveur email', 'Paramétrage SMTP pour notifications', 11, 38, 'TERMINE', 'NORMALE', '2025-02-14 10:20:00', NULL),
(52, 'Tests de charge serveur', 'Validation performance 1000 utilisateurs', 11, NULL, 'BROUILLON', 'HAUTE', '2025-02-23 14:30:00', NULL),

-- Projet 12: Application Fitness Tracker (chef=87, membres=42,43)
(53, 'Documentation API REST', 'Guide développeur endpoints', 12, 42, 'TERMINE', 'NORMALE', '2025-01-19 11:45:00', NULL),
(54, 'Sauvegarde automatique données', 'Backup quotidien base données', 12, NULL, 'BROUILLON', 'HAUTE', '2025-01-21 16:20:00', NULL),

-- Projet 13: Plateforme de Freelancing (chef=92, membres=44,45)
(55, 'Interface administration', 'Panel admin gestion utilisateurs', 13, 44, 'BROUILLON', 'NORMALE', '2025-02-13 12:35:00', NULL),

-- Projet 14: Système de Caisse Restaurant (chef=97, membres=46,47)
(56, 'Optimisation base de données', 'Index et requêtes pour performances', 14, 46, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-15 08:50:00', NULL),

-- Projet 15: Application de Méditation (chef=2, membres=48,49)
(57, 'Système de cache Redis', 'Mise en cache données fréquentes', 15, 48, 'BROUILLON', 'HAUTE', '2025-02-17 15:10:00', NULL),

-- Projet 16: Gestionnaire de Tâches Équipe (chef=2, membres=50,52)
(58, 'Tests sécurité application', 'Audit vulnérabilités et failles', 16, 50, 'BROUILLON', 'HAUTE', '2025-02-19 10:25:00', NULL),

-- Projet 17: Site de Petites Annonces (chef=3, membres=53,54)
(59, 'Formation équipe développement', 'Montée en compétences nouvelles technos', 17, 53, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-21 13:40:00', NULL),

-- Projet 18: Application de Quiz Éducatif (chef=4, membres=55,56)
(60, 'Mise en production serveur', 'Déploiement environnement production', 18, 55, 'BROUILLON', 'HAUTE', '2025-02-23 09:55:00', NULL),

-- Projet 19: Plateforme de Streaming Musique (chef=5, membres=58,59)
(61, 'Monitoring applicatif', 'Surveillance performances et erreurs', 19, 58, 'BROUILLON', 'NORMALE', '2025-02-25 14:15:00', NULL),

-- Projet 20: Gestion de Cabinet Médical (chef=17, membres=61,62)
(62, 'Support client niveau 1', 'Helpdesk utilisateurs finaux', 20, 61, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-27 11:30:00', NULL),

-- Projet 21: Application de Météo Locale (chef=28, membres=63,64)
(63, 'Refactoring code legacy', 'Nettoyage et modernisation code', 21, 63, 'BROUILLON', 'NORMALE', '2025-01-25 16:45:00', NULL),

-- Projet 22: E-commerce Produits Bio (chef=41, membres=65,66)
(64, 'Configuration HTTPS SSL', 'Sécurisation connexions chiffrées', 22, 65, 'TERMINE', 'NORMALE', '2025-01-27 12:20:00', NULL),

-- Projet 23: Système de Vote Électronique (chef=57, membres=67,68)
(65, 'Intégration CI/CD pipeline', 'Automatisation déploiements', 23, 67, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-01-29 08:35:00', NULL),

-- Projet 24: Application de Reconnaissance Vocale (chef=60, membres=69,70)
(66, 'Tests unitaires backend', 'Couverture code minimum 80%', 24, 69, 'BROUILLON', 'HAUTE', '2025-01-31 15:50:00', NULL),

-- Projet 25: Plateforme de Crowdfunding (chef=78, membres=71,72)
(67, 'Responsive design mobile', 'Adaptation écrans smartphones', 25, 71, 'TERMINE', 'NORMALE', '2025-02-02 11:05:00', NULL),

-- Projet 26: Gestion de Flotte Véhicules (chef=82, membres=73,74)
(68, 'Accessibilité WCAG', 'Conformité standards handicap', 26, 73, 'BROUILLON', 'NORMALE', '2025-02-04 14:20:00', NULL),

-- Projet 27: Application de Lecture QR Code (chef=87, membres=75,76)
(69, 'Analyse logs serveur', 'Monitoring erreurs et performances', 27, 75, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-06 10:35:00', NULL),

-- Projet 28: Site de Comparaison Prix (chef=92, membres=77,79)
(70, 'Migration cloud AWS', 'Transfert infrastructure cloud', 28, 77, 'BROUILLON', 'HAUTE', '2025-02-08 16:50:00', NULL),

-- Projet 29: Application de Chat Vidéo (chef=97, membres=80,81)
(71, 'Audit code qualité', 'Révision standards développement', 29, 80, 'BROUILLON', 'NORMALE', '2025-02-10 13:15:00', NULL),

-- Projet 30: Plateforme de Microblogging (chef=3, membres=83,84)
(72, 'Documentation utilisateur', 'Manuel utilisation application', 30, 83, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-12 09:30:00', NULL),

-- ============================================================================
-- PROJETS 31-100: PAS de MEMBRES
-- Tâches TERMINÉES/EN_ATTENTE → assignées au CHEF DE PROJET
-- Tâches BROUILLON → NULL (pas encore assignées)
-- ============================================================================

-- Projet 31: Système de Backup Cloud (chef=2)
(73, 'Création logo entreprise', 'Design identité visuelle corporate', 31, 2, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-16 14:35:00', NULL),

-- Projet 32: Application de Géolocalisation (chef=3)
(74, 'Optimisation images', 'Compression automatique photos upload', 32, NULL, 'BROUILLON', 'NORMALE', '2025-02-18 09:45:00', NULL),

-- Projet 33: Plateforme de Vente aux Enchères (chef=4)
(75, 'Tests compatibilité navigateurs', 'Validation Chrome, Firefox, Safari', 33, NULL, 'BROUILLON', 'NORMALE', '2025-02-20 15:10:00', NULL),

-- Projet 34: Gestion de Bibliothèque (chef=5)
(76, 'Formation utilisateurs finaux', 'Sessions apprentissage application', 34, 5, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-22 11:25:00', NULL),

-- Projet 35: Application de Calcul Scientifique (chef=17)
(77, 'Maintenance préventive système', 'Vérification régulière composants', 35, 17, 'TERMINE', 'NORMALE', '2025-02-24 16:40:00', NULL),

-- Projet 36: Site de Réservation Spectacles (chef=28)
(78, 'Analyse comportement utilisateurs', 'Étude statistiques navigation', 36, NULL, 'BROUILLON', 'NORMALE', '2025-02-26 12:55:00', NULL),

-- Projet 37: Application de Traduction (chef=41)
(79, 'Développement plugin WordPress', 'Extension CMS pour intégration', 37, NULL, 'BROUILLON', 'NORMALE', '2025-02-28 08:15:00', NULL),

-- Projet 38: Plateforme de Gestion Énergétique (chef=57)
(80, 'Tests régression automatisés', 'Suite tests non-régression', 38, 57, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-01-30 13:30:00', NULL),

-- Projet 39: Système de Gestion Scolaire (chef=60)
(81, 'Mise à jour dépendances', 'Update librairies et frameworks', 39, 60, 'TERMINE', 'NORMALE', '2025-02-01 10:45:00', NULL),

-- Projet 40: Application de Reconnaissance Faciale (chef=78)
(82, 'Configuration CDN', 'Réseau distribution contenu global', 40, NULL, 'BROUILLON', 'HAUTE', '2025-02-03 15:20:00', NULL),

-- Projet 41: Site de Location Véhicules (chef=82)
(83, 'Système de logs centralisé', 'Aggregation logs toutes applications', 41, NULL, 'BROUILLON', 'NORMALE', '2025-02-05 11:35:00', NULL),

-- Projet 42: Application de Réalité Augmentée (chef=87)
(84, 'Tests performance mobile', 'Optimisation vitesse smartphones', 42, 87, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-07 16:50:00', NULL),

-- Projet 43: Plateforme de Gaming Social (chef=92)
(85, 'Documentation technique API', 'Spécifications détaillées endpoints', 43, 92, 'TERMINE', 'NORMALE', '2025-02-09 09:25:00', NULL),

-- Projet 44: Système de Sécurité Domotique (chef=97)
(86, 'Système notification email', 'Templates personnalisés mailings', 44, NULL, 'BROUILLON', 'NORMALE', '2025-02-11 14:40:00', NULL),

-- Projet 45: Application de Gestion Budget (chef=4)
(87, 'Analyse concurrentielle approfondie', 'Benchmark 20 solutions marché', 45, NULL, 'BROUILLON', 'NORMALE', '2025-02-13 12:15:00', NULL),

-- Projet 46: Site de Journalisme Citoyen (chef=2)
(88, 'Configuration pare-feu applicatif', 'WAF protection attaques web', 46, 2, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-15 08:30:00', NULL),

-- Projet 47: Application de Scan Document (chef=3)
(89, 'Développement widget embarquable', 'Composant intégrable sites tiers', 47, 3, 'TERMINE', 'NORMALE', '2025-02-17 13:45:00', NULL),

-- Projet 48: Plateforme de Télémédecine (chef=4)
(90, 'Tests charge base données', 'Simulation 10000 requêtes simultanées', 48, NULL, 'BROUILLON', 'NORMALE', '2025-02-19 10:20:00', NULL),

-- Projet 49: Système de Gestion Événements (chef=5)
(91, 'Système sauvegarde temps réel', 'Backup continu données critiques', 49, NULL, 'BROUILLON', 'NORMALE', '2025-02-21 15:35:00', NULL),

-- Projet 50: Application de Mindfulness (chef=17)
(92, 'Interface gestion contenu', 'CMS pour administration contenu', 50, 17, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-23 11:50:00', NULL),

-- ============================================================================
-- TÂCHES SUPPLÉMENTAIRES (93-100) pour atteindre 100 tâches
-- ============================================================================

-- Projet 31: (chef=2)
(93, 'Création base de connaissances', 'Documentation complète du projet', 31, NULL, 'BROUILLON', 'NORMALE', '2025-02-24 10:15:00', NULL),

-- Projet 32: (chef=3)
(94, 'Tests d\'intégration continue', 'Pipeline automatisé de tests', 32, 3, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-02-25 15:30:00', NULL),

-- Projet 33: (chef=4)
(95, 'Optimisation requêtes SQL', 'Amélioration performances base', 33, NULL, 'BROUILLON', 'NORMALE', '2025-02-26 11:45:00', NULL),

-- Projet 34: (chef=5)
(96, 'Configuration monitoring alertes', 'Système d\'alerte automatique', 34, 5, 'TERMINE', 'NORMALE', '2025-02-27 14:20:00', NULL),

-- Projet 35: (chef=17)
(97, 'Formation sécurité équipe', 'Sensibilisation bonnes pratiques', 35, NULL, 'BROUILLON', 'NORMALE', '2025-02-28 09:35:00', NULL),

-- Projet 36: (chef=28)
(98, 'Audit accessibilité web', 'Vérification normes WCAG', 36, 28, 'EN_ATTENTE_VALIDATION', 'NORMALE', '2025-01-15 16:50:00', NULL),

-- Projet 37: (chef=41)
(99, 'Développement API mobile', 'Endpoints spécifiques applications', 37, NULL, 'BROUILLON', 'NORMALE', '2025-01-16 12:25:00', NULL),

-- Projet 38: (chef=57)
(100, 'Tests de montée en charge', 'Simulation 5000 utilisateurs', 38, 57, 'TERMINE', 'NORMALE', '2025-01-17 08:40:00', NULL);