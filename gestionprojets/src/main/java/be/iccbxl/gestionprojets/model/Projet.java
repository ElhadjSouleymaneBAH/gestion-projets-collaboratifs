package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projets")
@Getter
@Setter
@NoArgsConstructor
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_createur", nullable = false)
    private Utilisateur createur;

    @Column(nullable = false)
    private String statut = "ACTIF";

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // ✅ CORRECTION: champ manquant pour F3 (projets publics)
    @Column(name = "publique", nullable = false)
    private Boolean publique = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "projet_utilisateurs",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tache> taches = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (publique == null) {
            publique = false;
        }
    }

    public void ajouterMembre(Utilisateur utilisateur) {
        if (utilisateur == null) throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        membres.add(utilisateur);
        utilisateur.getProjets().add(this);
    }

    public void retirerMembre(Utilisateur utilisateur) {
        if (utilisateur != null) {
            membres.remove(utilisateur);
            utilisateur.getProjets().remove(this);
        }
    }

    public boolean ajouterProjet(String titre, String description) {
        if (titre != null && !titre.trim().isEmpty()) {
            this.titre = titre;
            this.description = description;
            return true;
        }
        return false;
    }

    public boolean modifierProjet(Long id, String titre, String description) {
        if (id != null && titre != null && !titre.trim().isEmpty()) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            return true;
        }
        return false;
    }

    public boolean supprimerProjet(Long id) {
        return id != null;
    }

    /** ✅ Utilise le flag publique (F3) */
    public boolean estPublic() {
        return Boolean.TRUE.equals(this.publique);
    }

    public boolean contientMembre(Utilisateur utilisateur) {
        return utilisateur != null && membres.contains(utilisateur);
    }

    public long getNombreTachesActives() {
        return taches.stream()
                .filter(t -> !"TERMINE".equals(t.getStatut().toString()))
                .count();
    }

    @Transient
    public Long getIdCreateur() {
        return (createur != null) ? createur.getId() : null;
    }

    @Override
    public String toString() {
        return String.format(
                "Projet{id=%d, titre='%s', statut='%s', publique=%s, createurId=%s, membres=%d, taches=%d}",
                id, titre, statut, publique, getIdCreateur(), membres.size(), taches.size()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Projet that)) return false;
        if (id != null && that.id != null) return id.equals(that.id);
        Long c1 = (createur != null) ? createur.getId() : null;
        Long c2 = (that.createur != null) ? that.createur.getId() : null;
        return titre != null ? titre.equals(that.titre) && (c1 != null ? c1.equals(c2) : c2 == null)
                : that.titre == null;
    }

    @Override
    public int hashCode() {
        if (id != null) return id.hashCode();
        int result = (titre != null) ? titre.hashCode() : 0;
        result = 31 * result + ((createur != null && createur.getId() != null) ? createur.getId().hashCode() : 0);
        return result;
    }
}
