package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.ListeColonneDTO;
import be.iccbxl.gestionprojets.service.ListeColonneService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST pour la gestion des colonnes Kanban (ListeColonne)
 *
 * @author Elhadj Souleymane BAH
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
public class ListeColonneController {

    private final ListeColonneService listeColonneService;
    private final UtilisateurService utilisateurService;

    public ListeColonneController(ListeColonneService listeColonneService,
                                  UtilisateurService utilisateurService) {
        this.listeColonneService = listeColonneService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/projets/{idProjet}/colonnes")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ListeColonneDTO>> obtenirColonnesProjet(
            @PathVariable Long idProjet,
            Authentication authentication) {

        try {
            System.out.println("[ListeColonneController] GET /api/projets/" + idProjet + "/colonnes");

            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, idProjet)) {
                System.err.println("[ListeColonneController] Accès refusé pour utilisateur " + idUser);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!listeColonneService.projetADesColonnes(idProjet)) {
                System.out.println("[ListeColonneController] Création colonnes par défaut");
                List<ListeColonneDTO> colonnes = listeColonneService.creerColonnesParDefaut(idProjet);
                return ResponseEntity.ok(colonnes);
            }

            List<ListeColonneDTO> colonnes = listeColonneService.obtenirColonnesParProjet(idProjet);

            System.out.println("[ListeColonneController] " + colonnes.size() + " colonnes retournées");

            return ResponseEntity.ok(colonnes);

        } catch (Exception e) {
            System.err.println("[ListeColonneController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/projets/{idProjet}/colonnes")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ListeColonneDTO> creerColonne(
            @PathVariable Long idProjet,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        try {
            System.out.println("[ListeColonneController] POST /api/projets/" + idProjet + "/colonnes");

            if (!body.containsKey("nom") || body.get("nom").trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, idProjet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            ListeColonneDTO dto = new ListeColonneDTO();
            dto.setNom(body.get("nom").trim());
            dto.setIdProjet(idProjet);

            ListeColonneDTO colonneCreee = listeColonneService.creerColonne(dto);

            System.out.println("[ListeColonneController] Colonne créée: " + colonneCreee.getNom());

            return ResponseEntity.status(HttpStatus.CREATED).body(colonneCreee);

        } catch (RuntimeException e) {
            System.err.println("[ListeColonneController] Erreur création: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("[ListeColonneController] Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/colonnes/{id}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ListeColonneDTO> renommerColonne(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        try {
            System.out.println("[ListeColonneController] PUT /api/colonnes/" + id);

            if (!body.containsKey("nom") || body.get("nom").trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            ListeColonneDTO colonneModifiee = listeColonneService.renommerColonne(id, body.get("nom").trim());

            System.out.println("[ListeColonneController] Colonne renommée: " + colonneModifiee.getNom());

            return ResponseEntity.ok(colonneModifiee);

        } catch (RuntimeException e) {
            System.err.println("[ListeColonneController] Erreur modification: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("[ListeColonneController] Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/colonnes/{id}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerColonne(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            System.out.println("[ListeColonneController] DELETE /api/colonnes/" + id);

            listeColonneService.supprimerColonne(id);

            System.out.println("[ListeColonneController] Colonne supprimée");

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            System.err.println("[ListeColonneController] Erreur suppression: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("[ListeColonneController] Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/projets/{idProjet}/colonnes/reorganiser")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> reorganiserColonnes(
            @PathVariable Long idProjet,
            @RequestBody Map<String, List<Long>> body,
            Authentication authentication) {

        try {
            System.out.println("[ListeColonneController] POST /api/projets/" + idProjet + "/colonnes/reorganiser");

            if (!body.containsKey("ordre") || body.get("ordre").isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, idProjet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            listeColonneService.reorganiserColonnes(idProjet, body.get("ordre"));

            System.out.println("[ListeColonneController] Colonnes réorganisées");

            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            System.err.println("[ListeColonneController] Erreur réorganisation: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("[ListeColonneController] Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/projets/{idProjet}/colonnes/initialiser")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ListeColonneDTO>> initialiserColonnes(
            @PathVariable Long idProjet,
            Authentication authentication) {

        try {
            System.out.println("[ListeColonneController] POST /api/projets/" + idProjet + "/colonnes/initialiser");

            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, idProjet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<ListeColonneDTO> colonnes = listeColonneService.creerColonnesParDefaut(idProjet);

            System.out.println("[ListeColonneController] " + colonnes.size() + " colonnes initialisées");

            return ResponseEntity.status(HttpStatus.CREATED).body(colonnes);

        } catch (RuntimeException e) {
            System.err.println("[ListeColonneController] Erreur initialisation: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("[ListeColonneController] Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}