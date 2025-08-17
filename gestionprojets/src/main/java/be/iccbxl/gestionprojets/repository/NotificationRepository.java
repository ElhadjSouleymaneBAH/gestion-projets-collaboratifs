package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Notification;
import be.iccbxl.gestionprojets.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository simple pour les notifications
 * Architecture cohérente avec UtilisateurRepository
 * Conforme au cahier des charges
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Récupérer les notifications d'un utilisateur par ID, triées par date
     * Pattern similaire à UtilisateurRepository.findByEmail()
     */
    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :utilisateurId ORDER BY n.date DESC")
    List<Notification> findByUtilisateurIdOrderByDateDesc(@Param("utilisateurId") Long utilisateurId);

    /**
     * Récupérer toutes les notifications d'un utilisateur, triées par date
     * Pattern direct JPA
     */
    List<Notification> findByUtilisateurOrderByDateDesc(Utilisateur utilisateur);

    /**
     * Récupérer les notifications non lues d'un utilisateur
     * Pattern similaire aux méthodes de filtrage
     */
    List<Notification> findByUtilisateurAndLuFalseOrderByDateDesc(Utilisateur utilisateur);

    /**
     * Compter les notifications non lues pour un utilisateur
     * Pattern similaire à UtilisateurRepository.existsByEmail()
     */
    long countByUtilisateurAndLuFalse(Utilisateur utilisateur);

    /**
     * Vérifier si un utilisateur a des notifications non lues
     * Pattern identique à UtilisateurRepository.existsByEmail()
     */
    boolean existsByUtilisateurAndLuFalse(Utilisateur utilisateur);

    /**
     * Récupérer les notifications récentes pour un utilisateur
     * Méthode corrigée avec paramètre de date pour flexibilité
     */
    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :utilisateurId " +
            "AND n.date >= :dateDebut ORDER BY n.date DESC")
    List<Notification> findRecentByUtilisateurId(@Param("utilisateurId") Long utilisateurId,
                                                 @Param("dateDebut") LocalDateTime dateDebut);

    /**
     * Compter le total des notifications pour un utilisateur
     * Pattern de comptage simple
     */
    long countByUtilisateur(Utilisateur utilisateur);
}