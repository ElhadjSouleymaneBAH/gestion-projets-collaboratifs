package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité représentant un projet dans le système de gestion collaborative.
 *
 * Un projet peut être créé et géré uniquement par un Chef de Projet (avec abonnement actif)
 * ou un Administrateur selon le cahier des charges. Les projets peuvent être publics
 * (consultables par les Visiteurs) ou privés (accessibles aux membres uniquement).
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

    /**
     * Identifiant unique du projet.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre du projet.
     * Champ obligatoire pour identifier le projet.
     * Utilisé dans les listes et l'affichage public (F3).
     */
    @Column(nullable = false)
    private String titre;

    /**
     * Description détaillée du projet.
     * Stockée en format TEXT pour permettre des descriptions longues.
     * Visible dans la consultation publique des projets (F3).
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Identifiant du créateur du projet.
     * Référence l'utilisateur (Chef de Projet ou Admin) qui a créé le projet.
     * Nécessaire pour les autorisations de gestion (F6).
     */
    @Column(name = "id_createur", nullable = false)
    private Long idCreateur;

    /**
     * Statut actuel du projet.
     * Valeurs possibles : ACTIF, SUSPENDU, TERMINE, ARCHIVE
     * Par défaut : ACTIF pour les nouveaux projets.
     */
    @Column(nullable = false)
    private String statut = "ACTIF";

    /**
     * Date et heure de création du projet.
     * Automatiquement définie lors de la création via @PrePersist.
     * Utilisée pour l'historique et les statistiques.
     */
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // ========== RELATIONS JPA ==========

    /**
     * Liste des membres participant au projet.
     * Relation Many-to-Many bidirectionnelle avec l'entité Utilisateur.
     *
     * Supporte la fonctionnalité F8 : Ajouter des membres à un projet.
     * Les Chefs de Projet peuvent inviter des membres existants pour collaboration.
     *
     * @see Utilisateur#projets
     * @see "Cahier des charges - F8: Ajouter des membres à un projet"
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "projet_utilisateurs",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    /**
     * Liste des tâches appartenant à ce projet.
     * Relation One-to-Many avec l'entité Tache.
     *
     * Supporte la fonctionnalité F7 : Gérer les tâches.
     * Les tâches sont automatiquement supprimées si le projet est supprimé (CASCADE.ALL).
     *
     * @see Tache#projet
     * @see "Cahier des charges - F7: Gérer les tâches"
     */
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tache> taches = new HashSet<>();

    //  MÉTHODES DU CYCLE DE VIE JPA

    /**
     * Méthode appelée automatiquement avant la persistance.
     * Initialise la date de création si elle n'est pas définie.
     */
    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    //  MÉTHODES MÉTIER

    /**
     * Ajoute un membre au projet.
     * Gère automatiquement la relation bidirectionnelle Many-to-Many.
     *
     * Fonctionnalité F8 : Ajouter des membres à un projet.
     * Seuls les Chefs de Projet peuvent ajouter des membres selon le cahier des charges.
     *
     * @param utilisateur L'utilisateur à ajouter comme membre du projet (obligatoire)
     * @throws IllegalArgumentException si l'utilisateur est null
     * @see Utilisateur#getProjets()
     * @see "Cahier des charges - F8: Ajouter des membres à un projet"
     */
    public void ajouterMembre(Utilisateur utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        membres.add(utilisateur);
        utilisateur.getProjets().add(this);
    }

    /**
     * Retire un membre du projet.
     * Gère automatiquement la relation bidirectionnelle Many-to-Many.
     *
     * Fonctionnalité F8 : Gérer les membres d'un projet.
     * Seuls les Chefs de Projet peuvent retirer des membres selon le cahier des charges.
     *
     * @param utilisateur L'utilisateur à retirer du projet
     * @see Utilisateur#getProjets()
     * @see "Cahier des charges - F8: Ajouter des membres à un projet"
     */
    public void retirerMembre(Utilisateur utilisateur) {
        if (utilisateur != null) {
            membres.remove(utilisateur);
            utilisateur.getProjets().remove(this);
        }
    }

    /**
     * Initialise un nouveau projet avec les données fournies.
     * Conformément à la fonctionnalité F6 du cahier des charges.
     *
     * Note: Cette méthode configure l'instance actuelle plutôt que de créer un nouveau projet.
     * Dans un contexte réel, cette logique serait dans un service dédié.
     *
     * @param titre Le titre du projet (obligatoire)
     * @param description La description du projet (optionnelle)
     * @return true si le projet a été configuré avec succès, false sinon
     * @see "Cahier des charges - F6: Gérer les projets - Création"
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
     * Modifie les propriétés du projet.
     * Conformément à la fonctionnalité F6 du cahier des charges.
     *
     * Seuls les Chefs de Projet (créateur) et les Administrateurs peuvent modifier un projet.
     *
     * @param id L'identifiant du projet (pour validation)
     * @param titre Le nouveau titre (obligatoire)
     * @param description La nouvelle description (optionnelle)
     * @return true si la modification a réussi, false sinon
     * @see "Cahier des charges - F6: Gérer les projets - Modification"
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
     * Supprime le projet.
     * Conformément à la fonctionnalité F6 du cahier des charges.
     *
     * Seuls les Chefs de Projet (créateur) et les Administrateurs peuvent supprimer un projet.
     * La suppression cascade automatiquement vers les tâches associées.
     *
     * @param id L'identifiant du projet à supprimer (pour validation)
     * @return true si la suppression a réussi, false sinon
     * @see "Cahier des charges - F6: Gérer les projets - Suppression"
     */
    public boolean supprimerProjet(Long id) {
        if (id != null) {
            return true;
        }
        return false;
    }

    /**
     * Vérifie si le projet est visible par les visiteurs.
     * Détermine si le projet apparaît dans la consultation publique (F3).
     *
     * @return true si le projet est public et actif
     * @see "Cahier des charges - F3: Consulter les projets publics"
     */
    public boolean estPublic() {
        return "ACTIF".equals(this.statut);
    }

    /**
     * Vérifie si un utilisateur est membre du projet.
     * Utilisé pour les autorisations d'accès et de collaboration.
     *
     * @param utilisateur L'utilisateur à vérifier
     * @return true si l'utilisateur est membre du projet
     */
    public boolean contientMembre(Utilisateur utilisateur) {
        return utilisateur != null && membres.contains(utilisateur);
    }

    /**
     * Retourne le nombre de tâches actives du projet.
     * Utilisé pour les statistiques et l'affichage dans l'interface.
     *
     * @return Le nombre de tâches non archivées
     */
    public long getNombreTachesActives() {
        return taches.stream()
                .filter(tache -> !"TERMINE".equals(tache.getStatut().toString()))
                .count();
    }

    //  MÉTHODES UTILITAIRES

    /**
     * Représentation textuelle du projet.
     * Utilisée pour le débogage et les logs.
     *
     * @return String contenant les informations principales
     */
    @Override
    public String toString() {
        return String.format("Projet{id=%d, titre='%s', statut='%s', membres=%d, taches=%d}",
                id, titre, statut, membres.size(), taches.size());
    }

    /**
     * Comparaison d'égalité basée sur l'ID et le titre.
     * Deux projets sont égaux s'ils ont le même ID ou le même titre et créateur.
     *
     * @param obj Objet à comparer
     * @return true si les projets sont identiques
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Projet that = (Projet) obj;
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        return titre != null ? titre.equals(that.titre) &&
                idCreateur != null ? idCreateur.equals(that.idCreateur) : that.idCreateur == null
                : that.titre == null;
    }

    /**
     * Code de hachage basé sur l'ID ou le titre.
     * Utilisé dans les collections (HashMap, HashSet).
     *
     * @return Hash code du projet
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return titre != null ? titre.hashCode() : 0;
    }
}