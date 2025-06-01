CREATE TABLE utilisateurs (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              email VARCHAR(191) NOT NULL UNIQUE,
                              mot_de_passe VARCHAR(255) NOT NULL,
                              role VARCHAR(50) NOT NULL,
                              langue VARCHAR(5) NOT NULL DEFAULT 'fr',
                              cgu_accepte BOOLEAN NOT NULL
);
