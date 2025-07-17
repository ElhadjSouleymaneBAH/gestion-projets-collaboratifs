-- Création de la table messages
CREATE TABLE messages (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          contenu TEXT NOT NULL,
                          date_envoi DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          type VARCHAR(20) DEFAULT 'TEXT',
                          statut VARCHAR(20) DEFAULT 'ENVOYE',
                          utilisateur_id BIGINT NOT NULL,
                          projet_id BIGINT NOT NULL,
                          date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
                          date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          PRIMARY KEY (id),

    -- Clés étrangères
                          FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
                          FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index pour performances (avec la table)
CREATE INDEX idx_messages_projet ON messages(projet_id);
CREATE INDEX idx_messages_utilisateur ON messages(utilisateur_id);
CREATE INDEX idx_messages_date_envoi ON messages(date_envoi);
CREATE INDEX idx_messages_type ON messages(type);
CREATE INDEX idx_messages_statut ON messages(statut);