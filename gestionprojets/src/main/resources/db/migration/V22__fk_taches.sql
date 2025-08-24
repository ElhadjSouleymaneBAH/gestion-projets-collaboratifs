-- Passe 'statut' en ENUM aligné avec l'enum Java + NOT NULL + DEFAULT
ALTER TABLE taches
    MODIFY statut ENUM(
    'BROUILLON',
    'EN_ATTENTE_VALIDATION',
    'TERMINE',
    'ANNULE'
    ) NOT NULL DEFAULT 'BROUILLON';

-- Ajoute les deux clés étrangères indispensables
ALTER TABLE taches
    ADD CONSTRAINT fk_taches_projet
        FOREIGN KEY (id_projet)  REFERENCES projets(id)
            ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT fk_taches_utilisateur
    FOREIGN KEY (id_assigne) REFERENCES utilisateurs(id)
    ON DELETE SET NULL ON UPDATE CASCADE;
