package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité Transaction selon le diagramme de classes
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
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
    private LocalDateTime date;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Facture facture;

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

    /**
     * Effectuer un paiement
     */
    public boolean effectuerPaiement(Long idUtilisateur, Double montant) {
        if (idUtilisateur != null && montant != null && montant > 0) {
            this.montantAbonnement = montant;
            this.montantHT = montant;
            this.tva = montant * 0.21;
            this.montantTTC = montantHT + tva;
            this.statut = "COMPLETE";
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