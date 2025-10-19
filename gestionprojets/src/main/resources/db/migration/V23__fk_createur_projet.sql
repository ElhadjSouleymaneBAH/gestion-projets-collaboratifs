
ALTER TABLE projets
    ADD CONSTRAINT fk_projets_createur
        FOREIGN KEY (id_createur) REFERENCES utilisateurs(id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;
