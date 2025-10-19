CREATE TABLE commentaires (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              contenu TEXT NOT NULL,
                              date_creation DATETIME NOT NULL,
                              utilisateur_id BIGINT NOT NULL,
                              tache_id BIGINT NOT NULL,

                              CONSTRAINT fk_commentaires_utilisateur
                                  FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
                              CONSTRAINT fk_commentaires_tache
                                  FOREIGN KEY (tache_id) REFERENCES taches(id) ON DELETE CASCADE,

                              INDEX idx_commentaires_tache (tache_id),
                              INDEX idx_commentaires_utilisateur (utilisateur_id),
                              INDEX idx_commentaires_tache_date (tache_id, date_creation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
