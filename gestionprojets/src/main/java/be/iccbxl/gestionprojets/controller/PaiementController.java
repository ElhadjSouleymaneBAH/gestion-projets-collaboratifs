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
 * Contrôleur Paiements (Stripe)
 * Aligne les endpoints avec le frontend: /api/stripe/create-payment-intent et /api/stripe/confirm-payment
 */
@RestController
@RequestMapping("/api/stripe")
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
     * F10 : Créer PaymentIntent (10€/mois)
     */
    @PostMapping("/create-payment-intent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> creerPaymentIntent(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Utilisateur non trouvé"));
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            PaymentIntent paymentIntent = stripeService.creerPaymentIntent(utilisateur);

            Map<String, Object> response = new HashMap<>();
            // alias snake_case utilisé par le front
            response.put("client_secret", paymentIntent.getClientSecret());
            response.put("payment_intent_id", paymentIntent.getId());
            // champs additionnels informatifs
            response.put("status", paymentIntent.getStatus());
            response.put("amount", paymentIntent.getAmount());
            response.put("currency", paymentIntent.getCurrency());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur Stripe", "details", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne", "details", e.getMessage()));
        }
    }

    /**
     * F10 : Confirmation paiement → Promotion + Facture
     * Compatibilité front :
     *  - Query param : /confirm-payment?paymentIntentId=pi_xxx
     *  - OU Body JSON : { "payment_intent_id": "pi_xxx" } ou { "paymentIntentId": "pi_xxx" }
     */
    @PostMapping("/confirm-payment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> confirmerPaiement(@RequestParam(required = false) String paymentIntentId,
                                               @RequestBody(required = false) Map<String, Object> body,
                                               Authentication authentication) {
        try {
            // Tolérer query param OU JSON body
            if ((paymentIntentId == null || paymentIntentId.isBlank()) && body != null) {
                Object snake = body.get("payment_intent_id");
                Object camel = body.get("paymentIntentId");
                if (camel != null) paymentIntentId = camel.toString();
                else if (snake != null) paymentIntentId = snake.toString();
            }

            if (paymentIntentId == null || paymentIntentId.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "paymentIntentId manquant"));
            }

            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Utilisateur non trouvé"));
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            Transaction transaction = stripeService.traiterPaiementReussi(paymentIntentId, utilisateur);

            // Promotion automatique vers CHEF_PROJET via UtilisateurService si nécessaire
            if (!"CHEF_PROJET".equals(utilisateur.getRole().toString())) {
                utilisateurService.promouvoirVersChefProjetAvecAbonnement(utilisateur.getId());
                // recharger l'utilisateur pour s'assurer du rôle à jour
                utilisateur = utilisateurService.findByEmail(email).orElse(utilisateur);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Paiement confirmé avec succès");
            response.put("transactionId", transaction.getId());
            response.put("paymentIntentId", paymentIntentId);
            response.put("nouveauRole", utilisateur.getRole().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur confirmation paiement", "details", e.getMessage()));
        }
    }

    /**
     * Historique transactions utilisateur
     * Seuls les Chef de projet et Administrateur peuvent voir les transactions
     */
    @GetMapping("/mes-transactions")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<Transaction>> mesTransactions(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<Transaction> transactions =
                    transactionService.obtenirTransactionsUtilisateur(utilisateurOpt.get().getId());

            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifier statut abonnement
     * CORRIGÉ : Selon cahier des charges F10 - Chef de projet, Administrateur
     */
    @GetMapping("/statut-abonnement")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<?> statutAbonnement(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Utilisateur non trouvé"));
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            boolean actif = stripeService.utilisateurAAbonnementActif(utilisateur.getId());

            return ResponseEntity.ok(Map.of(
                    "utilisateurId", utilisateur.getId(),
                    "email", utilisateur.getEmail(),
                    "role", utilisateur.getRole().toString(),
                    "abonnementActif", actif
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur vérification abonnement", "details", e.getMessage()));
        }
    }
}
