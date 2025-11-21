package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une colonne Kanban dans un projet.
 *
 * Logique Trello:
 * - Projet → ListeColonne (Colonne) → Tache (Carte)
 * - Chaque projet a par défaut 3 colonnes: "À faire", "En cours", "Terminé"
 * - Les tâches peuvent être déplacées entre colonnes (drag & drop)
 *
 * @author Elhadj Souleymane BAH
 * @version 1.0
 */
@Entity
@Table(name = "listes_colonnes")
@Getter
@Setter
@NoArgsConstructor
public class ListeColonne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false)
    private Integer position = 0;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // ========== RELATIONS JPA ==========

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_projet", nullable = false)
    private Projet projet;

    @OneToMany(mappedBy = "listeColonne", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<Tache> taches = new ArrayList<>();

    // ========== CONSTRUCTEURS ==========

    public ListeColonne(String nom, Integer position, Projet projet) {
        this.nom = nom;
        this.position = position;
        this.projet = projet;
        this.dateCreation = LocalDateTime.now();
    }

    // ========== MÉTHODES DU CYCLE DE VIE JPA ==========

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (position == null) {
            position = 0;
        }
    }

    // ========== MÉTHODES MÉTIER ==========

    public void ajouterTache(Tache tache) {
        if (tache == null) {
            throw new IllegalArgumentException("La tâche ne peut pas être null");
        }

        int nouvellePosition = taches.size();
        tache.setPosition(nouvellePosition);

        taches.add(tache);
        tache.setListeColonne(this);
    }

    public void retirerTache(Tache tache) {
        if (tache != null) {
            taches.remove(tache);
            tache.setListeColonne(null);
            reorganiserPositions();
        }
    }

    private void reorganiserPositions() {
        for (int i = 0; i < taches.size(); i++) {
            taches.get(i).setPosition(i);
        }
    }

    public void deplacerTache(Tache tache, int nouvellePosition) {
        if (tache == null || !taches.contains(tache)) {
            throw new IllegalArgumentException("La tâche n'appartient pas à cette colonne");
        }

        if (nouvellePosition < 0 || nouvellePosition >= taches.size()) {
            throw new IllegalArgumentException("Position invalide");
        }

        int anciennePosition = taches.indexOf(tache);

        if (anciennePosition == nouvellePosition) {
            return;
        }

        taches.remove(anciennePosition);
        taches.add(nouvellePosition, tache);
        reorganiserPositions();
    }

    public int getNombreTaches() {
        return taches != null ? taches.size() : 0;
    }

    public boolean estVide() {
        return taches == null || taches.isEmpty();
    }

    // ========== MÉTHODES UTILITAIRES ==========

    @Override
    public String toString() {
        return String.format(
                "ListeColonne{id=%d, nom='%s', position=%d, nombreTaches=%d, projetId=%d}",
                id, nom, position, getNombreTaches(),
                projet != null ? projet.getId() : null
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ListeColonne)) return false;

        ListeColonne other = (ListeColonne) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}