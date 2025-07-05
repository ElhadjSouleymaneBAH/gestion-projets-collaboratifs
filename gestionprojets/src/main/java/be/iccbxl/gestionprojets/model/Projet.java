package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité Projet selon le diagramme de classes
 */
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

    // Relations JPA
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "projet_membres",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tache> taches = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    /**
     * Ajouter un membre au projet
     */
    public void ajouterMembre(Utilisateur utilisateur) {
        membres.add(utilisateur);
        utilisateur.getProjets().add(this);
    }

    /**
     * Retirer un membre du projet
     */
    public void retirerMembre(Utilisateur utilisateur) {
        membres.remove(utilisateur);
        utilisateur.getProjets().remove(this);
    }

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