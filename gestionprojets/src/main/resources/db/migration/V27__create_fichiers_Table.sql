CREATE TABLE IF NOT EXISTS fichiers (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nom VARCHAR(255) NOT NULL,
    nom_original VARCHAR(255) NOT NULL,
    type_mime VARCHAR(100),
    taille BIGINT NOT NULL,
    chemin VARCHAR(500) NOT NULL,
    id_projet BIGINT NOT NULL,
    id_upload_par BIGINT NOT NULL,
    date_telechargement DATETIME NOT NULL,

    CONSTRAINT fk_fichiers_projet FOREIGN KEY (id_projet)
    REFERENCES projets(id) ON DELETE CASCADE,
    CONSTRAINT fk_fichiers_utilisateur FOREIGN KEY (id_upload_par)
    REFERENCES utilisateurs(id) ON DELETE CASCADE,

    INDEX idx_fichiers_projet (id_projet),
    INDEX idx_fichiers_upload_par (id_upload_par),
    INDEX idx_fichiers_date (date_telechargement)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Commentaires pour documentation
ALTER TABLE fichiers
    COMMENT = 'Table pour stocker les fichiers uploadés dans les projets';