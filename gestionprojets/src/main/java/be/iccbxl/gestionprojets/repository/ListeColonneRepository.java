package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.ListeColonne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des colonnes Kanban (ListeColonne)
 *
 * @author Elhadj Souleymane BAH
 * @version 1.0
 */
@Repository
public interface ListeColonneRepository extends JpaRepository<ListeColonne, Long> {

    /**
     * Trouve les colonnes d'un projet triées par position
     */
    List<ListeColonne> findByProjetIdOrderByPositionAsc(Long idProjet);

    /**
     * Trouve une colonne spécifique par projet et nom
     */
    Optional<ListeColonne> findByProjetIdAndNom(Long idProjet, String nom);

    /**
     * Compte le nombre de colonnes d'un projet
     */
    long countByProjetId(Long idProjet);

    /**
     * Vérifie si un projet a des colonnes
     */
    boolean existsByProjetId(Long idProjet);

    /**
     * Trouve la position maximale dans un projet
     */
    @Query("SELECT MAX(lc.position) FROM ListeColonne lc WHERE lc.projet.id = :idProjet")
    Integer findMaxPositionByProjetId(@Param("idProjet") Long idProjet);

    /**
     * Trouve les colonnes d'un projet avec leurs tâches (JOIN FETCH)
     */
    @Query("SELECT DISTINCT lc FROM ListeColonne lc " +
            "LEFT JOIN FETCH lc.taches t " +
            "WHERE lc.projet.id = :idProjet " +
            "ORDER BY lc.position ASC, t.position ASC")
    List<ListeColonne> findByProjetIdWithTaches(@Param("idProjet") Long idProjet);

    /**
     * Supprime toutes les colonnes d'un projet.
     * Utilisé lors de suppression de projet (F6).
     * @Modifying évite les conflits Hibernate en exécutant directement le DELETE en SQL.
     */
    @Modifying
    @Query("DELETE FROM ListeColonne lc WHERE lc.projet.id = :projetId")
    void deleteByProjetId(@Param("projetId") Long projetId);
}