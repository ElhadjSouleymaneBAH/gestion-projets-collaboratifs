package be.iccbxl.gestionprojets.mapper;

import be.iccbxl.gestionprojets.dto.FactureDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.model.Facture;
import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.model.Utilisateur;

/**
 * Mapper Facture ↔ FactureDTO.
 * Support F11 : Générer les factures
 */
public class FactureMapper {

    public static FactureDTO toDTO(Facture facture) {
        if (facture == null) return null;

        Double ht  = facture.getMontantHT() != null ? facture.getMontantHT() : 0d;
        Double tva = facture.getTva() != null ? facture.getTva() : 0d;

        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setNumeroFacture(facture.getNumeroFacture());
        dto.setMontantHT(ht);
        dto.setTva(tva);
        dto.setMontantTTC(ht + tva);
        dto.setDateEmission(facture.getDateEmission());
        dto.setDateEcheance(facture.getDateEcheance());
        dto.setStatut(facture.getStatut());
        dto.setPeriode(facture.getPeriode());

        Transaction tr = facture.getTransaction();
        if (tr != null) {
            dto.setTransactionId(tr.getId());
            Utilisateur u = tr.getUtilisateur();
            if (u != null) {
                dto.setUtilisateurId(u.getId());

                // Convertir utilisateur en DTO complet
                dto.setUtilisateur(new UtilisateurDTO(
                        u.getId(),
                        u.getNom(),
                        u.getPrenom(),
                        u.getEmail(),
                        u.getRole()
                ));
            }
        }

        return dto;
    }

    public static Facture toEntity(FactureDTO dto) {
        if (dto == null) return null;

        Facture f = new Facture();
        f.setId(dto.getId());
        f.setNumeroFacture(dto.getNumeroFacture());
        f.setMontantHT(dto.getMontantHT());
        f.setTva(dto.getTva());
        f.setDateEmission(dto.getDateEmission());
        f.setDateEcheance(dto.getDateEcheance());
        f.setStatut(dto.getStatut());

        return f;
    }
}