CREATE TABLE projets (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         titre VARCHAR(255) NOT NULL,
                         description TEXT,
                         id_createur BIGINT NOT NULL,
                         statut VARCHAR(50) NOT NULL DEFAULT 'ACTIF',
                         date_creation DATETIME NOT NULL,
                         publique BOOLEAN DEFAULT FALSE          -- ← NOUVEAU CHAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index pour F3: Consultation projets publics
CREATE INDEX idx_projets_publiques ON projets(publique);

-- Index pour F6: Gérer ses projets par créateur
CREATE INDEX idx_projets_createur ON projets(id_createur);

-- Index pour filtrage par statut
CREATE INDEX idx_projets_statut ON projets(statut);

-- Index composé pour requêtes mixtes
CREATE INDEX idx_projets_publiques_actifs ON projets(publique, statut);

-- Index pour tri par date (projets récents)
CREATE INDEX idx_projets_date_creation ON projets(date_creation);