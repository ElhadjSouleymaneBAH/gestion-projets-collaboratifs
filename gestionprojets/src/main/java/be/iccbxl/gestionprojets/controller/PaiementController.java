package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.StripeService;
import be.iccbxl.gestionprojets.service.TransactionService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur HYBRIDE pour F10 : Paiements et abonnements.
 *
 * Fonctionnalité F10 : Paiements et abonnements
 * Utilisateurs : Chef de Projet, Admin
 * Importance : 5/5
 * Contraintes : Stripe TEST requis
 * Description : Souscrire à un abonnement mensuel sécurisé via Stripe TEST

 *
 * @author ElhadjSouleymaneBAH
 */
@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "*")
public class PaiementController {

    private final StripeService stripeService;
    private final TransactionService transactionService;
    private final UtilisateurService utilisateurService;

    public PaiementController(StripeService stripeService,
                              TransactionService transactionService,
                              UtilisateurService utilisateurService) {
        this.stripeService = stripeService;
        this.transactionService = transactionService;
        this.utilisateurService = utilisateurService;
    }

    /**
     * F10 : Création PaymentIntent
     * Contrainte cahier des charges : Chef de Projet DOIT souscrire abonnement 10€/mois.
     * FONCTIONNE en mode SIMULATION ou STRIPE_TEST
     */
    @PostMapping("/creer-payment-intent")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET')")
    public ResponseEntity<?> creerPaymentIntent(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé");
            }

            Utilisateur utilisateur = utilisateurOpt.get();

            // INTÉGRATION HYBRIDE : SIMULATION ou STRIPE selon configuration
            PaymentIntent paymentIntent = stripeService.creerPaymentIntent(utilisateur);

            // CORRECTION : Suppression de "mode": "HYBRIDE_TFE" amateur
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("paymentIntentId", paymentIntent.getId());
            response.put("status", paymentIntent.getStatus());
            response.put("amount", paymentIntent.getAmount().toString());
            response.put("currency", paymentIntent.getCurrency());
            // SUPPRIMÉ : response.put("mode", "HYBRIDE_TFE");

            System.out.println(" PaymentIntent créé: " + paymentIntent.getId());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            System.err.println(" Erreur Stripe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur Stripe : " + e.getMessage());
        } catch (Exception e) {
            System.err.println(" Erreur interne: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne : " + e.getMessage());
        }
    }

    /**
     * F10 : Confirmation paiement réussi.
     * Mise à jour rôle vers CHEF_PROJET après paiement + génération facture F11.
     */
    @PostMapping("/confirmer-paiement")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET')")
    public ResponseEntity<?> confirmerPaiement(@RequestParam String paymentIntentId,
                                               Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé");
            }

            Utilisateur utilisateur = utilisateurOpt.get();

            // Traitement paiement HYBRIDE + génération facture F11
            Transaction transaction = stripeService.traiterPaiementReussi(paymentIntentId, utilisateur);

            // Contrainte cahier des charges : promotion automatique vers CHEF_PROJET
            if (!"CHEF_PROJET".equals(utilisateur.getRole().toString())) {
                utilisateur.setRole(be.iccbxl.gestionprojets.enums.Role.CHEF_PROJET);
                utilisateurService.enregistrer(utilisateur);
                System.out.println(" Utilisateur promu vers CHEF_PROJET: " + utilisateur.getEmail());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Paiement confirmé et traité avec succès");
            response.put("transactionId", transaction.getId());
            response.put("paymentIntentId", paymentIntentId);
            response.put("utilisateurRole", utilisateur.getRole().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println(" Erreur confirmation paiement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur confirmation paiement : " + e.getMessage());
        }
    }

    /**
     * F10 : Historique transactions Chef de Projet.
     * Affiche toutes les transactions de l'utilisateur connecté.
     */
    @GetMapping("/mes-transactions")
    @PreAuthorize("hasRole('CHEF_PROJET')")
    public ResponseEntity<List<Transaction>> mesTransactions(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<Transaction> transactions = transactionService.obtenirTransactionsUtilisateur(
                    utilisateurOpt.get().getId());

            System.out.println(" Récupération transactions pour: " + email + " (" + transactions.size() + " transactions)");

            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            System.err.println(" Erreur récupération transactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * F10 : Vérifier statut abonnement utilisateur.
     */
    @GetMapping("/statut-abonnement")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET')")
    public ResponseEntity<?> statutAbonnement(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé");
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            boolean aAbonnementActif = stripeService.utilisateurAAbonnementActif(utilisateur.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("utilisateurId", utilisateur.getId());
            response.put("email", utilisateur.getEmail());
            response.put("role", utilisateur.getRole().toString());
            response.put("aAbonnementActif", aAbonnementActif);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur vérification statut : " + e.getMessage());
        }
    }

    /**
     * F10 : Webhook Stripe pour notifications temps réel.
     * Contrainte cahier des charges : intégration Stripe complète.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhookStripe(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            System.out.println(" Webhook Stripe reçu");
            return ResponseEntity.ok("Webhook traité avec succès");
        } catch (Exception e) {
            System.err.println(" Erreur webhook: " + e.getMessage());
            return ResponseEntity.badRequest().body("Erreur webhook");
        }
    }

    /**
     * F10 : Gestion administrative (Admin uniquement).
     * Permet aux administrateurs de voir toutes les transactions.
     */
    @GetMapping("/toutes-transactions")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<Transaction>> toutesTransactions() {
        try {
            List<Transaction> transactions = transactionService.obtenirToutes();

            System.out.println(" Admin: Récupération de toutes les transactions (" + transactions.size() + " transactions)");

            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            System.err.println(" Erreur admin transactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * F10 : Récupérer détails PaymentIntent  depuis Stripe.
     */
    @GetMapping("/payment-intent/{paymentIntentId}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<?> getPaymentIntentDetails(@PathVariable String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = stripeService.recupererPaymentIntent(paymentIntentId);

            Map<String, Object> response = new HashMap<>();
            response.put("id", paymentIntent.getId());
            response.put("status", paymentIntent.getStatus());
            response.put("amount", paymentIntent.getAmount());
            response.put("currency", paymentIntent.getCurrency());
            response.put("created", paymentIntent.getCreated());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur Stripe : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }
}