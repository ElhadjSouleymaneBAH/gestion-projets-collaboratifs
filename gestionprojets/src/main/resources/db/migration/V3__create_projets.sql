CREATE TABLE projets (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         titre VARCHAR(255) NOT NULL,
                         description TEXT,
                         id_createur BIGINT NOT NULL,
                         statut VARCHAR(50) NOT NULL DEFAULT 'ACTIF',
                         date_creation DATETIME NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;