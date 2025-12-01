package be.iccbxl.gestionprojets.model;

import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.enums.PrioriteTache;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité représentant une tâche dans le système de gestion de projets collaboratifs.
 *
 * Les tâches sont créées par les Membres et gérées par les Chefs de Projet selon
 * le diagramme d'état de transition défini dans le cahier des charges.
 *
 * États possibles selon le diagramme :
 * - BROUILLON : Tâche créée mais non validée
 * - EN_ATTENTE_VALIDATION : Soumise pour validation par le Chef de Projet
 * - TERMINE : Tâche complétée et validée
 * - ANNULE : Tâche annulée (possible à tout moment par Admin)
 *
 * Fonctionnalités couvertes :
 * - F7 : Gérer les tâches (création, assignation, modification)
 * - F9 : Collaborer en temps réel (commentaires, notifications)
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @see StatutTache
 * @see PrioriteTache
 * @see Projet
 * @see Utilisateur
 * @see "Cahier des charges - F7: Gérer les tâches"
 * @see "Diagramme d'état de transition - Tâches"
 */
@Entity
@Table(name = "taches")
@Getter
@Setter
@NoArgsConstructor
public class Tache {

    /**
     * Identifiant unique de la tâche.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre de la tâche.
     * Champ obligatoire pour identifier la tâche dans les listes et notifications.
     */
    @Column(nullable = false)
    private String titre;

    /**
     * Description détaillée de la tâche.
     * Stockée en format TEXT pour permettre des descriptions complètes.
     * Utilisée pour la collaboration et la compréhension des objectifs.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Statut actuel de la tâche selon le diagramme d'état de transition.
     *
     * Transitions possibles :
     * - Membre : Créer (→BROUILLON), Compléter (BROUILLON→EN_ATTENTE_VALIDATION)
     * - Chef de Projet : Valider, Modifier, Annuler
     * - Admin : Annuler (à tout moment)
     *
     * @see StatutTache
     * @see "Diagramme d'état de transition - Cahier des charges"
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTache statut = StatutTache.BROUILLON;

    /**
     * Priorité de la tâche.
     * Permet de hiérarchiser les tâches dans le projet.
     * Par défaut : NORMALE
     *
     * @see PrioriteTache
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioriteTache priorite = PrioriteTache.NORMALE;

    /**
     * Date d'échéance de la tâche.
     * Optionnelle - permet de suivre les délais et identifier les retards.
     * Utilisée pour les notifications et l'affichage des tâches en retard.
     */
    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    /**
     * Date et heure de création de la tâche.
     * Automatiquement définie lors de la création via @PrePersist.
     * Utilisée pour l'historique et le suivi temporel.
     */
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // ========== RELATIONS JPA ==========

    /**
     * Projet auquel appartient cette tâche.
     * Relation Many-to-One obligatoire avec l'entité Projet.
     *
     * Une tâche appartient toujours à un projet spécifique.
     * Supporte la fonctionnalité F7 : Gérer les tâches dans le contexte d'un projet.
     *
     * @see Projet/ taches
     * @see "Cahier des charges - F7: Gérer les tâches"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projet", nullable = false)
    private Projet projet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_liste_colonne")
    private ListeColonne listeColonne;

    @Column(nullable = false)
    private Integer position = 0;

    /**
     * Utilisateur à qui la tâche est assignée.
     * Relation Many-to-One optionnelle avec l'entité Utilisateur.
     * Une tâche peut être non assignée (null) ou assignée à un membre du projet.
     * L'assignation permet le suivi des responsabilités et la collaboration.
     *
     * @see Utilisateur/ tachesAssignees
     * @see "Cahier des charges - F7 : Gérer les tâches - Assignation"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assigne")
    private Utilisateur assigneA;

    /**
     * Commentaires associés à cette tâche.
     * Relation One-to-Many avec l'entité Commentaire.

     * Supporte la fonctionnalité F9 : Collaborer en temps réel.
     * Les commentaires permettent la communication autour des tâches.
     *
     * @see Commentaire/ tache
     * @see "Cahier des charges - F9 : Collaborer en temps réel"
     */
    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Commentaire> commentaires = new HashSet<>();

    // ========== MÉTHODES DU CYCLE DE VIE JPA ==========

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

    // ========== MÉTHODES MÉTIER ==========

    /**
     * Assigne la tâche à un utilisateur.
     * Gère automatiquement la relation bidirectionnelle Many-to-One.
     *
     * Fonctionnalité F7 : Gérer les tâches - Assignation.
     * Seuls les Chefs de Projet peuvent assigner des tâches selon le cahier des charges.
     *
     * @param utilisateur L'utilisateur à qui assigner la tâche (peut être null pour désassigner)
     * @see Utilisateur#getTachesAssignees()
     * @see "Cahier des charges - F7: Gérer les tâches"
     */
    public void assignerA(Utilisateur utilisateur) {
        // Retirer la tâche de l'ancien assigné
        if (this.assigneA != null) {
            this.assigneA.getTachesAssignees().remove(this);
        }

        // Assigner au nouvel utilisateur
        this.assigneA = utilisateur;

        // Ajouter la tâche à la liste du nouvel assigné
        if (utilisateur != null) {
            utilisateur.getTachesAssignees().add(this);
        }
    }

    /**
     * Initialise une nouvelle tâche avec les données fournies.
     * Conformément à la fonctionnalité F7 du cahier des charges.
     *
     * Note: Cette méthode configure l'instance actuelle plutôt que de créer une nouvelle tâche.
     * Dans un contexte réel, cette logique serait dans un service dédié.
     *
     * @param titre Le titre de la tâche (obligatoire)
     * @param description La description détaillée de la tâche (optionnelle)
     * @param projet Le projet auquel appartient la tâche (obligatoire)
     * @return true si la tâche a été configurée avec succès, false sinon
     * @throws IllegalArgumentException si le titre est vide ou le projet null
     * @see "Cahier des charges - F7: Gérer les tâches - Création"
     */
    public boolean ajouterTache(String titre, String description, Projet projet) {
        if (titre != null && !titre.trim().isEmpty() && projet != null) {
            this.titre = titre;
            this.description = description;
            this.projet = projet;

            // Maintenir la relation bidirectionnelle
            projet.getTaches().add(this);
            return true;
        }
        return false;
    }

    /**
     * Modifie les propriétés de la tâche.
     * Conformément à la fonctionnalité F7 du cahier des charges.
     *
     * Permissions selon le diagramme d'état :
     * - Chef de Projet : peut modifier à tout moment
     * - Membre : peut modifier uniquement ses tâches en statut BROUILLON
     *
     * IMPORTANT : L'ID de la tâche ne doit jamais être modifié car c'est la clé primaire.
     *
     * @param titre Le nouveau titre (obligatoire)
     * @param description La nouvelle description (optionnelle)
     * @param statut Le nouveau statut de la tâche
     * @return true si la modification a réussi, false sinon
     * @see StatutTache
     * @see "Cahier des charges - F7: Gérer les tâches - Modification"
     * @see "Diagramme d'état de transition"
     */
    public boolean modifierTache(String titre, String description, StatutTache statut) {
        if (titre != null && !titre.trim().isEmpty()) {
            this.titre = titre;
            this.description = description;
            if (statut != null) {
                this.statut = statut;
            }
            return true;
        }
        return false;
    }

    /**
     * Supprime la tâche et nettoie toutes les relations.
     * Conformément à la fonctionnalité F7 du cahier des charges.
     *
     * Retire la tâche du projet et de l'utilisateur assigné pour maintenir
     * la cohérence des relations bidirectionnelles.
     *
     * Permissions selon le cahier des charges :
     * - Chef de Projet : peut supprimer les tâches de son projet
     * - Administrateur : peut supprimer toute tâche
     *
     * @return true si la suppression a réussi, false sinon
     * @see "Cahier des charges - F7: Gérer les tâches - Suppression"
     */
    public boolean supprimerTache() {
        // Nettoyer la relation avec le projet
        if (this.projet != null) {
            this.projet.getTaches().remove(this);
        }

        // Nettoyer la relation avec l'utilisateur assigné
        if (this.assigneA != null) {
            this.assigneA.getTachesAssignees().remove(this);
        }

        return true;
    }

    /**
     * Change le statut de la tâche selon les transitions autorisées.
     * Respecte le diagramme d'état de transition du cahier des charges.
     *
     * Transitions autorisées :
     * - BROUILLON → EN_ATTENTE_VALIDATION (Membre : Compléter)
     * - BROUILLON → ANNULE (Admin : Annuler)
     * - EN_ATTENTE_VALIDATION → TERMINE (Chef de Projet : Valider)
     * - EN_ATTENTE_VALIDATION → BROUILLON (Chef de Projet : Renvoyer en brouillon)
     * - EN_ATTENTE_VALIDATION → ANNULE (Admin/Chef de Projet : Annuler)
     * - TERMINE → ANNULE (Admin : Annuler)
     * - ANNULE → État final (aucune transition possible)
     *
     * @param nouveauStatut Le nouveau statut souhaité
     * @return true si la transition est autorisée et effectuée
     * @see StatutTache
     * @see "Diagramme d'état de transition - Cahier des charges"
     */
    public boolean changerStatut(StatutTache nouveauStatut) {
        if (nouveauStatut == null) {
            return false;
        }

        // Vérification des transitions autorisées selon le diagramme d'état
        switch (this.statut) {
            case BROUILLON:
                if (nouveauStatut == StatutTache.EN_ATTENTE_VALIDATION ||
                        nouveauStatut == StatutTache.ANNULE) {
                    this.statut = nouveauStatut;
                    return true;
                }
                break;

            case EN_ATTENTE_VALIDATION:
                if (nouveauStatut == StatutTache.TERMINE ||
                        nouveauStatut == StatutTache.BROUILLON ||
                        nouveauStatut == StatutTache.ANNULE) {
                    this.statut = nouveauStatut;
                    return true;
                }
                break;

            case TERMINE:
                // Une tâche terminée peut être modifiée (retour en validation) ou annulée
                if (nouveauStatut == StatutTache.ANNULE ||
                        nouveauStatut == StatutTache.EN_ATTENTE_VALIDATION) {
                    this.statut = nouveauStatut;
                    return true;
                }
                break;

            case ANNULE:
                // Une tâche annulée reste annulée (état final)
                break;
        }

        return false; // Transition non autorisée
    }

    /**
     * Vérifie si la tâche peut être modifiée par un utilisateur donné.
     * Applique les règles de permissions du cahier des charges.
     *
     * @param utilisateur L'utilisateur qui souhaite modifier la tâche
     * @return true si l'utilisateur peut modifier cette tâche
     */
    public boolean peutEtreModifieePar(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return false;
        }

        // Admin peut tout modifier
        if (utilisateur.getRole() == be.iccbxl.gestionprojets.enums.Role.ADMINISTRATEUR) {
            return true;
        }

        // Chef de Projet peut modifier les tâches de ses projets
        if (utilisateur.getRole() == be.iccbxl.gestionprojets.enums.Role.CHEF_PROJET) {
            return true; // Vérification du propriétaire du projet à faire dans le service
        }

        // Membre peut modifier ses tâches en brouillon
        if (utilisateur.equals(this.assigneA) && this.statut == StatutTache.BROUILLON) {
            return true;
        }

        return false;
    }

    /**
     * Vérifie si la tâche est en retard.
     * Utilisé pour les notifications et l'affichage dans l'interface.
     *
     * Une tâche est considérée en retard si :
     * - Elle a une date d'échéance définie
     * - La date actuelle dépasse la date d'échéance
     * - Elle n'est pas dans un état final (TERMINE ou ANNULE)
     *
     * @return true si la tâche est en retard
     */
    public boolean estEnRetard() {
        // Une tâche sans date d'échéance ne peut pas être en retard
        if (this.dateEcheance == null) {
            return false;
        }

        // Une tâche terminée ou annulée n'est plus considérée en retard
        if (this.statut == StatutTache.TERMINE || this.statut == StatutTache.ANNULE) {
            return false;
        }

        // Vérifier si la date d'échéance est dépassée
        LocalDate aujourdhui = LocalDate.now();
        return aujourdhui.isAfter(this.dateEcheance);
    }

    /**
     * Retourne le nombre de commentaires sur cette tâche.
     * Utilisé pour l'affichage des statistiques de collaboration.
     *
     * @return Le nombre de commentaires
     */
    public int getNombreCommentaires() {
        return commentaires != null ? commentaires.size() : 0;
    }

    // ← AJOUTE ICI
    public void deplacerVersColonne(ListeColonne nouvelleColonne) {
        if (this.listeColonne != null) {
            this.listeColonne.getTaches().remove(this);
        }
        this.listeColonne = nouvelleColonne;
        if (nouvelleColonne != null) {
            nouvelleColonne.getTaches().add(this);
            this.position = nouvelleColonne.getNombreTaches();
        }
    }

    // ========== MÉTHODES UTILITAIRES ==========



    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Représentation textuelle de la tâche.
     * Utilisée pour le débogage et les logs.
     *
     * @return String contenant les informations principales
     */
    @Override
    public String toString() {
        return String.format("Tache{id=%d, titre='%s', statut=%s, priorite=%s, dateEcheance=%s, projet=%s, assigneA=%s}",
                id, titre, statut, priorite, dateEcheance,
                projet != null ? projet.getTitre() : "null",
                assigneA != null ? assigneA.getEmail() : "non assignée");
    }

    /**
     * Comparaison d'égalité basée sur l'ID.
     * Deux tâches sont égales si elles ont le même ID.
     *
     * @param obj Objet à comparer
     * @return true si les tâches sont identiques
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Tache that = (Tache) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    /**
     * Code de hachage basé sur l'ID.
     * Utilisé dans les collections (HashMap, HashSet).
     *
     * @return Hash code de l'ID
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}