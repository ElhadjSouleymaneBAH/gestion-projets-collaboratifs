package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.ProjetUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetUtilisateurRepository extends JpaRepository<ProjetUtilisateur, Long> {

    // ========== MÉTHODES DE VÉRIFICATION F8 ==========

    boolean existsByProjetIdAndUtilisateurId(Long projetId, Long utilisateurId);
    boolean existsByProjetIdAndUtilisateurIdAndActif(Long projetId, Long utilisateurId, Boolean actif);

    // ========== MÉTHODES DE RECHERCHE F8 ==========

    @Query("SELECT pu.utilisateurId FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.actif = true")
    List<Long> findUtilisateurIdsByProjetId(@Param("projetId") Long projetId);

    @Query("SELECT pu.projetId FROM ProjetUtilisateur pu WHERE pu.utilisateurId = :utilisateurId AND pu.actif = true")
    List<Long> findProjetIdsByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    ProjetUtilisateur findByProjetIdAndUtilisateurId(Long projetId, Long utilisateurId);
    List<ProjetUtilisateur> findByProjetIdAndActif(Long projetId, Boolean actif);
    List<ProjetUtilisateur> findByUtilisateurIdAndActif(Long utilisateurId, Boolean actif);

    // ========== MÉTHODES DE SUPPRESSION F8 - CORRIGÉES ==========

    /**
     * Supprime un membre d'un projet.
     * CORRECTION: Utilise @Modifying pour éviter les conflits Hibernate
     */
    @Modifying
    @Query("DELETE FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.utilisateurId = :utilisateurId")
    void deleteByProjetIdAndUtilisateurId(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

    /**
     * Supprime tous les membres d'un projet.
     * CORRECTION: Utilise @Modifying pour éviter les conflits Hibernate lors de suppression de projet
     */
    @Modifying
    @Query("DELETE FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId")
    void deleteByProjetId(@Param("projetId") Long projetId);

    /**
     * Supprime toutes les appartenances d'un utilisateur.
     * CORRECTION: Utilise @Modifying pour cohérence
     */
    @Modifying
    @Query("DELETE FROM ProjetUtilisateur pu WHERE pu.utilisateurId = :utilisateurId")
    void deleteByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    // ========== MÉTHODES DE COMPTAGE F8 ==========

    long countByProjetIdAndActif(Long projetId, Boolean actif);
    long countByUtilisateurIdAndActif(Long utilisateurId, Boolean actif);

    // ========== MÉTHODES AVANCÉES F8/F9 ==========

    @Query("SELECT pu FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.role = :role AND pu.actif = true")
    List<ProjetUtilisateur> findByProjetIdAndRoleAndActif(@Param("projetId") Long projetId,
                                                          @Param("role") String role,
                                                          Boolean actif);

    @Query("SELECT pu FROM ProjetUtilisateur pu WHERE pu.utilisateurId = :utilisateurId AND pu.actif = true ORDER BY pu.dateAjout DESC")
    List<ProjetUtilisateur> findRecentProjetsByUtilisateur(@Param("utilisateurId") Long utilisateurId);

    @Query("SELECT CASE WHEN COUNT(pu) > 0 THEN true ELSE false END FROM ProjetUtilisateur pu WHERE pu.projetId = :projetId AND pu.utilisateurId = :utilisateurId AND pu.role IN ('ADMIN', 'CREATEUR') AND pu.actif = true")
    boolean canAdministerProjet(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

    // ========== MÉTHODES DE RECHERCHE AVANCÉE ==========

    @Query("SELECT pu.projetId, COUNT(pu) as memberCount FROM ProjetUtilisateur pu WHERE pu.actif = true GROUP BY pu.projetId")
    List<Object[]> findProjetsWithMemberCount();

    @Query("SELECT pu.utilisateurId, COUNT(pu) as projectCount FROM ProjetUtilisateur pu WHERE pu.actif = true GROUP BY pu.utilisateurId HAVING COUNT(pu) > 1")
    List<Object[]> findActiveCollaborators();
}