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

    /** Historique du chat d’un projet (asc) — avec JOIN FETCH pour éviter lazy init */
    @Query("""
           SELECT m
           FROM Message m
           JOIN FETCH m.utilisateur u
           JOIN FETCH m.projet p
           WHERE p.id = :projetId
           ORDER BY m.dateEnvoi ASC
           """)
    List<Message> findByProjetIdOrderByDateEnvoiAsc(@Param("projetId") Long projetId);

    /** Derniers messages d’un projet (desc) — avec JOIN FETCH */
    @Query("""
           SELECT m
           FROM Message m
           JOIN FETCH m.utilisateur u
           JOIN FETCH m.projet p
           WHERE p.id = :projetId
           ORDER BY m.dateEnvoi DESC
           """)
    List<Message> findByProjetIdOrderByDateEnvoiDesc(@Param("projetId") Long projetId);

    /** Messages par utilisateur (desc) */
    @Query("""
           SELECT m
           FROM Message m
           JOIN FETCH m.projet p
           WHERE m.utilisateur.id = :utilisateurId
           ORDER BY m.dateEnvoi DESC
           """)
    List<Message> findByUtilisateurIdOrderByDateEnvoiDesc(@Param("utilisateurId") Long utilisateurId);

    /** Messages par type */
    @Query("""
           SELECT m
           FROM Message m
           JOIN FETCH m.projet p
           JOIN FETCH m.utilisateur u
           WHERE m.type = :type
           ORDER BY m.dateEnvoi DESC
           """)
    List<Message> findByTypeOrderByDateEnvoiDesc(@Param("type") TypeMessage type);

    /** Non lus d’un projet */
    @Query("""
           SELECT m
           FROM Message m
           JOIN FETCH m.utilisateur u
           WHERE m.projet.id = :projetId AND m.statut <> 'LU'
           ORDER BY m.dateEnvoi DESC
           """)
    List<Message> findMessagesNonLusByProjet(@Param("projetId") Long projetId);

    /** Récents d’un projet depuis une date */
    @Query("""
           SELECT m
           FROM Message m
           JOIN FETCH m.utilisateur u
           JOIN FETCH m.projet p
           WHERE p.id = :projetId AND m.dateEnvoi >= :depuis
           ORDER BY m.dateEnvoi DESC
           """)
    List<Message> findMessagesRecentsByProjet(@Param("projetId") Long projetId,
                                              @Param("depuis") LocalDateTime depuis);

    /** Compter messages d’un projet */
    Long countByProjetId(Long projetId);

    /** Compter non lus d’un projet pour un utilisateur */
    @Query("""
           SELECT COUNT(m)
           FROM Message m
           WHERE m.projet.id = :projetId AND m.statut <> 'LU' AND m.utilisateur.id <> :utilisateurId
           """)
    Long countMessagesNonLusByProjetAndNotUtilisateur(@Param("projetId") Long projetId,
                                                      @Param("utilisateurId") Long utilisateurId);

    /** Par projet et type */
    @Query("""
           SELECT m
           FROM Message m
           JOIN FETCH m.utilisateur u
           WHERE m.projet.id = :projetId AND m.type = :type
           ORDER BY m.dateEnvoi DESC
           """)
    List<Message> findByProjetIdAndTypeOrderByDateEnvoiDesc(@Param("projetId") Long projetId,
                                                            @Param("type") TypeMessage type);

    /** Derniers messages sur une liste de projets (dashboard) */
    @Query("""
           SELECT m
           FROM Message m
           WHERE m.projet.id IN :projetIds
           ORDER BY m.dateEnvoi DESC
           """)
    List<Message> findLatestMessagesByProjetIds(@Param("projetIds") List<Long> projetIds);
}
