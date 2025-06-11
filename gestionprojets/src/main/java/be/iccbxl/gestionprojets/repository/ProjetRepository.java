package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

    /**
     * Trouve les projets par créateur
     *Gérer les projets (Chef de Projet)
     */
    List<Projet> findByIdCreateur(Long idCreateur);

    /**
     * Trouve les projets par statut
     * Nécessaire pour filtrer les projets actifs/inactifs
     */
    List<Projet> findByStatut(String statut);

    /**
     * Trouve les projets par titre (recherche)
     * Nécessaire pour F3: Consulter les projets publics
     */
    List<Projet> findByTitreContainingIgnoreCase(String titre);
}