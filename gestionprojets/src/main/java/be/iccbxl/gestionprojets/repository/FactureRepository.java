package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Facture;
import be.iccbxl.gestionprojets.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour les factures.
 * Support F11 : Générer les factures.
 *
 * @author ElhadjSouleymaneBAH
 */
@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    /**
     * Trouve une facture par transaction.
     */
    Optional<Facture> findByTransaction(Transaction transaction);

    /**
     * Compte les factures par préfixe de numéro.
     */
    long countByNumeroFactureStartingWith(String prefix);

    /**
     * Trouve toutes les factures d'un utilisateur via les transactions.
     */
    @Query("SELECT f FROM Facture f WHERE f.transaction.id IN " +
            "(SELECT t.id FROM Transaction t WHERE t.utilisateur.id = :utilisateurId) " +
            "ORDER BY f.dateEmission DESC")
    List<Facture> findFacturesByUtilisateur(@Param("utilisateurId") Long utilisateurId);

    /**
     * Trouve les factures par statut.
     */
    List<Facture> findByStatut(String statut);

    /**
     * Trouve les factures par numéro.
     */
    Optional<Facture> findByNumeroFacture(String numeroFacture);
}
