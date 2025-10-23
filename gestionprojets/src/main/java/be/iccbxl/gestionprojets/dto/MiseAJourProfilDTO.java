package be.iccbxl.gestionprojets.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MiseAJourProfilDTO {
    @Size(max = 100)
    private String nom;

    @Size(max = 100)
    private String prenom;

    @Size(max = 255)
    private String adresse;

    @Size(max = 5)
    private String langue;   // "fr", "en",
}
