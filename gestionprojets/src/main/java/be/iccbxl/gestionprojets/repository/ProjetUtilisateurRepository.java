package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.ProjetUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des associations Projet-Utilisateur.
 * Fonctionnalités cahier des charges :
 * - F8 : Ajouter des membres à un projet (Chef de Projet, Importance 4/5)
 * - F9 : Collaboration temps réel (vérifications accès)

 * Contraintes F8 : Membres existants (éviter doublons)
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Repository
public interface ProjetUtilisateurRepository extends JpaRepository<ProjetUtilisateur, Long> {

    // ========== MÉTHODES DE VÉRIFICATION F8 ==========

    /**
     * Vérifie si un utilisateur est membre d'un projet.
     * Utilisé pour F8 : validation avant ajout/suppression
     */
    boolean existsByProjetIdAndUtilisateurId(Long projetId, Long utilisateurId);

    /**
     * Vérifie si un utilisateur est membre actif d'un projet.
     * Utilisé pour F9 : contrôle d'accès collaboration temps réel
     */
    boolean existsByProjetIdAndUtilisateurIdAndActif(Long projetId, Long utilisateurId, Boolean actif);

    // ========== MÉTHODES DE RECHERCHE F8 ==========

    /**
     * Trouve tous les utilisateurs membres d'un projet.
     * Utilisé pour F8 : lister les membres d'un projet
     */
    @Query("SELECT pu.utilisateurId FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.actif = true")
    List<Long> findUtilisateurIdsByProjetId(@Param("projetId") Long projetId);

    /**
     * Trouve tous les projets où un utilisateur est membre.
     * Utilisé pour F6/F8 : projets de l'utilisateur
     */
    @Query("SELECT pu.projetId FROM ProjetUtilisateur pu WHERE pu.utilisateurId = :utilisateurId AND pu.actif = true")
    List<Long> findProjetIdsByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    /**
     * Trouve l'association spécifique projet-utilisateur.
     * Utilisé pour modifications de rôle ou statut
     */
    ProjetUtilisateur findByProjetIdAndUtilisateurId(Long projetId, Long utilisateurId);

    /**
     * Trouve tous les membres actifs d'un projet avec leurs détails.
     * Alternative pour récupération complète
     */
    List<ProjetUtilisateur> findByProjetIdAndActif(Long projetId, Boolean actif);

    /**
     * Trouve tous les projets actifs d'un utilisateur avec leurs détails.
     * Alternative pour récupération complète
     */
    List<ProjetUtilisateur> findByUtilisateurIdAndActif(Long utilisateurId, Boolean actif);

    // ========== MÉTHODES DE SUPPRESSION F8 ==========

    /**
     * Supprime un membre d'un projet.
     * Utilisé pour F8 : retirer membre du projet
     */
    void deleteByProjetIdAndUtilisateurId(Long projetId, Long utilisateurId);

    /**
     * Supprime tous les membres d'un projet.
     * Utilisé lors de suppression de projet (F6)
     */
    void deleteByProjetId(Long projetId);

    /**
     * Supprime toutes les appartenances d'un utilisateur.
     * Utilisé lors de suppression d'utilisateur (admin)
     */
    void deleteByUtilisateurId(Long utilisateurId);

    // ========== MÉTHODES DE COMPTAGE F8 ==========

    /**
     * Compte le nombre de membres d'un projet.
     * Utilisé pour statistiques et validations
     */
    long countByProjetIdAndActif(Long projetId, Boolean actif);

    /**
     * Compte le nombre de projets d'un utilisateur.
     * Utilisé pour statistiques utilisateur
     */
    long countByUtilisateurIdAndActif(Long utilisateurId, Boolean actif);

    // ========== MÉTHODES AVANCÉES F8/F9 ==========

    /**
     * Trouve les membres par rôle dans un projet.
     * Utile pour différencier les types de membres
     */
    @Query("SELECT pu FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.role = :role AND pu.actif = true")
    List<ProjetUtilisateur> findByProjetIdAndRoleAndActif(@Param("projetId") Long projetId,
                                                          @Param("role") String role,
                                                          Boolean actif);

    /**
     * Trouve les projets récents d'un utilisateur.
     * Utile pour tableau de bord
     */
    @Query("SELECT pu FROM ProjetUtilisateur pu WHERE pu.utilisateurId = :utilisateurId AND pu.actif = true ORDER BY pu.dateAjout DESC")
    List<ProjetUtilisateur> findRecentProjetsByUtilisateur(@Param("utilisateurId") Long utilisateurId);

    /**
     * Vérifie si un utilisateur peut administrer un projet.
     * (créateur ou admin du projet)
     */
    @Query("SELECT CASE WHEN COUNT(pu) > 0 THEN true ELSE false END FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.utilisateurId = :utilisateurId AND pu.role IN ('ADMIN', 'CREATEUR') AND pu.actif = true")
    boolean canAdministerProjet(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

    // ========== MÉTHODES DE RECHERCHE AVANCÉE ==========

    /**
     * Trouve tous les projets publics avec leurs statistiques de membres.
     * Utile pour F3 : consultation projets publics
     */
    @Query("SELECT pu.projetId, COUNT(pu) as memberCount FROM ProjetUtilisateur pu WHERE pu.actif = true GROUP BY pu.projetId")
    List<Object[]> findProjetsWithMemberCount();

    /**
     * Trouve les utilisateurs membres de plusieurs projets (collaborateurs actifs).
     * Utile pour suggestions de membres
     */
    @Query("SELECT pu.utilisateurId, COUNT(pu) as projectCount FROM ProjetUtilisateur pu WHERE pu.actif = true GROUP BY pu.utilisateurId HAVING COUNT(pu) > 1")
    List<Object[]> findActiveCollaborators();
}