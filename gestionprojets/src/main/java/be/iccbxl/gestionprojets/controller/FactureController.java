package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Facture;
import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.FactureService;
import be.iccbxl.gestionprojets.service.TransactionService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour F11 : Générer les factures.
 *
 * Fonctionnalité F11 : Générer les factures
 * Utilisateurs : Chef de Projet, Admin
 * Importance : 4/5
 * Contraintes : Après paiement
 *
 * @author ElhadjSouleymaneBAH
 */
@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "*")
public class FactureController {

    private final FactureService factureService;
    private final TransactionService transactionService;
    private final UtilisateurService utilisateurService;

    public FactureController(FactureService factureService,
                             TransactionService transactionService,
                             UtilisateurService utilisateurService) {
        this.factureService = factureService;
        this.transactionService = transactionService;
        this.utilisateurService = utilisateurService;
    }

    /**
     * Génération automatique de facture après paiement.
     * F11 : Création automatique de facture PDF pour chaque paiement.
     */
    @PostMapping("/generer/{transactionId}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<?> genererFacture(@PathVariable Long transactionId,
                                            Authentication authentication) {
        try {
            Optional<Transaction> transactionOpt = transactionService.obtenirParId(transactionId);
            if (transactionOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Transaction non trouvée");
            }

            Transaction transaction = transactionOpt.get();

            // Vérifier que l'utilisateur peut générer cette facture
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé");
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérification des droits (utilise la relation @ManyToOne)
            if (!estAdmin && !transaction.getUtilisateur().getId().equals(utilisateur.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Vous ne pouvez pas générer cette facture");
            }

            // Générer la facture
            Facture facture = factureService.genererFacture(transaction);
            return ResponseEntity.ok(facture);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération de la facture: " + e.getMessage());
        }
    }

    /**
     * Récupération des factures d'un utilisateur.
     * Liste toutes les factures du Chef de Projet connecté.
     */
    @GetMapping("/mes-factures")
    @PreAuthorize("hasRole('CHEF_PROJET')")
    public ResponseEntity<List<Facture>> mesFactures(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<Facture> factures = factureService.getFacturesUtilisateur(
                    utilisateurOpt.get().getId());
            return ResponseEntity.ok(factures);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Téléchargement d'une facture PDF.
     * F11 : Export facture PDF pour chaque paiement.
     */
    @GetMapping("/telecharger/{factureId}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<byte[]> telechargerFacturePdf(@PathVariable Long factureId,
                                                        Authentication authentication) {
        try {
            Optional<Facture> factureOpt = factureService.findById(factureId);
            if (factureOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Facture facture = factureOpt.get();

            // Vérifier les droits d'accès
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérification des droits sur la facture
            Transaction transaction = facture.getTransaction();
            if (!estAdmin && !transaction.getUtilisateur().getId().equals(utilisateur.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // TODO: Implémenter génération PDF (pour TFE, retourner message)
            String pdfContent = "PDF Facture " + facture.getNumeroFacture() + " - À implémenter";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment",
                    "facture_" + facture.getNumeroFacture() + ".txt");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent.getBytes());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new byte[0]);
        }
    }

    /**
     * Gestion administrative des factures.
     * Réservé aux administrateurs.
     */
    @GetMapping("/toutes")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<Facture>> toutesLesFactures() {
        try {
            List<Facture> factures = factureService.findAll();
            return ResponseEntity.ok(factures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Consultation d'une facture spécifique.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Facture> consulterFacture(@PathVariable Long id,
                                                    Authentication authentication) {
        try {
            Optional<Facture> factureOpt = factureService.findById(id);
            if (factureOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Facture facture = factureOpt.get();

            // Vérification des droits
            String email = authentication.getName();
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérification des droits sur la facture
            Transaction transaction = facture.getTransaction();
            if (!estAdmin && !transaction.getUtilisateur().getId().equals(utilisateur.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(facture);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}