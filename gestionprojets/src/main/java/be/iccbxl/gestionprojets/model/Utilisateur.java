package be.iccbxl.gestionprojets.model;

import be.iccbxl.gestionprojets.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité Utilisateur selon le diagramme de classes
 */
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

    // Relations avec les autres entités
    @ManyToMany(mappedBy = "membres", fetch = FetchType.LAZY)
    private Set<Projet> projets = new HashSet<>();

    @OneToMany(mappedBy = "assigneA", fetch = FetchType.LAZY)
    private Set<Tache> tachesAssignees = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Abonnement abonnement;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Commentaire> commentaires = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (dateInscription == null) {
            dateInscription = LocalDateTime.now();
        }
    }

    /**
     * Activer le compte utilisateur
     */
    public void activerCompte() {
        // Logique d'activation
    }

    /**
     * Changer le mot de passe
     */
    public boolean changerMotDePasse(String nouveauMotDePasse) {
        if (nouveauMotDePasse != null && !nouveauMotDePasse.trim().isEmpty()) {
            this.motDePasse = nouveauMotDePasse;
            return true;
        }
        return false;
    }
}