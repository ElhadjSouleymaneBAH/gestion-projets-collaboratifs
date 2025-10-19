package be.iccbxl.gestionprojets.dto;

import be.iccbxl.gestionprojets.enums.StatutAbonnement;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO pour les abonnements utilisateur.
 * Utilisé pour les échanges API sans exposer l'entité complète.

 * Support F10 : Paiements et abonnements
 *
 * @author ElhadjSouleymaneBAH
 */
@Getter
@Setter
@NoArgsConstructor
public class AbonnementDTO {

    /**
     * Identifiant de l'abonnement (lecture seule).
     */
    private Long id;

    /**
     * Nom de l'abonnement.
     * Obligatoire pour la création.
     */
    @NotBlank(message = "Le nom de l'abonnement est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    /**
     * Prix mensuel en euros.
     * Doit être positif selon le business plan.
     */
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être positif")
    @DecimalMax(value = "1000.00", message = "Le prix ne peut pas dépasser 1000€")
    private Double prix;

    /**
     * Durée en mois.
     * Généralement 1 pour mensuel selon le cahier des charges.
     */
    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée minimum est de 1 mois")
    @Max(value = 12, message = "La durée maximum est de 12 mois")
    private Integer duree;

    /**
     * Type d'abonnement.
     * Valeurs autorisées selon le dictionnaire de données.
     */
    @NotBlank(message = "Le type d'abonnement est obligatoire")
    @Pattern(regexp = "^(gratuit|premium|entreprise)$",
            message = "Le type doit être : gratuit, premium ou entreprise")
    private String type = "premium"; // défaut selon business plan

    /**
     * Statut actuel de l'abonnement.
     */
    private StatutAbonnement statut;

    /**
     * Date de début de l'abonnement.
     */
    @JsonProperty("date_debut")
    private LocalDate dateDebut;

    /**
     * Date de fin de l'abonnement.
     */
    @JsonProperty("date_fin")
    private LocalDate dateFin;

    /**
     * Indique si l'abonnement est actuellement actif.
     * Calculé côté serveur.
     */
    @JsonProperty("est_actif")
    private boolean estActif;

    /**
     * ID de l'utilisateur propriétaire.
     * Utilisé pour les liens sans exposer l'entité complète.
     */
    @JsonProperty("utilisateur_id")
    private Long utilisateurId;

    /**
     * Email de l'utilisateur (pour affichage).
     * Information non sensible pour l'administration.
     */
    @JsonProperty("utilisateur_email")
    private String utilisateurEmail;

    /**
     * Objet utilisateur complet
     * Le frontend attend cet objet avec id, nom, prenom, email, role
     * Évite les "Inconnu" en chargeant dynamiquement depuis la base
     */
    private UtilisateurDTO utilisateur;

    // CONSTRUCTEURS

    /**
     * Constructeur pour création d'abonnement simple.
     * Utilisé lors de la souscription F10.
     */
    public AbonnementDTO(String nom, Double prix, Integer duree) {
        this.nom = nom;
        this.prix = prix;
        this.duree = duree;
        this.type = "premium";
    }

    /**
     * Constructeur complet depuis entité.
     * Utilisé pour convertir Abonnement → DTO.
     */
    public AbonnementDTO(Long id, String nom, Double prix, Integer duree,
                         String type, StatutAbonnement statut,
                         LocalDate dateDebut, LocalDate dateFin,
                         boolean estActif, Long utilisateurId,
                         String utilisateurEmail, UtilisateurDTO utilisateur) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.duree = duree;
        this.type = type;
        this.statut = statut;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.estActif = estActif;
        this.utilisateurId = utilisateurId;
        this.utilisateurEmail = utilisateurEmail;
        this.utilisateur = utilisateur;
    }

    // MÉTHODES UTILITAIRES

    /**
     * Calcule le montant TTC affiché (lecture seule).
     * Délègue le calcul à l'entité métier.
     */
    public Double getMontantTTC() {
        if (prix == null) return null;
        double tva = 0.21;
        return prix + (prix * tva);
    }

    /**
     * Vérifie si l'abonnement est en cours de validité.
     * Basé sur le statut et les dates.
     */
    public boolean isValide() {
        return estActif &&
                statut == StatutAbonnement.ACTIF &&
                dateFin != null &&
                !dateFin.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("AbonnementDTO{id=%d, nom='%s', prix=%.2f€, type='%s', statut=%s, estActif=%s, utilisateur=%s}",
                id, nom, prix, type, statut, estActif, utilisateur != null ? utilisateur.toString() : "null");
    }
}