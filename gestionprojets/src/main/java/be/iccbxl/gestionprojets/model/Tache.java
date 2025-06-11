package be.iccbxl.gestionprojets.model;

import be.iccbxl.gestionprojets.enums.StatusTache;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité Tache selon le diagramme de classes
 */
@Entity
@Table(name = "taches")
@Getter
@Setter
@NoArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "id_projet", nullable = false)
    private Long idProjet;

    @Column(name = "id_assigne")
    private Long idAssigne;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTache statut = StatusTache.BROUILLON;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    /**
     * Ajouter une tâche
     */
    public boolean ajouterTache(String titre, String description, Long idProjet) {
        if (titre != null && !titre.trim().isEmpty() && idProjet != null) {
            this.titre = titre;
            this.description = description;
            this.idProjet = idProjet;
            return true;
        }
        return false;
    }

    /**
     * Modifier une tâche
     */
    public boolean modifierTache(Long id, String titre, String description, StatusTache statut) {
        if (id != null && titre != null && !titre.trim().isEmpty()) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            this.statut = statut;
            return true;
        }
        return false;
    }

    /**
     * Supprimer une tâche
     */
    public boolean supprimerTache(Long id) {
        if (id != null) {
            return true;
        }
        return false;
    }
}