package be.iccbxl.gestionprojets.model;

import be.iccbxl.gestionprojets.enums.StatusTache;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTache statut = StatusTache.BROUILLON;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // Relations JPA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projet", nullable = false)
    private Projet projet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assigne")
    private Utilisateur assigneA;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Commentaire> commentaires = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    /**
     * Assigner la tâche à un utilisateur
     */
    public void assignerA(Utilisateur utilisateur) {
        if (this.assigneA != null) {
            this.assigneA.getTachesAssignees().remove(this);
        }
        this.assigneA = utilisateur;
        if (utilisateur != null) {
            utilisateur.getTachesAssignees().add(this);
        }
    }

    /**
     * Ajouter une tâche
     */
    public boolean ajouterTache(String titre, String description, Projet projet) {
        if (titre != null && !titre.trim().isEmpty() && projet != null) {
            this.titre = titre;
            this.description = description;
            this.projet = projet;
            projet.getTaches().add(this);
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
            if (this.projet != null) {
                this.projet.getTaches().remove(this);
            }
            if (this.assigneA != null) {
                this.assigneA.getTachesAssignees().remove(this);
            }
            return true;
        }
        return false;
    }
}