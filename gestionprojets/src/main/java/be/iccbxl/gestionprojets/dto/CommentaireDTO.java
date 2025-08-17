package be.iccbxl.gestionprojets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireDTO {

    private Long id;

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    private String contenu;

    private LocalDateTime date;

    @NotNull(message = "L'ID de la tâche est obligatoire")
    private Long tacheId;

    private Long auteurId;
    private String auteurNom;
    private String auteurPrenom;
    private String auteurEmail;

    // Constructeur pour création
    public CommentaireDTO(String contenu, Long tacheId) {
        this.contenu = contenu;
        this.tacheId = tacheId;
        this.date = LocalDateTime.now();
    }
}