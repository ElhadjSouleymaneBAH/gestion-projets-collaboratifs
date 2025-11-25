CREATE TABLE listes_colonnes (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 nom VARCHAR(100) NOT NULL,                    -- "À faire", "En cours", "Terminé"
                                 position INT NOT NULL DEFAULT 0,              -- Ordre d'affichage des colonnes
                                 id_projet BIGINT NOT NULL,                    -- Lien vers le projet
                                 date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_listes_colonnes_projet
                                     FOREIGN KEY (id_projet) REFERENCES projets(id) ON DELETE CASCADE,

    -- Index pour performance
                                 INDEX idx_listes_colonnes_projet (id_projet),
                                 INDEX idx_listes_colonnes_projet_position (id_projet, position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ========================================
-- 2. MODIFIER LA TABLE taches (Ajouter colonnes Kanban)
-- ========================================

-- Ajouter la colonne id_liste_colonne (lien vers la colonne Kanban)
ALTER TABLE taches
    ADD COLUMN id_liste_colonne BIGINT NULL AFTER id_projet,
ADD COLUMN position INT NOT NULL DEFAULT 0 AFTER id_liste_colonne;

-- Ajouter la contrainte de clé étrangère
ALTER TABLE taches
    ADD CONSTRAINT fk_taches_liste_colonne
        FOREIGN KEY (id_liste_colonne) REFERENCES listes_colonnes(id) ON DELETE SET NULL;

-- Ajouter les index pour performance
CREATE INDEX idx_taches_liste_colonne ON taches(id_liste_colonne);
CREATE INDEX idx_taches_liste_colonne_position ON taches(id_liste_colonne, position);


-- ========================================
-- 3. CRÉER LES 3 COLONNES PAR DÉFAUT POUR CHAQUE PROJET EXISTANT
-- ========================================

-- Insérer les 3 colonnes Kanban standard pour tous les projets existants
INSERT INTO listes_colonnes (nom, position, id_projet, date_creation)
SELECT
    'À faire' as nom,
    0 as position,
    id as id_projet,
    CURRENT_TIMESTAMP as date_creation
FROM projets
UNION ALL
SELECT
    'En cours' as nom,
    1 as position,
    id as id_projet,
    CURRENT_TIMESTAMP as date_creation
FROM projets
UNION ALL
SELECT
    'Terminé' as nom,
    2 as position,
    id as id_projet,
    CURRENT_TIMESTAMP as date_creation
FROM projets;


-- ========================================
-- 4. MIGRER LES TÂCHES EXISTANTES VERS LES COLONNES
-- ========================================

-- Assigner les tâches BROUILLON et EN_ATTENTE_VALIDATION à "À faire"
UPDATE taches t
    INNER JOIN listes_colonnes lc ON lc.id_projet = t.id_projet AND lc.nom = 'À faire'
    SET t.id_liste_colonne = lc.id,
        t.position = 0
WHERE t.statut IN ('BROUILLON', 'EN_ATTENTE_VALIDATION')
  AND t.id_liste_colonne IS NULL;

-- Assigner les tâches TERMINE et ANNULE à "Terminé"
UPDATE taches t
    INNER JOIN listes_colonnes lc ON lc.id_projet = t.id_projet AND lc.nom = 'Terminé'
    SET t.id_liste_colonne = lc.id,
        t.position = 0
WHERE t.statut IN ('TERMINE', 'ANNULE')
  AND t.id_liste_colonne IS NULL;

-- Assigner les tâches restantes (s'il y en a) à "À faire"
UPDATE taches t
    INNER JOIN listes_colonnes lc ON lc.id_projet = t.id_projet AND lc.nom = 'À faire'
    SET t.id_liste_colonne = lc.id,
        t.position = 0
WHERE t.id_liste_colonne IS NULL;


-- ========================================
-- 5. TRIGGER: Créer automatiquement les colonnes pour nouveaux projets
-- ========================================

DELIMITER $$

CREATE TRIGGER trg_creer_colonnes_defaut
    AFTER INSERT ON projets
    FOR EACH ROW
BEGIN
    -- Créer "À faire"
    INSERT INTO listes_colonnes (nom, position, id_projet, date_creation)
    VALUES ('À faire', 0, NEW.id, CURRENT_TIMESTAMP);

    -- Créer "En cours"
    INSERT INTO listes_colonnes (nom, position, id_projet, date_creation)
    VALUES ('En cours', 1, NEW.id, CURRENT_TIMESTAMP);

    -- Créer "Terminé"
    INSERT INTO listes_colonnes (nom, position, id_projet, date_creation)
    VALUES ('Terminé', 2, NEW.id, CURRENT_TIMESTAMP);
    END$$

    DELIMITER ;


