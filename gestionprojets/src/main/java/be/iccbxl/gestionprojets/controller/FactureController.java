package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Facture;
import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.FactureService;
import be.iccbxl.gestionprojets.service.TransactionService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import org.springframework.http.HttpHeaders;
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
        Optional<Transaction> transactionOpt = transactionService.obtenirParId(transactionId);
        if (transactionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Transaction non trouvée");
        }
        Facture facture = factureService.genererFacture(transactionOpt.get());
        return ResponseEntity.ok(facture);
    }

    /**
     * Récupération des factures d'un utilisateur.
     * Liste toutes les factures du Chef de Projet connecté.
     */
    @GetMapping("/mes-factures")
    @PreAuthorize("hasRole('CHEF_PROJET')")
    public ResponseEntity<List<Facture>> mesFactures(Authentication authentication) {
        String email = authentication.getName();
        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Facture> factures = factureService.getFacturesUtilisateur(utilisateurOpt.get().getId());
        return ResponseEntity.ok(factures);
    }

    /**
     * Téléchargement d'une facture PDF.
     */
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<byte[]> telechargerFacturePdf(@PathVariable Long id) {
        Optional<Facture> factureOpt = factureService.findById(id);
        if (factureOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Facture facture = factureOpt.get();
        String pdfContent = "Facture " + facture.getNumeroFacture() + " - (PDF à implémenter)";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "facture_" + facture.getNumeroFacture() + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfContent.getBytes());
    }

    /**
     * Gestion administrative des factures.
     * Réservé aux administrateurs.
     */
    @GetMapping("/toutes")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<Facture>> toutesLesFactures() {
        return ResponseEntity.ok(factureService.findAll());
    }

    /**
     * Consultation d'une facture spécifique.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Facture> consulterFacture(@PathVariable Long id) {
        Optional<Facture> factureOpt = factureService.findById(id);
        return factureOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
