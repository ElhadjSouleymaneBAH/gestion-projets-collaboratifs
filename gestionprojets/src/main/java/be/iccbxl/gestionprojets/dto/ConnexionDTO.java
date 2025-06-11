package be.iccbxl.gestionprojets.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO pour la connexion Utilisateur
 */

@Getter
@Setter
@NoArgsConstructor
public class ConnexionDTO {
    private String email;
    private String motDePasse;
}
