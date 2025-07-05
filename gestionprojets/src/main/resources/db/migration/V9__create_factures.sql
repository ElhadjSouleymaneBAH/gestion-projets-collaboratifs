CREATE TABLE factures (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          numero_facture VARCHAR(50) UNIQUE NOT NULL,
                          montant_ht DOUBLE NOT NULL,
                          tva DOUBLE NOT NULL,
                          date_emission DATE NOT NULL,
                          date_echeance DATE NOT NULL,
                          statut VARCHAR(20) NOT NULL,
                          id_transaction BIGINT NOT NULL,
                          date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
                          date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (id_transaction) REFERENCES transactions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;