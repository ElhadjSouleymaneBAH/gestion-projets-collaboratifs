package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.ProjetUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository pour l'entité ProjetUtilisateur
 * Gère les associations entre projets et utilisateurs
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Repository
public interface ProjetUtilisateurRepository extends JpaRepository<ProjetUtilisateur, Long> {

    /**
     * Vérifier si un utilisateur est membre d'un projet
     */
    boolean existsByProjetIdAndUtilisateurId(Long projetId, Long utilisateurId);

    /**
     * Obtenir les IDs des utilisateurs membres d'un projet
     */
    @Query("SELECT pu.utilisateurId FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.actif = true")
    List<Long> findUtilisateurIdsByProjetId(@Param("projetId") Long projetId);

    /**
     * Supprimer toutes les associations d'un projet
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId")
    void deleteByProjetId(@Param("projetId") Long projetId);

    /**
     * Supprimer une association spécifique projet-utilisateur
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.utilisateurId = :utilisateurId")
    void deleteByProjetIdAndUtilisateurId(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);
}