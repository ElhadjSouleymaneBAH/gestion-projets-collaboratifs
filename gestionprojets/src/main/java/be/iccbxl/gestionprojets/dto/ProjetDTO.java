package be.iccbxl.gestionprojets.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProjetDTO {

    private Long id;
    private String titre;
    private String description;
    private String statut;
    private LocalDateTime dateCreation;
    private Boolean publique;
    private Long idCreateur;
    private String createurNom;
    private String createurPrenom;
    private String createurEmail;
}
