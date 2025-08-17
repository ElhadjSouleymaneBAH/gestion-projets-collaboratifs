package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les transactions.
 * Support F10 : Paiements et abonnements.
 *
 * @author ElhadjSouleymaneBAH
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Trouve toutes les transactions d'un utilisateur.
     * Utilise la relation @ManyToOne avec Utilisateur.
     */
    @Query("SELECT t FROM Transaction t WHERE t.utilisateur.id = :utilisateurId ORDER BY t.dateCreation DESC")
    List<Transaction> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    /**
     * Compte les transactions réussies d'un utilisateur.
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.utilisateur.id = :utilisateurId AND t.statut = :statut")
    long countByUtilisateurIdAndStatut(@Param("utilisateurId") Long utilisateurId, @Param("statut") String statut);

    /**
     * Trouve les transactions par statut.
     */
    List<Transaction> findByStatut(String statut);
}