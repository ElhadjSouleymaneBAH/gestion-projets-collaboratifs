package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.model.Facture;
import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Value("${app.entreprise.nom:CollabPro Solutions}")
    private String entrepriseNom;

    @Value("${app.entreprise.adresse:Avenue de l'Innovation 123}")
    private String entrepriseAdresse;

    @Value("${app.entreprise.ville:1000 Bruxelles, Belgique}")
    private String entrepriseVille;

    @Value("${app.entreprise.email:contact@collabpro.be}")
    private String entrepriseEmail;

    @Value("${app.entreprise.tva:BE0123.456.789}")
    private String entrepriseTva;

    /**
     * F11 : Générer une facture automatiquement après paiement
     */
    public Facture genererFacture(Transaction transaction) {
        Facture facture = new Facture();
        facture.setNumeroFacture(genererNumeroFacture());
        facture.setTransaction(transaction);

        // Sécurisation null
        Double ht = transaction.getMontantHT() != null ? transaction.getMontantHT() : 0d;
        Double tva = transaction.getTva() != null ? transaction.getTva() : 0d;

        facture.setMontantHT(ht);
        facture.setTva(tva);

        facture.setDateEmission(LocalDate.now());
        facture.setDateEcheance(LocalDate.now().plusDays(30));
        facture.setStatut("GENEREE");
        facture.setPeriode(genererPeriodeFacturation());

        return factureRepository.save(facture);
    }

    /**
     * Récupérer les données complètes pour affichage PDF
     */
    public Map<String, Object> getDonneesFacturePDF(Long factureId) {
        Optional<Facture> factureOpt = findByIdWithJoins(factureId);
        if (factureOpt.isEmpty()) {
            throw new IllegalArgumentException("Facture non trouvée : " + factureId);
        }

        Facture facture = factureOpt.get();
        Map<String, Object> donnees = new HashMap<>();

        // En-tête facture
        donnees.put("factureId", facture.getId());
        donnees.put("numeroFacture", facture.getNumeroFacture());
        donnees.put("dateEmission", formatDate(facture.getDateEmission()));
        donnees.put("dateEcheance", formatDate(facture.getDateEcheance()));
        donnees.put("statut", facture.getStatut());
        donnees.put("periode", facture.getPeriode());

        // Montants
        Double montantHT = facture.getMontantHT() != null ? facture.getMontantHT() : 0.0;
        Double tva = facture.getTva() != null ? facture.getTva() : 0.0;
        Double montantTTC = montantHT + tva;
        donnees.put("montantHT", montantHT);
        donnees.put("tva", tva);
        donnees.put("montantTTC", montantTTC);
        donnees.put("tauxTva", 21.0);
        donnees.put("description", "Abonnement Premium CollabPro - Mensuel");

        // Entreprise
        donnees.put("entrepriseNom", entrepriseNom);
        donnees.put("entrepriseAdresse", entrepriseAdresse);
        donnees.put("entrepriseVille", entrepriseVille);
        donnees.put("entrepriseEmail", entrepriseEmail);
        donnees.put("entrepriseTva", entrepriseTva);
        donnees.put("logoPath", "/logo-collabpro.png");

        // Informations de paiement
        donnees.put("ibanEntreprise", "BE99 9999 9999 9999");
        donnees.put("bicEntreprise", "GEBABEBB");

        // Client
        if (facture.getTransaction() != null && facture.getTransaction().getUtilisateur() != null) {
            var utilisateur = facture.getTransaction().getUtilisateur();

            // Nom complet du client
            String prenom = utilisateur.getPrenom() != null ? utilisateur.getPrenom().trim() : "";
            String nom = utilisateur.getNom() != null ? utilisateur.getNom().trim() : "";
            String nomComplet = (prenom + " " + nom).trim();
            donnees.put("clientNom", !nomComplet.isEmpty() ? nomComplet : "Client");

            // Email du client
            donnees.put("clientEmail", utilisateur.getEmail() != null ? utilisateur.getEmail() : "");

            // Adresse du client
            donnees.put("clientAdresse", utilisateur.getAdresse() != null ? utilisateur.getAdresse().trim() : "");

            // Ville non disponible dans la classe Utilisateur (valeur vide pour cohérence frontend)
            donnees.put("clientVille", "");

            donnees.put("transactionId", facture.getTransaction().getId());
        } else {
            donnees.put("clientNom", "");
            donnees.put("clientEmail", "");
            donnees.put("clientAdresse", "");
            donnees.put("clientVille", "");
        }

        return donnees;
    }

    /**
     * Générer la période de facturation
     */
    private String genererPeriodeFacturation() {
        LocalDate maintenant = LocalDate.now();
        return maintenant.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    /**
     * Formater une date
     */
    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Récupérer une facture par ID
     */
    @Transactional(readOnly = true)
    public Optional<Facture> findById(Long id) {
        return factureRepository.findById(id);
    }

    /**
     * Récupérer une facture par ID avec fetch-join (transaction + utilisateur)
     */
    @Transactional(readOnly = true)
    public Optional<Facture> findByIdWithJoins(Long id) {
        return factureRepository.findByIdWithTransactionAndUser(id);
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
        return factureRepository.findByStatutWithJoins(statut);
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
        return factureRepository.findAllWithTransactionAndUser();
    }
}
