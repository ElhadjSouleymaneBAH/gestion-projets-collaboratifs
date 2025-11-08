package be.iccbxl.gestionprojets.dto;

import be.iccbxl.gestionprojets.enums.PrioriteTache;
import be.iccbxl.gestionprojets.enums.StatutTache;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour les tâches (F7)
 *
 * @author ElhadjSouleymanéBAH
 * @version 2.0 - Ajout support assignation enrichie
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

    // Projet
    private Long idProjet;
    private String nomProjet;

    // Assignation
    private Long idAssigne;
    private String nomAssigne;

    // Détails enrichis de l'assigné
    private String prenomAssigne;
    private String emailAssigne;


}