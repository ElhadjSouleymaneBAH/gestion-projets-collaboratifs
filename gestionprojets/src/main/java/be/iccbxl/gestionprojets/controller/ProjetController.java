package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.ProjetDTO;
import be.iccbxl.gestionprojets.service.ProjetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST pour la gestion des projets
 * Exigence F6: Gérer les projets
 */
@RestController
@RequestMapping("/api/projets")
@CrossOrigin(origins = "*")
public class ProjetController {

    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    /**
     * Créer un projet - F6 (Chef de Projet uniquement)
     */
    @PostMapping
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> creerProjet(@RequestBody ProjetDTO projetDTO) {
        ProjetDTO projetCree = projetService.creerProjet(projetDTO);
        return ResponseEntity.ok(projetCree);
    }

    /**
     * Obtenir tous les projets - F3 (Consultation publique)
     */
    @GetMapping
    public List<ProjetDTO> obtenirTousProjets() {
        return projetService.obtenirTousProjets();
    }

    /**
     * Obtenir un projet par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjetDTO> obtenirProjetParId(@PathVariable Long id) {
        Optional<ProjetDTO> projet = projetService.obtenirProjetParId(id);
        return projet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtenir les projets d'un créateur - F6
     */
    @GetMapping("/createur/{idCreateur}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public List<ProjetDTO> obtenirProjetsParCreateur(@PathVariable Long idCreateur) {
        return projetService.obtenirProjetsParCreateur(idCreateur);
    }

    /**
     * Modifier un projet - F6 (Chef de Projet uniquement)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> modifierProjet(@PathVariable Long id, @RequestBody ProjetDTO projetDTO) {
        ProjetDTO projetModifie = projetService.modifierProjet(id, projetDTO);
        return ResponseEntity.ok(projetModifie);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerProjet(@PathVariable Long id) {
        projetService.supprimerProjet(id);
        return ResponseEntity.ok().build();
    }
}