package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.service.TacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST pour la gestion des tâches
 */
@RestController
@RequestMapping("/api/taches")
@CrossOrigin(origins = "*")
public class TacheController {

    private final TacheService tacheService;

    public TacheController(TacheService tacheService) {
        this.tacheService = tacheService;
    }

    /**
     * Création d'une tâche par le Chef de Projet et Membres
     */
    @PostMapping
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> creerTache(@RequestBody TacheDTO tacheDTO) {
        TacheDTO tacheCreee = tacheService.creerTache(tacheDTO);
        return ResponseEntity.ok(tacheCreee);
    }

    /**
     * Avoir toutes les tâches
     */
    @GetMapping
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public List<TacheDTO> obtenirToutesLesTaches() {
        return tacheService.obtenirToutesLesTaches();
    }

    /**
     * Obtenir une tâche par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> obtenirTacheParId(@PathVariable Long id) {
        Optional<TacheDTO> tache = tacheService.obtenirTacheParId(id);
        return tache.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtenir les tâches d'un projet
     */
    @GetMapping("/projet/{idProjet}")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public List<TacheDTO> obtenirTachesParProjet(@PathVariable Long idProjet) {
        return tacheService.obtenirTachesParProjet(idProjet);
    }

    /**
     * Obtenir les tâches assignées à un utilisateur
     */
    @GetMapping("/utilisateur/{idUtilisateur}")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public List<TacheDTO> obtenirTachesParUtilisateur(@PathVariable Long idUtilisateur) {
        return tacheService.obtenirTachesParUtilisateur(idUtilisateur);
    }

    /**
     * Modification d'une tâche
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> modifierTache(@PathVariable Long id, @RequestBody TacheDTO tacheDTO) {
        TacheDTO tacheModifiee = tacheService.modifierTache(id, tacheDTO);
        return ResponseEntity.ok(tacheModifiee);
    }

    /**
     * Suppression d'une tâche  uniquement par le chef de projet
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerTache(@PathVariable Long id) {
        tacheService.supprimerTache(id);
        return ResponseEntity.ok().build();
    }
}