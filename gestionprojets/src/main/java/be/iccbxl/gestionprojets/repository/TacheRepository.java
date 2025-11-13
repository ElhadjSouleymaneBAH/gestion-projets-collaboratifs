package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.enums.StatutTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour les tâches.
 * Support F7 : Gérer les tâches.
 *
 * @author ElhadjSouleymaneBAH
 */
@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {

    // ========== MÉTHODES DE BASE (sans JOIN FETCH) ==========

    /**
     * Trouve les tâches par projet.
     */
    List<Tache> findByProjetId(Long projetId);

    /**
     * Utilise une @Query pour accéder à assigneA.id
     * Car "assigneAId" n'est pas une propriété directe de Tache
     */
    @Query("SELECT t FROM Tache t WHERE t.assigneA.id = :assigneId")
    List<Tache> findByAssigneAId(@Param("assigneId") Long assigneId);

    /**
     * Trouve les tâches par statut.
     */
    List<Tache> findByStatut(StatutTache statut);

    /**
     * Utilise une @Query pour la combinaison projet + statut
     */
    @Query("SELECT t FROM Tache t WHERE t.projet.id = :projetId AND t.statut = :statut")
    List<Tache> findByProjetIdAndStatut(@Param("projetId") Long projetId, @Param("statut") StatutTache statut);

    /**
     * Utilise une @Query pour assigneA.id + statut
     */
    @Query("SELECT t FROM Tache t WHERE t.assigneA.id = :assigneId AND t.statut = :statut")
    List<Tache> findByAssigneAIdAndStatut(@Param("assigneId") Long assigneId, @Param("statut") StatutTache statut);

    /**
     * Trouve les tâches non assignées.
     */
    List<Tache> findByAssigneAIsNull();

    /**
     * Compte les tâches par projet
     */
    @Query("SELECT COUNT(t) FROM Tache t WHERE t.projet.id = :projetId")
    Long countByProjetId(@Param("projetId") Long projetId);

    /**
     * Compte les tâches par utilisateur assigné
     */
    @Query("SELECT COUNT(t) FROM Tache t WHERE t.assigneA.id = :assigneId")
    Long countByAssigneAId(@Param("assigneId") Long assigneId);

    // ========== NOUVELLES MÉTHODES AVEC JOIN FETCH ==========

    /**
     * Trouve une tâche par ID AVEC toutes ses relations chargées
     * Évite LazyInitializationException lors de la conversion en DTO
     */
    @Query("SELECT t FROM Tache t " +
            "LEFT JOIN FETCH t.projet p " +
            "LEFT JOIN FETCH p.createur " +
            "LEFT JOIN FETCH t.assigneA " +
            "WHERE t.id = :id")
    Optional<Tache> findByIdWithRelations(@Param("id") Long id);

    /**
     * Trouve les tâches par projet AVEC toutes les relations chargées
     */
    @Query("SELECT t FROM Tache t " +
            "LEFT JOIN FETCH t.projet p " +
            "LEFT JOIN FETCH p.createur " +
            "LEFT JOIN FETCH t.assigneA " +
            "WHERE t.projet.id = :projetId")
    List<Tache> findByProjetIdWithRelations(@Param("projetId") Long projetId);

    /**
     * Trouve les tâches assignées à un utilisateur AVEC toutes les relations
     * À UTILISER pour la conversion en DTO dans les contrôleurs
     */
    @Query("SELECT t FROM Tache t " +
            "LEFT JOIN FETCH t.projet p " +
            "LEFT JOIN FETCH p.createur " +
            "LEFT JOIN FETCH t.assigneA " +
            "WHERE t.assigneA.id = :assigneId")
    List<Tache> findByAssigneAIdWithRelations(@Param("assigneId") Long assigneId);

    // ========== MÉTHODE POUR UtilisateurService (F7) ==========

    /**
     * Vérifie s'il existe au moins une tâche "active" assignée à l'utilisateur.
     * Les états actifs sont passés en paramètre (évite les enums écrites en dur).
     */
    @Query("SELECT (COUNT(t) > 0) FROM Tache t " +
            "WHERE t.assigneA.id = :assigneId " +
            "AND t.statut IN :actifs")
    boolean existsByAssigneAIdAndTachesActives(@Param("assigneId") Long assigneId,
                                               @Param("actifs") List<StatutTache> actifs);

    /**
     * Trouve les tâches par liste d'IDs de projets (utilisé pour les chefs de projet)
     */
    @Query("SELECT t FROM Tache t " +
            "LEFT JOIN FETCH t.projet p " +
            "LEFT JOIN FETCH p.createur " +
            "LEFT JOIN FETCH t.assigneA " +
            "WHERE t.projet.id IN :idsProjets")
    List<Tache> findByProjetIdInWithRelations(@Param("idsProjets") List<Long> idsProjets);

    /**
     * Tâches non assignées pour un projet donné.
     * Utilisée dans la fenêtre d’assignation de tâche.
     */
    @Query("SELECT t FROM Tache t " +
            "LEFT JOIN FETCH t.projet p " +
            "LEFT JOIN FETCH p.createur " +
            "WHERE t.projet.id = :projetId AND t.assigneA IS NULL")
    List<Tache> findNonAssigneesByProjetId(@Param("projetId") Long projetId);
}
