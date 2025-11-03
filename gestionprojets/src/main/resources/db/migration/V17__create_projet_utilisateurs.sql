CREATE TABLE projet_utilisateurs (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     projet_id BIGINT NOT NULL,
                                     utilisateur_id BIGINT NOT NULL,
                                     role VARCHAR(50) DEFAULT 'MEMBRE',
                                     date_ajout DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     actif BOOLEAN DEFAULT TRUE,

                                     CONSTRAINT fk_pu_projet
                                         FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE,
                                     CONSTRAINT fk_pu_utilisateur
                                         FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,

                                     UNIQUE KEY unique_projet_utilisateur (projet_id, utilisateur_id),
                                     INDEX idx_pu_utilisateur (utilisateur_id),
                                     INDEX idx_pu_projet_actif (projet_id, actif),
                                     INDEX idx_pu_utilisateur_actif (utilisateur_id, actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
