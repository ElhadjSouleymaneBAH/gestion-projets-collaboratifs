package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.enums.StatusTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des tâches
 */
@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {

    List<Tache> findByIdProjet(Long idProjet);
    List<Tache> findByIdAssigne(Long idAssigne);

    /**
     * Trouve les tâches par statut
     * Nécessaire pour filtrer selon enum StatusTache
     */
    List<Tache> findByStatut(StatusTache statut);
    List<Tache> findByIdProjetAndStatut(Long idProjet, StatusTache statut);
}