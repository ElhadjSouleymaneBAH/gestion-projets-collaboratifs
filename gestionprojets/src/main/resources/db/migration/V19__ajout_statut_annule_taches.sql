-- Synchroniser àla BD avec enum StatutTache du code
ALTER TABLE taches
    MODIFY statut ENUM(
    'BROUILLON',
    'EN_ATTENTE_VALIDATION',
    'TERMINE',
    'ANNULE'
    ) DEFAULT 'BROUILLON';