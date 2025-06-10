CREATE TABLE utilisateurs (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              nom VARCHAR(100) NOT NULL,
                              prenom VARCHAR(100) NOT NULL,
                              email VARCHAR(191) NOT NULL UNIQUE,
                              mot_de_passe VARCHAR(255) NOT NULL,
                              role VARCHAR(50) NOT NULL,
                              langue VARCHAR(10) NOT NULL DEFAULT 'fr',
                              cgu_accepte BOOLEAN NOT NULL,
                              date_inscription DATETIME NOT NULL
);
