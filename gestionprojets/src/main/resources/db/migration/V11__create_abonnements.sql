-- Migration V11 : Création de la table abonnements

CREATE TABLE abonnements (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL,
                             prix DOUBLE NOT NULL,
                             duree INT NOT NULL,
                             type VARCHAR(50) NOT NULL DEFAULT 'premium'
                                 CHECK (type IN ('gratuit', 'premium', 'entreprise')),
                             statut VARCHAR(50) NOT NULL DEFAULT 'ACTIF'
                                 CHECK (statut IN ('ACTIF', 'RESILIE', 'EXPIRE', 'EN_PAUSE')),
                             date_debut DATE NOT NULL,
                             date_fin DATE NULL,
                             utilisateur_id BIGINT NOT NULL,

    -- Contraintes
                             CONSTRAINT FK_abonnements_utilisateur
                                 FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
                                     ON DELETE CASCADE ON UPDATE CASCADE,

    -- Un seul abonnement par utilisateur
                             CONSTRAINT UK_abonnements_utilisateur UNIQUE (utilisateur_id),

    -- Index pour performance
                             INDEX idx_abonnements_utilisateur (utilisateur_id),
                             INDEX idx_abonnements_statut (statut),
                             INDEX idx_abonnements_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;