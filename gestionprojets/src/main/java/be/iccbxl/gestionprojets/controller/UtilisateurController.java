package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {
    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<Utilisateur> getTous() {
        return utilisateurService.obtenirTous();
    }

    @GetMapping("/{id}")
    public Optional<Utilisateur> getParId(@PathVariable Long id) {
        return utilisateurService.obtenirParId(id);
    }

    @PostMapping
    public Utilisateur creer(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.enregistrer(utilisateur);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
    }
}
