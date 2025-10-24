package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.FactureDTO;
import be.iccbxl.gestionprojets.mapper.FactureMapper;
import be.iccbxl.gestionprojets.model.Facture;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.FactureService;
import be.iccbxl.gestionprojets.service.PdfGenerator;
import be.iccbxl.gestionprojets.service.TransactionService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Contrôleur pour F11 : Générer les factures.
 * Fonctionnalité F11 : Générer les factures
 * Utilisateurs : MEMBRE (après paiement), Chef de Projet, Admin
 * Importance : 4/5
 * Contraintes : Après paiement
 *
 * @author ElhadjSouleymaneBah
 * @version 1.0
 */
@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureService factureService;
    private final TransactionService transactionService;
    private final UtilisateurService utilisateurService;
    private final PdfGenerator pdfGenerator;

    public FactureController(FactureService factureService,
                             TransactionService transactionService,
                             UtilisateurService utilisateurService,
                             PdfGenerator pdfGenerator) {
        this.factureService = factureService;
        this.transactionService = transactionService;
        this.utilisateurService = utilisateurService;
        this.pdfGenerator = pdfGenerator;
    }

    // ========== Récupération des factures (toutes ou filtrées) ==========
    @GetMapping
    public ResponseEntity<?> getFactures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long utilisateurId,
            Authentication authentication) {

        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Utilisateur non authentifié"));
            }

            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            Long targetUserId = (userId != null) ? userId : utilisateurId;

            // Vérification d’accès
            if (!estAdmin && targetUserId != null) {
                Optional<Utilisateur> userOpt = utilisateurService.obtenirParEmail(emailConnecte);
                if (userOpt.isEmpty() || !userOpt.get().getId().equals(targetUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Accès interdit aux factures d'un autre utilisateur"));
                }
            }

            // Récupération des factures
            List<Facture> factures;
            if (targetUserId != null) {
                factures = factureService.getFacturesUtilisateur(targetUserId);
            } else if (statut != null) {
                factures = factureService.getFacturesParStatut(statut);
            } else {
                factures = factureService.findAll();
            }

            // Recherche textuelle
            if (recherche != null && !recherche.trim().isEmpty()) {
                String rechLower = recherche.toLowerCase();
                factures = factures.stream()
                        .filter(f -> f.getNumeroFacture().toLowerCase().contains(rechLower)
                                || (f.getPeriode() != null && f.getPeriode().toLowerCase().contains(rechLower)))
                        .toList();
            }

            // Filtrage par période
            if (periode != null && !periode.trim().isEmpty()) {
                factures = factures.stream()
                        .filter(f -> f.getPeriode() != null && f.getPeriode().contains(periode))
                        .toList();
            }

            // Pagination manuelle
            int start = page * size;
            int end = Math.min(start + size, factures.size());
            List<Facture> facturesPaginee = (start < factures.size()) ? factures.subList(start, end) : List.of();

            List<FactureDTO> facturesDTO = facturesPaginee.stream()
                    .map(FactureMapper::toDTO)
                    .toList();

            Map<String, Object> response = Map.of(
                    "content", facturesDTO,
                    "number", page,
                    "size", size,
                    "totalElements", factures.size(),
                    "totalPages", (int) Math.ceil((double) factures.size() / size)
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR: Erreur récupération factures: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des factures"));
        }
    }

    // ========== ADMIN : Voir toutes les factures ==========
    @GetMapping("/toutes")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<?> getToutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long utilisateurId,
            Authentication authentication) {
        return getFactures(page, size, statut, periode, recherche, userId, utilisateurId, authentication);
    }

    // ========== Données PDF ==========
    @GetMapping("/{id}/pdf-data")
    public ResponseEntity<?> getDonneesPDF(@PathVariable Long id, Authentication authentication) {
        try {
            Optional<Facture> factureOpt = factureService.findByIdWithJoins(id);
            if (factureOpt.isEmpty()) return ResponseEntity.notFound().build();

            Facture facture = factureOpt.get();
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && (facture.getTransaction() == null
                    || facture.getTransaction().getUtilisateur() == null
                    || !facture.getTransaction().getUtilisateur().getEmail().equals(emailConnecte))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accès interdit à cette facture"));
            }

            Map<String, Object> donneesPDF = factureService.getDonneesFacturePDF(id);

            // Ajout infos fixes entreprise si manquantes
            donneesPDF.putIfAbsent("entrepriseNom", "CollabPro Solutions");
            donneesPDF.putIfAbsent("entrepriseAdresse", "Rue du Projet 42, 1000 Bruxelles");
            donneesPDF.putIfAbsent("entrepriseTva", "BE0123.456.789");
            donneesPDF.putIfAbsent("entrepriseEmail", "support@collabpro.com");
            donneesPDF.putIfAbsent("logoPath", "/logo-collabpro.png");

            return ResponseEntity.ok(donneesPDF);

        } catch (Exception e) {
            System.err.println("ERROR: Erreur données PDF facture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des données PDF"));
        }
    }

    // ========== Téléchargement PDF localisé ==========
    @GetMapping(value = {"/{id}/telecharger", "/telecharger/{id}"})
    public ResponseEntity<?> telecharger(
            @PathVariable Long id,
            @RequestParam(required = false) String langue,
            @RequestHeader(value = "Accept-Language", required = false) String headerLang,
            Authentication authentication) {
        try {
            Optional<Facture> factureOpt = factureService.findByIdWithJoins(id);
            if (factureOpt.isEmpty()) return ResponseEntity.notFound().build();

            Facture facture = factureOpt.get();
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && (facture.getTransaction() == null
                    || facture.getTransaction().getUtilisateur() == null
                    || !facture.getTransaction().getUtilisateur().getEmail().equals(emailConnecte))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accès interdit à cette facture"));
            }

            // Langue
            String effectiveLang = (langue != null && !langue.isBlank())
                    ? langue
                    : (headerLang != null ? headerLang : "fr");

            effectiveLang = effectiveLang.toLowerCase(Locale.ROOT).startsWith("en") ? "en" : "fr";

            // Génération du PDF
            Map<String, Object> data = factureService.getDonneesFacturePDF(id);
            byte[] pdfBytes = pdfGenerator.generate(data, effectiveLang);

            String fileName = (facture.getNumeroFacture() != null
                    ? facture.getNumeroFacture()
                    : "facture-" + id) + "-" + effectiveLang + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("ERROR: Erreur téléchargement facture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du téléchargement de la facture"));
        }
    }

    // ========== Factures de l’utilisateur connecté ==========
    @GetMapping("/mes-factures")
    public ResponseEntity<?> mesFactures(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        String email = authentication.getName();
        Optional<Utilisateur> utilisateurOpt = utilisateurService.obtenirParEmail(email);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Utilisateur connecté introuvable en base : " + email));
        }

        List<Facture> factures = factureService.getFacturesUtilisateur(utilisateurOpt.get().getId());
        List<FactureDTO> facturesDTO = factures.stream()
                .map(FactureMapper::toDTO)
                .toList();

        System.out.println("DEBUG: [F11] Factures retournées pour " + email + " : " + facturesDTO.size());
        return ResponseEntity.ok(facturesDTO);
    }
}
