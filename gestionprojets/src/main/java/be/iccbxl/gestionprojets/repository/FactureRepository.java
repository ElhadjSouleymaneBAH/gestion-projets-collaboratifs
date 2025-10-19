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
     * Fetch join pour éviter LazyInitializationException lors du mapping DTO.
     */
    @Query("""
           SELECT f
             FROM Facture f
             JOIN FETCH f.transaction t
             JOIN FETCH t.utilisateur u
            WHERE u.id = :utilisateurId
         ORDER BY f.dateEmission DESC
           """)
    List<Facture> findFacturesByUtilisateur(@Param("utilisateurId") Long utilisateurId);

    /**
     * Trouve les factures par statut.
     */
    List<Facture> findByStatut(String statut);

    /**
     * Trouve les factures par numéro.
     */
    Optional<Facture> findByNumeroFacture(String numeroFacture);

    // ====== Ajouts fetch-join pour éviter les proxies hors session ======

    @Query("""
           SELECT f
             FROM Facture f
             JOIN FETCH f.transaction t
             JOIN FETCH t.utilisateur u
         ORDER BY f.dateEmission DESC
           """)
    List<Facture> findAllWithTransactionAndUser();

    @Query("""
           SELECT f
             FROM Facture f
             JOIN FETCH f.transaction t
             JOIN FETCH t.utilisateur u
            WHERE f.statut = :statut
         ORDER BY f.dateEmission DESC
           """)
    List<Facture> findByStatutWithJoins(@Param("statut") String statut);

    @Query("""
           SELECT f
             FROM Facture f
             JOIN FETCH f.transaction t
             JOIN FETCH t.utilisateur u
            WHERE f.id = :id
           """)
    Optional<Facture> findByIdWithTransactionAndUser(@Param("id") Long id);
}
