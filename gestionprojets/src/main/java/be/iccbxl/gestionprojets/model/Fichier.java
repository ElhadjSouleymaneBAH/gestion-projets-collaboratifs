package be.iccbxl.gestionprojets.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fichiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(name = "nom_original", nullable = false)
    private String nomOriginal;

    @Column(name = "type_mime")
    private String typeMime;

    @Column(nullable = false)
    private Long taille; // en bytes

    @Column(nullable = false)
    private String chemin;

    @Column(name = "id_projet", nullable = false)
    private Long idProjet;

    @Column(name = "id_upload_par", nullable = false)
    private Long idUploadPar;

    @Column(name = "date_telechargement", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTelechargement;

    @PrePersist
    protected void onCreate() {
        if (dateTelechargement == null) {
            dateTelechargement = LocalDateTime.now();
        }
    }
}