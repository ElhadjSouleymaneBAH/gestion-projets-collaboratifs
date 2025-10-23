package be.iccbxl.gestionprojets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité Transaction selon le diagramme de classes
 *
 * Représente une transaction financière dans le système de gestion de projets.
 * Gère les paiements d'abonnements et la génération de factures.
 * Fonctionnalités couvertes :
 * F10 : Effectuer des paiements via Stripe
 * F11 : Générer des factures automatiquement
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @since 2025-07-14
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "montant_ht", nullable = false)
    private Double montantHT;

    @Column(nullable = false)
    private Double tva;

    @Column(name = "montant_ttc", nullable = false)
    private Double montantTTC;

    @Column(name = "montant_abonnement", nullable = false)
    private Double montantAbonnement;

    @Column(nullable = false)
    private String statut;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // ========== RELATIONS JPA ==========

    /**
     * Utilisateur qui a effectué la transaction.
     * Relation Many-to-One avec l'entité Utilisateur.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    @JsonIgnoreProperties({"transactions", "projets", "abonnement", "notifications", "commentaires", "tachesAssignees", "motDePasse"})
    private Utilisateur utilisateur;

    /**
     * Facture générée pour cette transaction.
     * Relation One-to-One bidirectionnelle avec l'entité Facture.
     */
    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("transaction")
    private Facture facture;

    // ========== CONSTRUCTEURS ==========

    /**
     * Constructeur pour créer une nouvelle transaction.
     *
     * @param montantHT Montant hors taxes
     * @param utilisateur Utilisateur effectuant la transaction
     */
    public Transaction(Double montantHT, Utilisateur utilisateur) {
        this.montantHT = montantHT;
        this.utilisateur = utilisateur;
        this.tva = montantHT * 0.21; // TVA 21%
        this.montantTTC = montantHT + this.tva;
        this.montantAbonnement = montantHT;
        this.statut = "EN_ATTENTE";
        this.dateCreation = LocalDateTime.now();
    }

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

    // ========== GETTERS PERSONNALISÉS POUR LE FRONTEND ==========

    /**
     * Retourne l'ID de l'utilisateur pour le frontend.
     * Permet au frontend d'accéder directement à l'ID sans charger l'objet complet.
     * Utilisé dans le tableau de bord admin pour afficher le nom de l'utilisateur.
     *
     * @return L'ID de l'utilisateur ou null si aucun utilisateur n'est associé
     */
    @JsonProperty("idUtilisateur")
    public Long getIdUtilisateur() {
        return utilisateur != null ? utilisateur.getId() : null;
    }

    // ========== MÉTHODES MÉTIER ==========

    /**
     * Effectuer un paiement.
     * Implémente la fonctionnalité F10 du cahier des charges.
     *
     * Configure la transaction avec les montants calculés et marque
     * le statut comme "COMPLETE" si les paramètres sont valides.
     *
     * @param idUtilisateur L'identifiant de l'utilisateur (obligatoire)
     * @param montant Le montant de base hors taxes (obligatoire, > 0)
     * @return true si le paiement a été configuré avec succès, false sinon
     * @see "Cahier des charges - F10: Effectuer des paiements via Stripe"
     */
    public boolean effectuerPaiement(Long idUtilisateur, Double montant) {
        if (idUtilisateur != null && montant != null && montant > 0) {
            this.montantAbonnement = montant;
            this.montantHT = montant;
            this.tva = montant * 0.21; // TVA belge 21%
            this.montantTTC = montantHT + tva;
            this.statut = "COMPLETE";
            this.dateCreation = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * Modifier le statut de la transaction.
     * Permet de mettre à jour l'état d'une transaction existante.
     *
     * Statuts possibles : EN_ATTENTE, COMPLETE, ECHEC, REMBOURSE
     *
     * @param id L'identifiant de la transaction (pour validation)
     * @param nouveauStatut Le nouveau statut (obligatoire)
     */
    public void modificationStatut(Long id, String nouveauStatut) {
        if (id != null && nouveauStatut != null && !nouveauStatut.trim().isEmpty()) {
            this.id = id;
            this.statut = nouveauStatut.toUpperCase();
        }
    }

    /**
     * Calcule le montant total TTC.
     * Utilisé pour vérifier la cohérence des calculs.
     *
     * @return Le montant TTC calculé (HT + TVA)
     */
    public Double calculerMontantTTC() {
        if (montantHT != null && tva != null) {
            return montantHT + tva;
        }
        return 0.0;
    }

    /**
     * Vérifie si la transaction est complète.
     * Utilisé pour valider l'état avant génération de facture.
     *
     * @return true si le statut est "COMPLETE"
     */
    public boolean estComplete() {
        return "COMPLETE".equals(this.statut);
    }

    /**
     * Vérifie si la transaction peut être remboursée.
     *
     * @return true si le statut permet un remboursement
     */
    public boolean peutEtreRemboursee() {
        return "COMPLETE".equals(this.statut) || "ECHEC".equals(this.statut);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Représentation textuelle de la transaction.
     * Utilisée pour le débogage et les logs.
     *
     * @return String contenant les informations principales
     */
    @Override
    public String toString() {
        return String.format("Transaction{id=%d, montantTTC=%.2f, statut='%s', utilisateur=%s}",
                id, montantTTC, statut,
                utilisateur != null ? utilisateur.getEmail() : "N/A");
    }

    /**
     * Comparaison d'égalité basée sur l'ID.
     * Deux transactions sont égales si elles ont le même ID.
     *
     * @param obj Objet à comparer
     * @return true si les transactions sont identiques
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Transaction that = (Transaction) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    /**
     * Code de hachage basé sur l'ID.
     * Utilisé dans les collections (HashMap, HashSet).
     *
     * @return Hash code de la transaction
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}