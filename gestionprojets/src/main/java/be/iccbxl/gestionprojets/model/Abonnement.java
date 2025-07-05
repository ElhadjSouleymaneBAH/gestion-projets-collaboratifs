package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité Abonnement selon le diagramme de classes
 */
@Entity
@Table(name = "abonnements")
@Getter
@Setter
@NoArgsConstructor
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private Double prix;

    @Column(nullable = false)
    private Integer duree;

    // Relation avec Utilisateur
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    /**
     * Calculer le montant TTC
     */
    public Double calculMontantTTC() {
        return prix * 1.21;
    }

    /**
     * Vérifier si l'abonnement est valide
     */
    public boolean estValide() {
        return prix != null && prix > 0 && duree != null && duree > 0;
    }

    /**
     * Renouveler l'abonnement
     */
    public boolean renouveler() {
        if (estValide()) {
            return true;
        }
        return false;
    }
}