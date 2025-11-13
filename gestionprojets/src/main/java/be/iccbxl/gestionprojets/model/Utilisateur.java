package be.iccbxl.gestionprojets.model;

import be.iccbxl.gestionprojets.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité représentant un utilisateur du système de gestion de projets collaboratifs.
 *
 * Cette classe gère les quatre types d'utilisateurs définis dans le cahier des charges :
 * - VISITEUR : Consultation des projets publics uniquement
 * - MEMBRE : Participation aux projets et collaboration en temps réel
 * - CHEF_PROJET : Création et gestion complète de projets (abonnement requis)
 * - ADMINISTRATEUR: Gestion globale de la plateforme
 *
 * Fonctionnalités couvertes : F1 (S'inscrire), F4-F5 (Profil), F8 (Membres), F12 (Multilingue)
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    /**
     * Identifiant unique de l'utilisateur.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de famille de l'utilisateur.
     * Champ obligatoire pour l'identification selon F1.
     */
    @Column(nullable = false)
    private String nom;

    /**
     * Prénom de l'utilisateur.
     * Champ obligatoire pour l'identification selon F1.
     */
    @Column(nullable = false)
    private String prenom;

    /**
     * Adresse email de l'utilisateur.
     * Doit être unique dans le système et sert d'identifiant de connexion (F1, F2).
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Mot de passe haché de l'utilisateur.
     * Utilise BCrypt pour le hachage sécurisé selon les contraintes de sécurité.
     * Le champ est exclu de la sérialisation JSON en lecture pour la sécurité.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    /**
     * Rôle de l'utilisateur dans le système.
     * Détermine les permissions et fonctionnalités accessibles.
     * Par défaut : MEMBRE selon le cahier des charges.
     *
     * @see Role
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBRE;

    /**
     * Langue préférée de l'utilisateur.
     * Utilisée pour l'interface multilingue (français/anglais) - Fonctionnalité F12.
     * Par défaut : "fr" (français).
     */
    @Column(nullable = false)
    private String langue = "fr";

    /**
     * Indicateur d'acceptation des Conditions Générales d'Utilisation.
     * Obligatoire pour la conformité RGPD selon les contraintes du cahier des charges.
     */
    @Column(name = "cgu_accepte", nullable = false)
    private boolean cguAccepte;

    /**
     * Date et heure d'inscription de l'utilisateur.
     * Automatiquement définie lors de la création via @PrePersist.
     */
    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    /**
     * Adresse postale de l'utilisateur.
     * Champ optionnel pour les informations de contact et facturation.
     * Supporte les fonctionnalités F10-F11 : Facturation et données de contact.
     */
    @Column(name = "adresse")
    private String adresse;

    //  RELATIONS JPA
    /**
     * Liste des projets auxquels l'utilisateur participe en tant que membre.
     * Relation Many-to-Many bidirectionnelle avec l'entité Projet.
     * Supporte la fonctionnalité F8 : Ajouter des membres à un projet.
     *
     * @see Projet membres
     */
    @ManyToMany(mappedBy = "membres", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Projet> projets = new HashSet<>();

    /**
     * Liste des tâches assignées à cet utilisateur.
     * Relation One-to-Many avec l'entité Tache.
     * Supporte la fonctionnalité F7 : Gérer les tâches (assignation).

     */
    @OneToMany(mappedBy = "assigneA", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Tache> tachesAssignees = new HashSet<>();

    /**
     * Historique des transactions financières de l'utilisateur.
     * Inclut les paiements d'abonnements et autres transactions.
     * Supporte les fonctionnalités F10-F11 : Paiements et factures.
     *
     * @see Transaction
     */
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Transaction> transactions = new HashSet<>();

    /**
     * Abonnement actuel de l'utilisateur (obligatoire pour les Chefs de Projet).
     * Relation One-to-One avec l'entité Abonnement.
     * Supporte la fonctionnalité F10 : Paiements et abonnements.
     *
     * @see Abonnement
     */
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Abonnement abonnement;

    /**
     * Notifications reçues par l'utilisateur.
     * Inclut les alertes de collaboration, tâches, paiements, etc.
     * Supporte la fonctionnalité F9 : Collaboration en temps réel.
     *
     * @see Notification
     */
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Notification> notifications = new HashSet<>();

    /**
     * Commentaires rédigés par l'utilisateur.
     * Relation One-to-Many avec l'entité Commentaire.
     * Supporte la collaboration et communication dans les projets.
     *
     * @see Commentaire auteur
     */
    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Commentaire> commentaires = new HashSet<>();

    // MÉTHODES DU CYCLE DE VIE JPA

    /**
     * Méthode appelée automatiquement avant la persistance.
     * Initialise la date d'inscription si elle n'est pas définie.
     */
    @PrePersist
    protected void onCreate() {
        if (dateInscription == null) {
            dateInscription = LocalDateTime.now();
        }
    }

    //  MÉTHODES MÉTIER

    /**
     * Active le compte utilisateur selon les exigences du cahier des charges.
     * Vérifie l'acceptation des CGU pour la conformité RGPD.
     *
     * Fonctionnalité F1 : S'inscrire - Finalise la création du compte
     *
     * @throws IllegalArgumentException si les CGU ne sont pas acceptées
     * @see "Cahier des charges - F1: S'inscrire"
     * @see "Contraintes RGPD - Acceptation CGU obligatoire"
     */
    public void activerCompte() {
        if (!this.cguAccepte) {
            throw new IllegalArgumentException("Acceptation des CGU obligatoire (RGPD)");
        }
        // Logique d'activation conforme au cahier des charges
    }

    /**
     * Change le mot de passe de l'utilisateur.
     * Conformément à la fonctionnalité F5 du cahier des charges.
     *
     * @param nouveauMotDePasse Le nouveau mot de passe (sera haché par le système de sécurité)
     * @return true si le changement a réussi, false si le mot de passe est invalide
     * @see "Cahier des charges - F5: Mettre à jour son profil"
     */
    public boolean changerMotDePasse(String nouveauMotDePasse) {
        if (nouveauMotDePasse != null && !nouveauMotDePasse.trim().isEmpty()) {
            this.motDePasse = nouveauMotDePasse;
            return true;
        }
        return false;
    }

    /**
     * Vérifie si l'utilisateur peut créer des projets.
     * Selon le cahier des charges, seuls les Chefs de Projet et Administrateurs peuvent créer des projets.
     *
     * @return true si l'utilisateur est Chef de Projet ou Administrateur
     * @see "Cahier des charges - F6: Gérer les projets"
     */
    public boolean peutCreerProjets() {
        return role == Role.CHEF_PROJET || role == Role.ADMINISTRATEUR;
    }

    /**
     * Retourne le nom complet de l'utilisateur.
     * Utilisé pour l'affichage dans l'interface utilisateur.
     *
     * @return Prénom + Nom
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    //  MÉTHODES UTILITAIRES
    /**
     * Représentation textuelle de l'utilisateur.
     * Utilisée pour le débogage et les logs.
     *
     * @return String contenant les informations principales
     */
    @Override
    public String toString() {
        return String.format("Utilisateur{id=%d, email='%s', role=%s, nom='%s'}",
                id, email, role, getNomComplet());
    }

    /**
     * Comparaison d'égalité basée sur l'email.
     * Deux utilisateurs sont égaux s'ils ont le même email.
     *
     * @param obj Objet à comparer
     * @return true si les emails sont identiques
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Utilisateur that = (Utilisateur) obj;
        return email != null ? email.equals(that.email) : that.email == null;
    }

    /**
     * Code de hachage basé sur l'email.
     * Utilisé dans les collections (HashMap, HashSet).
     *
     * @return Hash code de l'email
     */
    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}