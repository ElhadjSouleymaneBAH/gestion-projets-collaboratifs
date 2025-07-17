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
 * Support des fonctionnalités F8 et F9
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Repository
public interface ProjetUtilisateurRepository extends JpaRepository<ProjetUtilisateur, Long> {

    /**
     * Vérifier si un utilisateur est membre d'un projet
     * Utilisé pour F8 : Ajouter des membres à un projet
     */
    boolean existsByProjetIdAndUtilisateurId(Long projetId, Long utilisateurId);

    /**
     * Vérifier si un utilisateur est membre actif d'un projet
     * Utilisé pour F9 : Collaboration en temps réel
     */
    boolean existsByProjetIdAndUtilisateurIdAndActif(Long projetId, Long utilisateurId, Boolean actif);

    /**
     * Obtenir les IDs des utilisateurs membres d'un projet
     * Utilisé pour F8 : Visualiser les membres
     */
    @Query("SELECT pu.utilisateurId FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.actif = true")
    List<Long> findUtilisateurIdsByProjetId(@Param("projetId") Long projetId);

    /**
     * Obtenir les IDs des projets d'un utilisateur
     * Utilisé pour l'interface utilisateur
     */
    @Query("SELECT pu.projetId FROM ProjetUtilisateur pu WHERE pu.utilisateurId = :utilisateurId AND pu.actif = true")
    List<Long> findProjetIdsByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    /**
     * Supprimer toutes les associations d'un projet
     * Utilisé lors de la suppression d'un projet
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId")
    void deleteByProjetId(@Param("projetId") Long projetId);

    /**
     * Supprimer une association spécifique projet-utilisateur
     * Utilisé pour F8 : Retirer des membres
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.utilisateurId = :utilisateurId")
    void deleteByProjetIdAndUtilisateurId(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

    /**
     * Trouver les associations par projet avec détails
     * Utilisé pour l'administration
     */
    List<ProjetUtilisateur> findByProjetIdAndActif(Long projetId, Boolean actif);

    /**
     * Trouver les associations par utilisateur
     * Utilisé pour le profil utilisateur
     */
    List<ProjetUtilisateur> findByUtilisateurIdAndActif(Long utilisateurId, Boolean actif);

    /**
     * Compter les membres actifs d'un projet
     * Statistiques pour l'interface
     */
    @Query("SELECT COUNT(pu) FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.actif = true")
    Long countMembresActifsByProjetId(@Param("projetId") Long projetId);
}