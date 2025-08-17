package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.enums.TypeMessage;
import be.iccbxl.gestionprojets.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité Message
 * Support de F9 : Collaboration en temps réel
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Trouver tous les messages d'un projet, triés par date d'envoi
     * Utilisé pour F9 : Afficher l'historique du chat
     */
    @Query("SELECT m FROM Message m WHERE m.projet.id = :projetId ORDER BY m.dateEnvoi ASC")
    List<Message> findByProjetIdOrderByDateEnvoiAsc(@Param("projetId") Long projetId);

    /**
     * Trouver les derniers messages d'un projet
     * Utilisé pour F9 : Chat avec pagination
     */
    @Query("SELECT m FROM Message m WHERE m.projet.id = :projetId ORDER BY m.dateEnvoi DESC")
    List<Message> findByProjetIdOrderByDateEnvoiDesc(@Param("projetId") Long projetId);

    /**
     * Trouver les messages par utilisateur
     * Utilisé pour le profil utilisateur
     */
    List<Message> findByUtilisateurIdOrderByDateEnvoiDesc(Long utilisateurId);

    /**
     * Trouver les messages par type (avec enum)
     * Utilisé pour filtrer les messages système/notifications
     */
    List<Message> findByTypeOrderByDateEnvoiDesc(TypeMessage type);

    /**
     * Trouver les messages non lus d'un projet
     * Utilisé pour F9 : Notifications de messages non lus
     */
    @Query("SELECT m FROM Message m WHERE m.projet.id = :projetId AND m.statut != 'LU' ORDER BY m.dateEnvoi DESC")
    List<Message> findMessagesNonLusByProjet(@Param("projetId") Long projetId);

    /**
     * Trouver les messages récents d'un projet (depuis une date)
     * Utilisé pour F9 : Activité récente
     */
    @Query("SELECT m FROM Message m WHERE m.projet.id = :projetId AND m.dateEnvoi >= :depuis ORDER BY m.dateEnvoi DESC")
    List<Message> findMessagesRecentsByProjet(@Param("projetId") Long projetId, @Param("depuis") LocalDateTime depuis);

    /**
     * Compter les messages d'un projet
     * Statistiques pour l'interface
     */
    Long countByProjetId(Long projetId);

    /**
     * Compter les messages non lus d'un projet pour un utilisateur
     * Utilisé pour les badges de notification
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.projet.id = :projetId AND m.statut != 'LU' AND m.utilisateur.id != :utilisateurId")
    Long countMessagesNonLusByProjetAndNotUtilisateur(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

    /**
     * Trouver les messages par projet et type (avec enum)
     * Utilisé pour filtrer les notifications ou messages système
     */
    @Query("SELECT m FROM Message m WHERE m.projet.id = :projetId AND m.type = :type ORDER BY m.dateEnvoi DESC")
    List<Message> findByProjetIdAndTypeOrderByDateEnvoiDesc(@Param("projetId") Long projetId, @Param("type") TypeMessage type);

    /**
     * Trouver les derniers messages de tous les projets d'un utilisateur
     * Utilisé pour le tableau de bord utilisateur
     */
    @Query("SELECT m FROM Message m WHERE m.projet.id IN :projetIds ORDER BY m.dateEnvoi DESC")
    List<Message> findLatestMessagesByProjetIds(@Param("projetIds") List<Long> projetIds);
}