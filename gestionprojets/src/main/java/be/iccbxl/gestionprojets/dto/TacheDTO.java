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
 * @version 3.0 - Ajout mapping Jackson pour compatibilité SQL
 */
@Data
public class TacheDTO {

    private Long id;
    private String titre;
    private String description;
    private StatutTache statut;
    private PrioriteTache priorite;

    // 🔧 Mapping vers date_echeance en SQL
    @JsonProperty("dateEcheance")
    private LocalDate dateEcheance;

    // 🔧 Mapping vers date_creation en SQL
    @JsonProperty("dateCreation")
    private LocalDateTime dateCreation;


    @JsonProperty("idProjet")
    private Long idProjet;

    @JsonProperty("nomProjet")
    private String nomProjet;

    // Assignation

    @JsonProperty("idAssigne")
    private Long idAssigne;

    @JsonProperty("nomAssigne")
    private String nomAssigne;

    // Détails enrichis de l'assigné
    @JsonProperty("prenomAssigne")
    private String prenomAssigne;

    @JsonProperty("emailAssigne")
    private String emailAssigne;
}