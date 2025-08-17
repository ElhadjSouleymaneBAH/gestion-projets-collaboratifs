package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des transactions.
 * Support pour F10 : Paiements et abonnements.
 *
 * @author ElhadjSouleymaneBAH
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Récupère toutes les transactions d'un utilisateur.
     */
    public List<Transaction> obtenirTransactionsUtilisateur(Long utilisateurId) {
        return transactionRepository.findByUtilisateurId(utilisateurId);
    }

    /**
     * Récupère une transaction par ID.
     */
    public Optional<Transaction> obtenirParId(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * Sauvegarde une transaction.
     */
    public Transaction enregistrer(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Récupère toutes les transactions (admin).
     */
    public List<Transaction> obtenirToutes() {
        return transactionRepository.findAll();
    }

    /**
     * Récupère les transactions par statut.
     */
    public List<Transaction> obtenirParStatut(String statut) {
        return transactionRepository.findByStatut(statut);
    }
}