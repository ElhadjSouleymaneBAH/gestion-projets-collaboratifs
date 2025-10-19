package be.iccbxl.gestionprojets.dto;

import be.iccbxl.gestionprojets.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO pour les informations utilisateur.
 * Utilisé dans les services et pour éviter LAZY loading.
 * Support F1-F10
 *
 * @author ElhadjSouleymaneBAH
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private String langue;
    private LocalDateTime dateInscription;

    // Constructeur pour AbonnementMapper
    public UtilisateurDTO(Long id, String nom, String prenom, String email, Role role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", prenom, nom, email);
    }
}