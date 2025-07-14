package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité de liaison entre Projet et Utilisateur
 * Correspond à la table projet_membres dans votre base de données
 *
 * Implémente la fonctionnalité F8 du cahier des charges :
 * "Ajouter des membres à un projet"
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Entity
@Table(name = "projet_utilisateurs")
@Getter
@Setter
@NoArgsConstructor
public class ProjetUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout;

    @Column(name = "actif")
    private Boolean actif;

    /**
     * Constructeur pour créer une nouvelle association membre-projet
     */
    public ProjetUtilisateur(Long projetId, Long utilisateurId, String role) {
        this.projetId = projetId;
        this.utilisateurId = utilisateurId;
        this.role = role != null ? role : "MEMBRE";
        this.dateAjout = LocalDateTime.now();
        this.actif = true;
    }

    /**
     * Méthode automatique avant la persistance
     */
    @PrePersist
    protected void onCreate() {
        if (dateAjout == null) {
            dateAjout = LocalDateTime.now();
        }
        if (actif == null) {
            actif = true;
        }
        if (role == null) {
            role = "MEMBRE";
        }
    }
}
