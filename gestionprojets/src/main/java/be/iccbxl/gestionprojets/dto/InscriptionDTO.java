package be.iccbxl.gestionprojets.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO pour l'inscription utilisateur
 */
@Getter
@Setter
@NoArgsConstructor
public class InscriptionDTO {

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String langue = "fr";
    private boolean cguAccepte;
}