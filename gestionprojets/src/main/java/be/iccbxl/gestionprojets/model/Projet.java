package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un projet dans le système de gestion collaborative.
 *
 * Un projet peut être créé et géré uniquement par un Chef de Projet (avec abonnement actif)
 * ou un Administrateur selon le cahier des charges. Les projets peuvent être publics
 * (consultables par les Visiteurs) ou privés (accessibles aux membres seulment).
 *
 * Fonctionnalités couvertes :
 * - F3 : Consulter les projets publics
 * - F6 : Gérer les projets (création, modification, suppression)
 * - F8 : Ajouter des membres à un projet
 * - F9 : Collaborer en temps réel
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @since 2025-07-05
 * @see Utilisateur
 * @see Tache
 * @see "Cahier des charges - F6: Gérer les projets"
 */
@Entity
@Table(name = "projets")
@Getter
@Setter
@NoArgsConstructor
public class Projet {

    /** Identifiant unique du projet. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre du projet (obligatoire, utilisé pour l’affichage public F3). */
    @Column(nullable = false)
    private String titre;

    /** Description détaillée du projet (format TEXT). */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Créateur du projet.
     * Mappé sur la colonne id_createur (FK vers utilisateurs.id).
     * Nécessaire pour les autorisations de gestion (F6).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_createur", nullable = false)
    private Utilisateur createur;

    /**
     * Statut du projet.
     * Valeurs prévues : ACTIF, SUSPENDU, TERMINE, ARCHIVE (stockées en VARCHAR).
     */
    @Column(nullable = false)
    private String statut = "ACTIF";

    /** Date/heure de création (initialisée en @PrePersist). */
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // ========== RELATIONS JPA ==========

    /**
     * Membres du projet (ManyToMany via table de jointure).
     * Supporte F8 : Ajouter des membres.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "projet_utilisateurs",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    /**
     * Tâches du projet (OneToMany).
     * Supporte F7 : Gérer les tâches.
     */
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tache> taches = new HashSet<>();

    // ========== CYCLE DE VIE ==========

    /** Initialise la date de création si absente. */
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    // ========== MÉTHODES MÉTIER ==========

    /**
     * Ajoute un membre (relation bidirectionnelle).
     * F8 : Ajouter des membres à un projet.
     */
    public void ajouterMembre(Utilisateur utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        membres.add(utilisateur);
        utilisateur.getProjets().add(this);
    }

    /**
     * Retire un membre (relation bidirectionnelle).
     * F8 : Gérer les membres d'un projet.
     */
    public void retirerMembre(Utilisateur utilisateur) {
        if (utilisateur != null) {
            membres.remove(utilisateur);
            utilisateur.getProjets().remove(this);
        }
    }

    /**
     * Initialise un nouveau projet (F6).
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
     * Modifie le projet (F6).
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
     * Supprime le projet (F6) – logique métier gérée au service.
     */
    public boolean supprimerProjet(Long id) {
        return id != null;
    }

    /**
     * Le projet est-il visible publiquement ? (F3)
     */
    public boolean estPublic() {
        return "ACTIF".equals(this.statut);
    }

    /**
     * @return true si l'utilisateur est membre du projet
     */
    public boolean contientMembre(Utilisateur utilisateur) {
        return utilisateur != null && membres.contains(utilisateur);
    }

    /**
     * @return nombre de tâches non terminées (indicatif)
     */
    public long getNombreTachesActives() {
        return taches.stream()
                .filter(t -> !"TERMINE".equals(t.getStatut().toString()))
                .count();
    }

    // ========== UTILITAIRES ==========

    /** Accès pratique à l'ID du créateur pour compatibilité éventuelle. */
    @Transient
    public Long getIdCreateur() {
        return (createur != null) ? createur.getId() : null;
    }

    @Override
    public String toString() {
        return String.format(
                "Projet{id=%d, titre='%s', statut='%s', createurId=%s, membres=%d, taches=%d}",
                id, titre, statut, getIdCreateur(), membres.size(), taches.size()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Projet that)) return false;
        if (id != null && that.id != null) return id.equals(that.id);
        // fallback sur titre + créateur
        Long c1 = (createur != null) ? createur.getId() : null;
        Long c2 = (that.createur != null) ? that.createur.getId() : null;
        return titre != null ? titre.equals(that.titre) && (c1 != null ? c1.equals(c2) : c2 == null)
                : that.titre == null;
    }

    @Override
    public int hashCode() {
        if (id != null) return id.hashCode();
        int result = (titre != null) ? titre.hashCode() : 0;
        result = 31 * result + ((createur != null && createur.getId() != null) ? createur.getId().hashCode() : 0);
        return result;
    }
}
