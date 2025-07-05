CREATE TABLE projet_membres (
                                projet_id BIGINT NOT NULL,
                                utilisateur_id BIGINT NOT NULL,
                                date_ajout DATETIME NOT NULL,
                                PRIMARY KEY (projet_id, utilisateur_id),
                                FOREIGN KEY (projet_id) REFERENCES projets(id),
                                FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;