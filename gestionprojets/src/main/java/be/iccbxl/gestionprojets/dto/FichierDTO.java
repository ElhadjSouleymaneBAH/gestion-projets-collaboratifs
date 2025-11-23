package be.iccbxl.gestionprojets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FichierDTO {

    private Long id;
    private String nom;
    private String nomOriginal;
    private String typeMime;
    private Long taille;
    private String tailleLisible;
    private Long projetId;
    private Long uploadParId;
    private String uploadParNom;
    private LocalDateTime dateUpload;

    /**
     * Convertit la taille en bytes vers un format lisible
     */
    public static String formatTaille(Long bytes) {
        if (bytes == null || bytes < 0) {
            return "0 B";
        }

        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}