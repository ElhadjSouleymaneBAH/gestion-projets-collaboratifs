package be.iccbxl.gestionprojets.dto;

import be.iccbxl.gestionprojets.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO pour les réponses Utilisateur
 */
@Getter
@Setter
@NoArgsConstructor
public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private String langue;
    private LocalDateTime dateInscription;
}
