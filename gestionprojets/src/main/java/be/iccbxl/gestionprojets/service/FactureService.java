package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.model.Facture;
import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des factures
 * Support F11 : Générer les factures
 *
 * @author ElhadjSouleymaneBAH
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FactureService {

    private final FactureRepository factureRepository;

    /**
     * F11 : Générer une facture automatiquement après paiement
     */
    public Facture genererFacture(Transaction transaction) {
        Facture facture = new Facture();
        facture.setNumeroFacture(genererNumeroFacture());
        facture.setTransaction(transaction);
        facture.setMontantHT(transaction.getMontantHT());
        facture.setTva(transaction.getTva());
        facture.setDateEmission(LocalDate.now());
        facture.setDateEcheance(LocalDate.now().plusDays(30));
        facture.setStatut("GENEREE");

        return factureRepository.save(facture);
    }

    /**
     * Récupérer une facture par ID
     */
    @Transactional(readOnly = true)
    public Optional<Facture> findById(Long id) {
        return factureRepository.findById(id);
    }

    /**
     * Récupérer une facture par transaction
     */
    @Transactional(readOnly = true)
    public Optional<Facture> findByTransaction(Transaction transaction) {
        return factureRepository.findByTransaction(transaction);
    }

    /**
     * Récupérer toutes les factures d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Facture> getFacturesUtilisateur(Long utilisateurId) {
        return factureRepository.findFacturesByUtilisateur(utilisateurId);
    }

    /**
     * Récupérer les factures par statut
     */
    @Transactional(readOnly = true)
    public List<Facture> getFacturesParStatut(String statut) {
        return factureRepository.findByStatut(statut);
    }

    /**
     * Générer un numéro de facture unique
     */
    private String genererNumeroFacture() {
        int annee = LocalDate.now().getYear();
        String prefixe = "FACT-" + annee + "-";
        long compteur = factureRepository.countByNumeroFactureStartingWith(prefixe) + 1;
        return prefixe + String.format("%03d", compteur);
    }

    /**
     * Récupérer une facture par numéro
     */
    @Transactional(readOnly = true)
    public Optional<Facture> findByNumeroFacture(String numeroFacture) {
        return factureRepository.findByNumeroFacture(numeroFacture);
    }

    /**
     * Sauvegarder une facture
     */
    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    /**
     * Récupérer toutes les factures
     */
    @Transactional(readOnly = true)
    public List<Facture> findAll() {
        return factureRepository.findAll();
    }
}