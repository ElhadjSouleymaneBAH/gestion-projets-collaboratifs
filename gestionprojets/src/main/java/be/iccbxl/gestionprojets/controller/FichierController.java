package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.FichierDTO;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.security.JwtService;
import be.iccbxl.gestionprojets.service.FichierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fichiers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FichierController {

    private final FichierService fichierService;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Upload un fichier dans un projet
     * POST /api/fichiers/upload?projetId=1
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFichier(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projetId") Long projetId,
            @RequestHeader("Authorization") String token) {

        try {
            log.info("[API Upload]  Fichier: {}, Projet: {}", file.getOriginalFilename(), projetId);

            // Extraire l'ID utilisateur depuis le token
            Long userId = getUserIdFromToken(token);

            FichierDTO fichierDTO = fichierService.uploadFichier(file, projetId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Fichier uploadé avec succès");
            response.put("data", fichierDTO);

            log.info("[API Upload]  Fichier uploadé: ID {}", fichierDTO.getId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("[API Upload]  Erreur validation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("[API Upload]  Erreur: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Erreur lors de l'upload du fichier: " + e.getMessage()
            ));
        }
    }

    /**
     * Récupère tous les fichiers d'un projet
     * GET /api/fichiers/projet/1
     */
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<FichierDTO>> getFichiersByProjet(@PathVariable Long projetId) {
        try {
            log.info("[API Get]  Récupération fichiers du projet {}", projetId);
            List<FichierDTO> fichiers = fichierService.getFichiersByProjet(projetId);
            log.info("[API Get]  {} fichier(s) trouvé(s)", fichiers.size());
            return ResponseEntity.ok(fichiers);
        } catch (Exception e) {
            log.error("[API Get]  Erreur: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Télécharge un fichier
     */
    @GetMapping("/{fichierId}/telecharger")
    public ResponseEntity<Resource> telechargerFichier(
            @PathVariable Long fichierId,
            @RequestParam Long projetId) {

        try {
            log.info("[API Download]  Fichier: {}, Projet: {}", fichierId, projetId);

            Resource resource = fichierService.telechargerFichier(fichierId, projetId);
            FichierDTO fichierInfo = fichierService.getFichierInfo(fichierId, projetId);

            String contentType = fichierInfo.getTypeMime();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            log.info("[API Download]  Téléchargement: {}", fichierInfo.getNomOriginal());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fichierInfo.getNomOriginal() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("[API Download]  Erreur: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime un fichier
     * DELETE /api/fichiers/1?projetId=1
     */
    @DeleteMapping("/{fichierId}")
    public ResponseEntity<?> supprimerFichier(
            @PathVariable Long fichierId,
            @RequestParam Long projetId,
            @RequestHeader("Authorization") String token) {

        try {
            log.info("[API Delete]  Fichier: {}, Projet: {}", fichierId, projetId);

            // Extraire l'ID utilisateur depuis le token
            Long userId = getUserIdFromToken(token);

            fichierService.supprimerFichier(fichierId, projetId, userId);

            log.info("[API Delete]  Fichier supprimé");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Fichier supprimé avec succès"
            ));

        } catch (Exception e) {
            log.error("[API Delete]  Erreur: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Erreur lors de la suppression du fichier: " + e.getMessage()
            ));
        }
    }

    /**
     * Récupère les infos d'un fichier
     * GET /api/fichiers/1?projetId=1
     */
    @GetMapping("/{fichierId}")
    public ResponseEntity<FichierDTO> getFichierInfo(
            @PathVariable Long fichierId,
            @RequestParam Long projetId) {

        try {
            log.info("[API Info]  Fichier: {}, Projet: {}", fichierId, projetId);
            FichierDTO fichier = fichierService.getFichierInfo(fichierId, projetId);
            return ResponseEntity.ok(fichier);
        } catch (Exception e) {
            log.error("[API Info]  Erreur: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Extrait l'ID utilisateur du token JWT
     * Utilise JwtService pour extraire le username, puis cherche l'utilisateur en BDD
     */
    private Long getUserIdFromToken(String token) {
        try {
            // Nettoyer le token
            String jwt = token.replace("Bearer ", "").trim();

            // Extraire le username (email) du token
            String email = jwtService.extractUsername(jwt);

            log.debug("[JWT]  Email extrait du token: {}", email);

            // Chercher l'utilisateur en BDD
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable pour l'email: " + email));

            log.debug("[JWT]  Utilisateur trouvé: ID {}", utilisateur.getId());

            return utilisateur.getId();

        } catch (Exception e) {
            log.error("[JWT]  Erreur extraction user ID: {}", e.getMessage());
            throw new RuntimeException("Token invalide ou utilisateur introuvable", e);
        }
    }
}