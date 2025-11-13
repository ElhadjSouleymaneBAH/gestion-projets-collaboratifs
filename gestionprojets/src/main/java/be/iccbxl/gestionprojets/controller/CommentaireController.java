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

@RestController
@RequestMapping("/api/commentaires")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175",
                "http://localhost:5177",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:5174",
                "http://127.0.0.1:5175",
                "http://127.0.0.1:5177"
        },
        allowCredentials = "true"
)
public class CommentaireController {

    private final CommentaireService commentaireService;

    public CommentaireController(CommentaireService commentaireService) {
        this.commentaireService = commentaireService;
    }

    /**
     * Obtenir tous les commentaires (utilisé par le tableau de bord admin)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<CommentaireDTO>> obtenirTousCommentaires() {
        try {
            List<CommentaireDTO> commentaires = commentaireService.obtenirTous();
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Alias admin spécifique pour compatibilité avec les anciennes versions
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<CommentaireDTO>> getAllForAdmin() {
        return ResponseEntity.ok(commentaireService.obtenirTous());
    }

    /**
     * Obtenir les commentaires d'une tâche (F9)
     */
    @GetMapping("/tache/{tacheId}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<CommentaireDTO>> obtenirCommentairesTache(@PathVariable Long tacheId) {
        try {
            List<CommentaireDTO> commentaires = commentaireService.obtenirCommentairesTache(tacheId);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
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
            CommentaireDTO commentaireCree = commentaireService.creerCommentaire(commentaireDTO, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(commentaireCree);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
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
            commentaireService.supprimerCommentaire(id);
            return ResponseEntity.ok(Map.of("message", "Commentaire supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }
}
