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
    private Long idCreateur;
    private String statut;
    private LocalDateTime dateCreation;
    private Boolean publique;
}