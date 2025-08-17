package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.CommentaireDTO;
import be.iccbxl.gestionprojets.service.CommentaireService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST pour la gestion des commentaires.
 *
 * Implémente la fonctionnalité F9 du cahier des charges :
 * - F9 : Support collaboration temps réel
 * - Utilisateurs : Membre, Chef de Projet
 * - Importance : 3/5
 * - Contraintes : Commentaires sur tâches
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @see "Cahier des charges - Fonctionnalité F9"
 */
@RestController
@RequestMapping("/api/commentaires")
@CrossOrigin(origins = "*")
public class CommentaireController {

    private final CommentaireService commentaireService;

    public CommentaireController(CommentaireService commentaireService) {
        this.commentaireService = commentaireService;
    }

    /**
     * Obtenir les commentaires d'une tâche (F9)
     */
    @GetMapping("/tache/{tacheId}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<CommentaireDTO>> obtenirCommentairesTache(@PathVariable Long tacheId) {
        try {
            System.out.println("DEBUG: [F9] Consultation commentaires tâche " + tacheId);

            List<CommentaireDTO> commentaires = commentaireService.obtenirCommentairesTache(tacheId);
            return ResponseEntity.ok(commentaires);

        } catch (Exception e) {
            System.out.println("ERROR: [F9] Erreur consultation commentaires: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Ajouter un commentaire à une tâche (F9)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<CommentaireDTO> ajouterCommentaire(@Valid @RequestBody CommentaireDTO commentaireDTO,
                                                             Authentication authentication) {
        try {
            System.out.println("DEBUG: [F9] Ajout commentaire par " + authentication.getName());

            CommentaireDTO commentaireCree = commentaireService.creerCommentaire(commentaireDTO, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(commentaireCree);

        } catch (RuntimeException e) {
            System.out.println("ERROR: [F9] Erreur création commentaire: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F9] Erreur interne: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprimer un commentaire (F9)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> supprimerCommentaire(@PathVariable Long id,
                                                                    Authentication authentication) {
        try {
            System.out.println("DEBUG: [F9] Suppression commentaire " + id);

            commentaireService.supprimerCommentaire(id);
            return ResponseEntity.ok(Map.of("message", "Commentaire supprimé avec succès"));

        } catch (RuntimeException e) {
            System.out.println("ERROR: [F9] Erreur suppression: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println("ERROR: [F9] Erreur interne suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }
}