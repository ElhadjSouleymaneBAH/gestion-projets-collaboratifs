package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    // Seul l'administrateur peut voir tous les utilisateurs
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public List<Utilisateur> getTous() {
        return utilisateurService.obtenirTous();
    }

    // Un utilisateur peut voir son profil
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getParId(@PathVariable Long id, Authentication authentication) {

        Optional<Utilisateur> utilisateurDemandeOpt = utilisateurService.obtenirParId(id);
        if (utilisateurDemandeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur utilisateurDemande = utilisateurDemandeOpt.get();
        String emailConnecte = authentication.getName();

        // Vérifie si c'est un administrateur
        boolean estAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

        // Vérifie si c'est son propre profil
        boolean estSonPropreProfil = utilisateurDemande.getEmail().equals(emailConnecte);

        // Autorise si admin OU si c'est son propre profil
        if (estAdmin || estSonPropreProfil) {
            return ResponseEntity.ok(utilisateurDemande);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // Seul l'administrateur peut créer des utilisateurs
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public Utilisateur creer(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.enregistrer(utilisateur);
    }

    // Mise à jour du profil - Un utilisateur peut modifier son profil
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> mettreAJour(@PathVariable Long id,
                                                   @RequestBody Utilisateur utilisateurModifie,
                                                   Authentication authentication) {

        Optional<Utilisateur> utilisateurExistantOpt = utilisateurService.obtenirParId(id);
        if (utilisateurExistantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur utilisateurExistant = utilisateurExistantOpt.get();
        String emailConnecte = authentication.getName();

        // Vérifie si c'est un admin
        boolean estAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

        // Vérifie si c'est son propre profil
        boolean estSonPropreProfil = utilisateurExistant.getEmail().equals(emailConnecte);

        // Autorise si admin OU si c'est son propre profil
        if (estAdmin || estSonPropreProfil) {
            utilisateurModifie.setId(id);
            Utilisateur utilisateurSauvegarde = utilisateurService.enregistrer(utilisateurModifie);
            return ResponseEntity.ok(utilisateurSauvegarde);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // Seul l'administrateur peut supprimer
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
        return ResponseEntity.ok().build();
    }
}