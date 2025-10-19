package be.iccbxl.gestionprojets.dto;

import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.enums.PrioriteTache;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour les tâches (F7)
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Data
public class TacheDTO {
    private Long id;
    private String titre;
    private String description;
    private StatutTache statut;
    private PrioriteTache priorite;
    private LocalDate dateEcheance;
    private LocalDateTime dateCreation;
    private Long idProjet;
    private Long idAssigne;
}