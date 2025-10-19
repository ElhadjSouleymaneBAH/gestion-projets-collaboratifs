package be.iccbxl.gestionprojets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour exposer les factures au frontend.
 * Support F11 : Générer les factures.
 * Utilisé par les Chefs de Projet et Administrateurs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureDTO {
    private Long id;
    private String numeroFacture;
    private Double montantHT;
    private Double tva;
    private Double montantTTC;
    private LocalDate dateEmission;
    private LocalDate dateEcheance;
    private String statut;
    private String periode;
    private Long transactionId;
    private Long utilisateurId;

    /**
     * Contient id, nom, prenom, email, role
     */
    private UtilisateurDTO utilisateur;
}