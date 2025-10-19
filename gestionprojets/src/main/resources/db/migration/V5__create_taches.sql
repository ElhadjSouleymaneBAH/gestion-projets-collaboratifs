CREATE TABLE taches (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        titre VARCHAR(255) NOT NULL,
                        description TEXT,
                        id_projet BIGINT NOT NULL,
                        id_assigne BIGINT,
                        statut VARCHAR(50) NOT NULL DEFAULT 'BROUILLON',
                        date_creation DATETIME NOT NULL,

                        CONSTRAINT fk_taches_projet
                            FOREIGN KEY (id_projet) REFERENCES projets(id) ON DELETE CASCADE,
                        CONSTRAINT fk_taches_assigne
                            FOREIGN KEY (id_assigne) REFERENCES utilisateurs(id) ON DELETE SET NULL,

                        INDEX idx_taches_projet (id_projet),
                        INDEX idx_taches_assigne (id_assigne),
                        INDEX idx_taches_statut (statut),
                        INDEX idx_taches_projet_statut (id_projet, statut),
                        INDEX idx_taches_assigne_statut (id_assigne, statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
