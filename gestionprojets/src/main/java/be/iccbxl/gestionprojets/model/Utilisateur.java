package be.iccbxl.gestionprojets.model;

import be.iccbxl.gestionprojets.enums.Role;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBRE;

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

    public void activerCompte() {

    }

    public boolean changerMotDePasse(String nouveauMotDePasse) {
        if (nouveauMotDePasse != null && !nouveauMotDePasse.trim().isEmpty()) {
            this.motDePasse = nouveauMotDePasse;
            return true;
        }
        return false;
    }
}