package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "projets")
@Getter
@Setter
@NoArgsConstructor
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "id_createur", nullable = false)
    private Long idCreateur;

    @Column(nullable = false)
    private String statut = "ACTIF";

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    // Méthodes selon le diagramme de classes

    /**
     * Ajouter un projet
     */
    public boolean ajouterProjet(String titre, String description) {
        if (titre != null && !titre.trim().isEmpty()) {
            this.titre = titre;
            this.description = description;
            return true;
        }
        return false;
    }

    /**
     * Modifier un projet
     */
    public boolean modifierProjet(Long id, String titre, String description) {
        if (id != null && titre != null && !titre.trim().isEmpty()) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            return true;
        }
        return false;
    }

    /**
     * Supprimer un projet
     */
    public boolean supprimerProjet(Long id) {
        if (id != null) {

            return true;
        }
        return false;
    }
}