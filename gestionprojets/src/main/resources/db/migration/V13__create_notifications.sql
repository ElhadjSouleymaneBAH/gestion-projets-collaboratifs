CREATE TABLE notifications (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               message VARCHAR(255) NOT NULL,
                               date DATETIME NOT NULL,
                               lu BOOLEAN NOT NULL DEFAULT FALSE,
                               id_utilisateur BIGINT NOT NULL,
                               FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;