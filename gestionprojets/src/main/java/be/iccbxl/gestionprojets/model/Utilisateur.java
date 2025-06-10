package be.iccbxl.gestionprojets.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String langue = "fr";

    @Column(name = "cgu_accepte", nullable = false)
    private boolean cguAccepte;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    @PrePersist
    protected void onCreate() {
        if (dateInscription == null) {
            dateInscription = LocalDateTime.now();
        }
    }
}