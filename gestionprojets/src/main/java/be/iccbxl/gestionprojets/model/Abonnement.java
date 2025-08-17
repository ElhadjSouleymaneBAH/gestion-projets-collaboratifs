package be.iccbxl.gestionprojets.model;

import be.iccbxl.gestionprojets.enums.StatutAbonnement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entité représentant un abonnement utilisateur pour les fonctionnalités premium.
 *
 * Supporte la fonctionnalité F10 : Paiements et abonnements du cahier des charges.
 * STRICTEMENT RÉSERVÉ AUX CHEFS DE PROJET qui doivent souscrire un abonnement
 * mensuel obligatoire pour accéder aux fonctionnalités avancées.
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @see "Cahier des charges - F10: Paiements et abonnements"
 * @see "Business plan - Abonnement mensuel obligatoire 10€/mois"
 */
@Entity
@Table(name = "abonnements")
@Getter
@Setter
@NoArgsConstructor
public class Abonnement {

    /**
     * Identifiant unique de l'abonnement.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de l'abonnement (ex: "Premium Mensuel").
     * Utilisé pour identifier le type d'abonnement souscrit.
     */
    @Column(nullable = false, length = 100)
    private String nom;

    /**
     * Prix mensuel de l'abonnement en euros.
     * Selon le business plan : 10€/mois pour le plan Premium.
     */
    @Column(nullable = false)
    private Double prix;

    /**
     * Durée de l'abonnement en mois.
     * Utilisée pour calculer la date de fin et les renouvellements.
     */
    @Column(nullable = false)
    private Integer duree;

    /**
     * Type d'abonnement selon le dictionnaire de données.
     * Valeurs possibles : 'gratuit', 'premium', 'entreprise'
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * Statut actuel de l'abonnement.
     * Utilise l'énumération existante du projet.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAbonnement statut = StatutAbonnement.ACTIF;

    /**
     * Date de début de l'abonnement.
     * Automatiquement définie à la souscription.
     */
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    /**
     * Date de fin de l'abonnement.
     * Calculée automatiquement : dateDebut + duree (en mois).
     * OBLIGATOIRE selon le cahier des charges (abonnements mensuels uniquement).
     */
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    /**
     * Indique si l'abonnement est actif à la date actuelle.
     * Calculé dynamiquement après chargement depuis la base.
     * Ne persiste pas en base - évite les problèmes MySQL avec CURDATE().
     */
    @Transient
    private boolean estActif;

    // RELATIONS JPA

    /**
     * Utilisateur propriétaire de cet abonnement.
     * Relation One-to-One bidirectionnelle avec l'entité Utilisateur.
     * UNIQUEMENT pour les Chefs de Projet selon le cahier des charges.
     *
     * @see Utilisateur#abonnement
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // CONSTRUCTEURS

    /**
     * Constructeur pour créer un nouvel abonnement.
     * Initialise automatiquement les dates et le statut.
     *
     * @param nom Nom de l'abonnement
     * @param prix Prix mensuel en euros
     * @param duree Durée en mois
     * @param utilisateur Utilisateur souscripteur (CHEF_PROJET uniquement)
     */
    public Abonnement(String nom, Double prix, Integer duree, Utilisateur utilisateur) {
        this.nom = nom;
        this.prix = prix;
        this.duree = duree;
        this.utilisateur = utilisateur;
        this.dateDebut = LocalDate.now();
        this.dateFin = this.dateDebut.plusMonths(duree);
        this.type = "premium"; // défaut selon business plan
        this.statut = StatutAbonnement.ACTIF;
    }

    // MÉTHODES MÉTIER (basées sur le diagramme de classes)

    /**
     * Calcule le montant TTC de l'abonnement.
     * Applique la TVA belge de 21% sur le prix HT.
     *
     * @return Le montant TTC en euros
     * @see "Diagramme de classes - Méthode calculMontantTTC()"
     */
    public Double calculMontantTTC() {
        double tva = 0.21; // TVA belge 21%
        return prix + (prix * tva);
    }

    /**
     * Vérifie si l'abonnement est valide (actif et non expiré).
     *
     * SELON LE CAHIER DES CHARGES :
     * - Tous les abonnements sont mensuels avec date de fin obligatoire
     * - Pas d'abonnements sans fin autorisés
     * - Réservé aux Chefs de Projet uniquement
     *
     * @return true si l'abonnement est valide, false sinon
     * @see "Cahier des charges - F6,F7,F8: Contrainte abonnement actif"
     * @see "Cahier des charges - Chef de projet: abonnement mensuel obligatoire"
     */
    public Boolean estValide() {
        // Vérifier d'abord le statut
        if (statut != StatutAbonnement.ACTIF) {
            return false;
        }

        // SELON CAHIER DES CHARGES : Abonnements mensuels uniquement
        // Tous ont une date de fin obligatoire
        if (dateFin == null) {
            return false;
        }

        return LocalDate.now().isBefore(dateFin) || LocalDate.now().isEqual(dateFin);
    }

    /**
     * Renouvelle l'abonnement pour la durée spécifiée.
     * Étend la date de fin et réactive l'abonnement si nécessaire.
     *
     * @return true si le renouvellement a réussi
     * @see "Diagramme de classes - Méthode renouveler()"
     */
    public Boolean renouveler() {
        if (dateFin != null) {
            this.dateFin = this.dateFin.plusMonths(duree);
        } else {
            this.dateFin = LocalDate.now().plusMonths(duree);
        }

        // Réactiver l'abonnement s'il était expiré
        if (statut != StatutAbonnement.ACTIF) {
            this.statut = StatutAbonnement.ACTIF;
        }

        return true;
    }

    // MÉTHODES DU CYCLE DE VIE JPA

    /**
     * Méthode appelée automatiquement avant la persistance.
     * Initialise les dates si elles ne sont pas définies.
     */
    @PrePersist
    protected void onCreate() {
        if (dateDebut == null) {
            dateDebut = LocalDate.now();
        }
        if (dateFin == null && duree != null) {
            dateFin = dateDebut.plusMonths(duree);
        }
    }

    /**
     * Méthode appelée automatiquement après chargement JPA.
     * Calcule la validité de l'abonnement selon le statut et la date de fin.
     */
    @PostLoad
    public void calculerEstActif() {
        this.estActif = this.statut == StatutAbonnement.ACTIF &&
                this.dateFin != null &&
                !this.dateFin.isBefore(LocalDate.now());
    }

    // GETTERS/SETTERS SUPPLÉMENTAIRES

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }

    // MÉTHODES UTILITAIRES

    /**
     * Représentation textuelle de l'abonnement.
     * Utilisée pour le débogage et les logs.
     *
     * @return String contenant les informations principales
     */
    @Override
    public String toString() {
        return String.format("Abonnement{id=%d, nom='%s', prix=%.2f€, type='%s', statut=%s, dateDebut=%s, dateFin=%s}",
                id, nom, prix, type, statut, dateDebut, dateFin);
    }

    /**
     * Comparaison d'égalité basée sur l'ID.
     * Deux abonnements sont égaux s'ils ont le même ID.
     *
     * @param obj Objet à comparer
     * @return true si les IDs sont identiques
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Abonnement that = (Abonnement) obj;
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