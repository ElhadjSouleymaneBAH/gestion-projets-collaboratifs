package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.enums.StatusTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {

    // Adaptées aux relations JPA
    List<Tache> findByProjetId(Long projetId);

    List<Tache> findByAssigneAId(Long assigneId);

    List<Tache> findByStatut(StatusTache statut);

    List<Tache> findByProjetIdAndStatut(Long projetId, StatusTache statut);

    List<Tache> findByAssigneAIdAndStatut(Long assigneId, StatusTache statut);

    List<Tache> findByAssigneAIsNull();

    Long countByProjetId(Long projetId);
    
    Long countByAssigneAId(Long assigneId);
}