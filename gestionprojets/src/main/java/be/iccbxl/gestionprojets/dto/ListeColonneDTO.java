package be.iccbxl.gestionprojets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour les colonnes Kanban (ListeColonne)
 * Compatible avec le frontend Vue.js
 *
 * @author Elhadj Souleymane BAH
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class ListeColonneDTO {

    private Long id;

    private String nom;

    private Integer position;

    @JsonProperty("dateCreation")
    private LocalDateTime dateCreation;

    @JsonProperty("idProjet")
    private Long idProjet;

    @JsonProperty("nomProjet")
    private String nomProjet;

    @JsonProperty("taches")
    private List<TacheDTO> taches = new ArrayList<>();

    @JsonProperty("nombreTaches")
    private Integer nombreTaches;

    public ListeColonneDTO(Long id, String nom, Integer position, Long idProjet) {
        this.id = id;
        this.nom = nom;
        this.position = position;
        this.idProjet = idProjet;
        this.nombreTaches = 0;
    }
}
