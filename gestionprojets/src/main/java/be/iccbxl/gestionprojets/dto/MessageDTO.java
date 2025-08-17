package be.iccbxl.gestionprojets.dto;

import be.iccbxl.gestionprojets.enums.TypeMessage;
import be.iccbxl.gestionprojets.enums.StatutMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private TypeMessage type;
    private StatutMessage statut;
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurPrenom;
    private Long projetId;
    private String projetTitre;
}
