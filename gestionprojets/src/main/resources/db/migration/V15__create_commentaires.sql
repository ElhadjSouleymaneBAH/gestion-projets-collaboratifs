CREATE TABLE commentaires (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              contenu TEXT NOT NULL,
                              date_creation DATETIME NOT NULL,
                              utilisateur_id BIGINT NOT NULL,
                              tache_id BIGINT NOT NULL,
                              FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id),
                              FOREIGN KEY (tache_id) REFERENCES taches(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;