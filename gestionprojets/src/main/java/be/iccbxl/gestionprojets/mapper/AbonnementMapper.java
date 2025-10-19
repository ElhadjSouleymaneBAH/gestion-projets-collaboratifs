package be.iccbxl.gestionprojets.mapper;

import be.iccbxl.gestionprojets.dto.AbonnementDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.model.Utilisateur;

import java.time.LocalDate;

/**
 * Mapper pour convertir Abonnement ↔ AbonnementDTO.
 * Support F10 : Paiements et abonnements
 *
 * @author ElhadjSouleymaneBAH
 */
public class AbonnementMapper {

    /**
     * Convertit une entité Abonnement vers un DTO.
     * Charge dynamiquement les informations utilisateur pour éviter "Inconnu".
     */
    public static AbonnementDTO toDTO(Abonnement abonnement) {
        if (abonnement == null) {
            return null;
        }

        AbonnementDTO dto = new AbonnementDTO();

        // Informations de base
        dto.setId(abonnement.getId());
        dto.setNom(abonnement.getNom());
        dto.setPrix(abonnement.getPrix());
        dto.setDuree(abonnement.getDuree());
        dto.setType(abonnement.getType());
        dto.setStatut(abonnement.getStatut());
        dto.setDateDebut(abonnement.getDateDebut());
        dto.setDateFin(abonnement.getDateFin());

        // Calcul de l'état actif
        dto.setEstActif(calculerEstActif(abonnement));

        // Conversion utilisateur
        Utilisateur utilisateur = abonnement.getUtilisateur();
        if (utilisateur != null) {
            dto.setUtilisateurId(utilisateur.getId());
            dto.setUtilisateurEmail(utilisateur.getEmail());
            dto.setUtilisateur(convertirUtilisateurDTO(utilisateur));
        }

        return dto;
    }

    /**
     * Méthode utilitaire pour calculer si l'abonnement est actif.
     */
    private static boolean calculerEstActif(Abonnement abonnement) {
        return abonnement.getStatut() != null
                && abonnement.getStatut().name().equals("ACTIF")
                && abonnement.getDateFin() != null
                && !abonnement.getDateFin().isBefore(LocalDate.now());
    }

    /**
     * Méthode utilitaire pour convertir Utilisateur en UtilisateurDTO.
     */
    private static UtilisateurDTO convertirUtilisateurDTO(Utilisateur utilisateur) {
        return new UtilisateurDTO(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole()
        );
    }
}