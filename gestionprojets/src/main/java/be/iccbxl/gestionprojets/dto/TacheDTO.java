package be.iccbxl.gestionprojets.dto;

import be.iccbxl.gestionprojets.enums.PrioriteTache;
import be.iccbxl.gestionprojets.enums.StatutTache;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour les tâches (F7)
 *
 * @author Elhadj Souleymane BAH
 * @version 1.1 - Ajout idListeColonne pour Kanban
 */
@Data
public class TacheDTO {

    private Long id;
    private String titre;
    private String description;
    private StatutTache statut;
    private PrioriteTache priorite;

    @JsonProperty("dateEcheance")
    private LocalDate dateEcheance;

    @JsonProperty("dateCreation")
    private LocalDateTime dateCreation;

    @JsonProperty("idProjet")
    private Long idProjet;

    @JsonProperty("nomProjet")
    private String nomProjet;

    @JsonProperty("idAssigne")
    private Long idAssigne;

    @JsonProperty("nomAssigne")
    private String nomAssigne;

    @JsonProperty("prenomAssigne")
    private String prenomAssigne;

    @JsonProperty("emailAssigne")
    private String emailAssigne;

    @JsonProperty("idListeColonne")
    private Long idListeColonne;
}