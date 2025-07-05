CREATE TABLE transactions (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              montant_ht DOUBLE NOT NULL,
                              tva DOUBLE NOT NULL,
                              montant_ttc DOUBLE NOT NULL,
                              montant_abonnement DOUBLE NOT NULL,
                              statut VARCHAR(50) NOT NULL,
                              date_creation DATETIME NOT NULL,
                              id_utilisateur BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;