CREATE TABLE transactions (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              montant_ht DOUBLE NOT NULL,
                              tva DOUBLE NOT NULL,
                              montant_ttc DOUBLE NOT NULL,
                              montant_abonnement DOUBLE NOT NULL,
                              statut VARCHAR(50) NOT NULL,
                              date_creation DATETIME NOT NULL,
                              id_utilisateur BIGINT NOT NULL,

    -- Contraintes de validation
                              CONSTRAINT chk_montant_positif CHECK (montant_ht > 0),
                              CONSTRAINT chk_tva_positive CHECK (tva >= 0),
                              CONSTRAINT chk_montant_ttc_coherent CHECK (montant_ttc = montant_ht + tva),

    -- Index pour performances
                              INDEX idx_transactions_utilisateur (id_utilisateur),
                              INDEX idx_transactions_statut (statut),
                              INDEX idx_transactions_date (date_creation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;