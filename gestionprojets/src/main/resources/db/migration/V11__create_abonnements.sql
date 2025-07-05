CREATE TABLE abonnements (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             nom VARCHAR(255) NOT NULL,
                             prix DOUBLE NOT NULL,
                             duree INT NOT NULL,
                             id_utilisateur BIGINT NOT NULL,
                             FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;