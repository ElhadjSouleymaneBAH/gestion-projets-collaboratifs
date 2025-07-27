package be.iccbxl.gestionprojets.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO pour l'inscription utilisateur
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class InscriptionDTO {

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String langue = "fr";
    private boolean cguAccepte;
}