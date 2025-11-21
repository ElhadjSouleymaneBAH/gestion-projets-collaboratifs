package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.ListeColonne;
import org.springframework.data.jpa.repository.JpaRepository;
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

    List<ListeColonne> findByProjetIdOrderByPositionAsc(Long idProjet);

    Optional<ListeColonne> findByProjetIdAndNom(Long idProjet, String nom);

    long countByProjetId(Long idProjet);

    boolean existsByProjetId(Long idProjet);

    void deleteByProjetId(Long idProjet);

    @Query("SELECT MAX(lc.position) FROM ListeColonne lc WHERE lc.projet.id = :idProjet")
    Integer findMaxPositionByProjetId(@Param("idProjet") Long idProjet);

    @Query("SELECT DISTINCT lc FROM ListeColonne lc " +
            "LEFT JOIN FETCH lc.taches t " +
            "WHERE lc.projet.id = :idProjet " +
            "ORDER BY lc.position ASC, t.position ASC")
    List<ListeColonne> findByProjetIdWithTaches(@Param("idProjet") Long idProjet);
}