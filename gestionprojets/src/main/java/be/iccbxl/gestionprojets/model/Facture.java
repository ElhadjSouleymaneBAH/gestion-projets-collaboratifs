package be.iccbxl.gestionprojets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Entité Facture selon le diagramme de classes
 */
@Entity
@Table(name = "factures")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_facture", nullable = false, unique = true)
    private String numeroFacture;

    @Column(name = "montant_ht", nullable = false)
    private Double montantHT;

    @Column(nullable = false)
    private Double tva;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    @Column(nullable = false)
    private String statut;

    // Relation avec Transaction
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_transaction", nullable = false)
    @JsonIgnoreProperties({"facture", "utilisateur"})
    private Transaction transaction;

    // --------- Champ non persisté, utile à l'UI (pas de colonne en base) ---------
    @Transient
    private String periode;

    public String getPeriode() {
        if (periode != null) return periode;
        if (dateEmission == null) return null;
        // Exemple: "octobre 2025"
        String mois = dateEmission.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        return Character.toUpperCase(mois.charAt(0)) + mois.substring(1) + " " + dateEmission.getYear();
    }
    // ------------------------------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        if (dateEmission == null) {
            dateEmission = LocalDate.now();
        }
        if (dateEcheance == null) {
            dateEcheance = dateEmission.plusDays(30);
        }
        if (numeroFacture == null) {
            numeroFacture = "FACT-" + System.currentTimeMillis();
        }
    }

    /**
     * Générer une facture
     */
    public boolean genererFacture(Long idTransaction) {
        if (idTransaction != null) {
            this.statut = "GENEREE";
            return true;
        }
        return false;
    }

    /**
     * Modifier le statut
     */
    public void modificationStatut(Long id, String statut) {
        if (id != null && statut != null) {
            this.id = id;
            this.statut = statut;
        }
    }
}