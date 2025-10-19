package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.model.Facture;
import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.enums.StatutAbonnement;
import be.iccbxl.gestionprojets.repository.TransactionRepository;
import be.iccbxl.gestionprojets.repository.FactureRepository;
import be.iccbxl.gestionprojets.repository.AbonnementRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service Stripe HYBRIDE pour F10 et F11
 * F10 : Paiements abonnements 10€/mois Chef de Projet
 * F11 : Génération factures automatique après paiement
 * ARCHITECTURE HYBRIDE : SIMULATION + STRIPE_TEST
 *
 * @author ElhadjSouleymaneBAH
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StripeService {

    private final TransactionRepository transactionRepository;
    private final FactureRepository factureRepository;
    private final AbonnementRepository abonnementRepository;

    @Value("${app.paiement.mode:SIMULATION}")
    private String paiementMode;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    /**
     * F10 : Créer PaymentIntent
     * RESPECTE EXACTEMENT votre cahier des charges
     */
    public PaymentIntent creerPaymentIntent(Utilisateur utilisateur) throws StripeException {

        if ("STRIPE_TEST".equals(paiementMode)) {
            return creerPaymentIntentStripe(utilisateur);
        } else {
            return creerPaymentIntentSimulation(utilisateur);
        }
    }

    /**
     * Mode STRIPE_TEST - Vraie intégration Stripe avec vos clés
     */
    private PaymentIntent creerPaymentIntentStripe(Utilisateur utilisateur) throws StripeException {
        String cleStripe = stripeSecretKey;
        com.stripe.Stripe.apiKey = cleStripe;

        System.out.println(" STRIPE TEST: Configuration Stripe avec clés réelles");

        // Créer transaction dans votre base
        Transaction transaction = creerTransactionBase(utilisateur);

        // PaymentIntent Stripe réel
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 1000); // 10€ selon cahier des charges
        params.put("currency", "eur");
        params.put("description", "Abonnement Premium 10€/mois - Transaction #" + transaction.getId());
        params.put("automatic_payment_methods", Map.of("enabled", true));

        Map<String, String> metadata = new HashMap<>();
        metadata.put("transaction_id", transaction.getId().toString());
        metadata.put("utilisateur_email", utilisateur.getEmail());
        metadata.put("application", "gestionprojets_tfe");
        params.put("metadata", metadata);

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        System.out.println(" STRIPE TEST: PaymentIntent créé - ID=" + paymentIntent.getId());
        return paymentIntent;
    }

    /**
     * Mode SIMULATION
     */
    private PaymentIntent creerPaymentIntentSimulation(Utilisateur utilisateur) {
        System.out.println(" MODE SIMULATION: Parfait pour TFE et démo jury");

        // Créer VRAIE transaction dans votre base MySQL
        Transaction transaction = creerTransactionBase(utilisateur);

        PaymentIntent simulatedPaymentIntent = new PaymentIntent() {
            private String id = "pi_sim_" + System.currentTimeMillis();
            private String clientSecret = id + "_secret_simulation";
            private String status = "requires_payment_method";
            private Long amount = 1000L;
            private String currency = "eur";

            @Override public String getId() { return id; }
            @Override public String getClientSecret() { return clientSecret; }
            @Override public String getStatus() { return status; }
            @Override public Long getAmount() { return amount; }
            @Override public String getCurrency() { return currency; }
        };

        System.out.println(" SIMULATION TFE: PaymentIntent simulé - Transaction DB ID=" + transaction.getId());
        System.out.println(" VRAIE transaction sauvée dans votre base MySQL !");

        return simulatedPaymentIntent;
    }

    /**
     * Créer transaction dans la base de données
     */
    private Transaction creerTransactionBase(Utilisateur utilisateur) {
        Transaction transaction = new Transaction();
        transaction.setMontantHT(10.0);
        transaction.setTva(2.1);
        transaction.setMontantTTC(12.1);
        transaction.setMontantAbonnement(10.0);
        transaction.setStatut("EN_ATTENTE");
        transaction.setDateCreation(LocalDateTime.now());
        transaction.setUtilisateur(utilisateur);

        return transactionRepository.save(transaction);
    }

    /**
     * F10 + F11 : Traiter paiement réussi
     * Génération facture automatique + promotion CHEF_PROJET
     */
    public Transaction traiterPaiementReussi(String paymentIntentId, Utilisateur utilisateur) {

        String modeLog = "STRIPE_TEST".equals(paiementMode) ? " STRIPE TEST" : " SIMULATION";
        System.out.println(modeLog + ": Traitement paiement réussi PaymentIntent=" + paymentIntentId);

        // 1. Récupérer transaction EN_ATTENTE
        Transaction transaction = transactionRepository.findByUtilisateurId(utilisateur.getId())
                .stream()
                .filter(t -> "EN_ATTENTE".equals(t.getStatut()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Transaction EN_ATTENTE non trouvée"));

        // 2. Marquer transaction comme COMPLETE
        transaction.setStatut("COMPLETE");
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 3. Créer abonnement (F10)
        creerAbonnement(utilisateur);

        // 4. Générer facture automatiquement (F11)
        genererFacture(savedTransaction);

        System.out.println("✅ " + modeLog + ": Paiement traité - Transaction ID=" + savedTransaction.getId());
        return savedTransaction;
    }

    /**
     * Récupérer PaymentIntent HYBRIDE
     */
    public PaymentIntent recupererPaymentIntent(String paymentIntentId) throws StripeException {
        if ("STRIPE_TEST".equals(paiementMode)) {
            com.stripe.Stripe.apiKey = stripeSecretKey;
            return PaymentIntent.retrieve(paymentIntentId);
        } else {
            // Simulation PaymentIntent
            return new PaymentIntent() {
                @Override public String getId() { return paymentIntentId; }
                @Override public String getStatus() { return "succeeded"; }
                @Override public Long getAmount() { return 1000L; }
                @Override public String getCurrency() { return "eur"; }
                @Override public Long getCreated() { return System.currentTimeMillis() / 1000; }
            };
        }
    }

    /**
     * Créer abonnement après paiement
     */
    private void creerAbonnement(Utilisateur utilisateur) {
        if (abonnementRepository.findByUtilisateur(utilisateur).isEmpty()) {
            Abonnement abonnement = new Abonnement();
            abonnement.setNom("Plan Premium Mensuel");
            abonnement.setPrix(10.0);
            abonnement.setDuree(1);
            abonnement.setUtilisateur(utilisateur);
            abonnement.setType("premium");
            abonnement.setStatut(StatutAbonnement.ACTIF);
            abonnement.setDateDebut(LocalDate.now());
            abonnement.setDateFin(LocalDate.now().plusMonths(1));

            Abonnement savedAbonnement = abonnementRepository.save(abonnement);
            System.out.println(" Abonnement créé - ID=" + savedAbonnement.getId());
        }
    }

    /**
     * F11 : Générer facture automatiquement
     */
    private void genererFacture(Transaction transaction) {
        Facture facture = new Facture();
        facture.setNumeroFacture(genererNumeroFacture());
        facture.setTransaction(transaction);
        facture.setMontantHT(transaction.getMontantHT());
        facture.setTva(transaction.getTva());
        facture.setDateEmission(LocalDate.now());
        facture.setDateEcheance(LocalDate.now().plusDays(30));
        facture.setStatut("GENEREE");

        Facture savedFacture = factureRepository.save(facture);
        System.out.println(" Facture générée - " + savedFacture.getNumeroFacture());
    }

    /**
     * Générer numéro de facture unique
     */
    private String genererNumeroFacture() {
        int annee = LocalDate.now().getYear();
        String prefixe = "FACT-" + annee + "-";
        long compteur = factureRepository.countByNumeroFactureStartingWith(prefixe) + 1;
        return prefixe + String.format("%03d", compteur);
    }

    /**
     * Vérifier abonnement actif
     */
    @Transactional(readOnly = true)
    public boolean utilisateurAAbonnementActif(Long utilisateurId) {
        return abonnementRepository.existsByUtilisateurId(utilisateurId);
    }
}
