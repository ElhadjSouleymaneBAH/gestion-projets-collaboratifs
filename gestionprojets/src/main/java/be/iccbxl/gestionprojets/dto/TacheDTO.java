package be.iccbxl.gestionprojets.dto;

import be.iccbxl.gestionprojets.enums.StatusTache;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO pour les tâches
 */
@Getter
@Setter
@NoArgsConstructor
public class TacheDTO {

    private Long id;
    private String titre;
    private String description;
    private Long idProjet;
    private Long idAssigne;
    private StatusTache statut;
    private LocalDateTime dateCreation;
}
