package be.iccbxl.gestionprojets.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MessageCreateDTO {
    @NotBlank(message = "Le contenu du message est obligatoire")
    private String contenu;

    @NotNull(message = "L'ID du projet est obligatoire")
    private Long projetId;
}