CREATE TABLE projet_membres (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                projet_id BIGINT NOT NULL,
                                utilisateur_id BIGINT NOT NULL,
                                role VARCHAR(50) DEFAULT 'MEMBRE',
                                date_ajout DATETIME DEFAULT CURRENT_TIMESTAMP,
                                actif BOOLEAN DEFAULT TRUE,
                                FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE,
                                FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
                                UNIQUE KEY unique_projet_utilisateur (projet_id, utilisateur_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;