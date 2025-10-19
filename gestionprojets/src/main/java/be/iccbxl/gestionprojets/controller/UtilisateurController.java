package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.InscriptionDTO;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@RestController
@RequestMapping("/api/utilisateurs")
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
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> sInscrire(@Valid @RequestBody InscriptionDTO inscriptionDTO) {
        System.out.println("inscriptionDTO: " + inscriptionDTO);

        try {
            if (!inscriptionDTO.isCguAccepte()) {
                return ResponseEntity.badRequest()
                        .body("L'acceptation des CGU est obligatoire pour la conformité RGPD");
            }

            String emailNormalise = inscriptionDTO.getEmail() != null
                    ? inscriptionDTO.getEmail().trim().toLowerCase()
                    : null;
            if (emailNormalise == null || emailNormalise.isBlank()) {
                return ResponseEntity.badRequest().body("L'email est requis");
            }
            inscriptionDTO.setEmail(emailNormalise);

            if (utilisateurService.existeParEmail(emailNormalise)) {
                return ResponseEntity.badRequest()
                        .body("Un compte existe déjà avec cette adresse email");
            }

            Utilisateur nouvelUtilisateur = utilisateurService.inscrire(inscriptionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'inscription");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getParId(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Utilisateur> utilisateurDemandeOpt = utilisateurService.obtenirParId(id);
        if (utilisateurDemandeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur utilisateurDemande = utilisateurDemandeOpt.get();
        String emailConnecte = authentication.getName();

        boolean estAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

        boolean estSonPropreProfil = utilisateurDemande.getEmail().equals(emailConnecte);

        if (estAdmin || estSonPropreProfil) {
            return ResponseEntity.ok(utilisateurDemande);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> mettreAJour(@PathVariable Long id,
                                                   @Valid @RequestBody Utilisateur utilisateurModifie,
                                                   Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Utilisateur> utilisateurExistantOpt = utilisateurService.obtenirParId(id);
        if (utilisateurExistantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur utilisateurExistant = utilisateurExistantOpt.get();
        String emailConnecte = authentication.getName();

        boolean estAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

        boolean estSonPropreProfil = utilisateurExistant.getEmail().equals(emailConnecte);

        if (estAdmin || estSonPropreProfil) {
            try {
                utilisateurModifie.setId(id);
                Utilisateur utilisateurSauvegarde = utilisateurService.enregistrer(utilisateurModifie);
                return ResponseEntity.ok(utilisateurSauvegarde);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<Utilisateur>> rechercherUtilisateurs(@RequestParam String q) {
        try {
            System.out.println("DEBUG: [F8] Recherche utilisateurs avec terme: " + q);

            final String query = q == null ? "" : q.trim().toLowerCase();

            List<Utilisateur> utilisateurs = utilisateurService.obtenirTousLesUtilisateursSansProjet()
                    .stream()
                    .filter(u -> {
                        String nom = u.getNom() == null ? "" : u.getNom().toLowerCase();
                        String prenom = u.getPrenom() == null ? "" : u.getPrenom().toLowerCase();
                        String email = u.getEmail() == null ? "" : u.getEmail().toLowerCase();
                        return nom.contains(query) || prenom.contains(query) || email.contains(query);
                    })
                    .collect(Collectors.toList());

            System.out.println("DEBUG: [F8] " + utilisateurs.size() + " utilisateurs trouvés");
            return ResponseEntity.ok(utilisateurs);

        } catch (Exception e) {
            System.err.println("ERROR: [F8] Erreur recherche utilisateurs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<?> getTous(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "") String role,
            @RequestParam(required = false, defaultValue = "") String statut,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        try {
            System.out.println("DEBUG: [ADMIN] getTous() - Paramètres reçus:");
            System.out.println("  - q: [" + q + "]");
            System.out.println("  - role: [" + role + "]");
            System.out.println("  - statut: [" + statut + "]");
            System.out.println("  - page: " + page);
            System.out.println("  - size: " + size);

            List<Utilisateur> tousLesUtilisateurs = utilisateurService.obtenirTous();

            // Filtrage
            List<Utilisateur> utilisateursFiltres = tousLesUtilisateurs.stream()
                    .filter(u -> {
                        // Filtrage par terme de recherche
                        if (q != null && !q.trim().isEmpty()) {
                            String terme = q.toLowerCase();
                            String nom = u.getNom() == null ? "" : u.getNom().toLowerCase();
                            String prenom = u.getPrenom() == null ? "" : u.getPrenom().toLowerCase();
                            String email = u.getEmail() == null ? "" : u.getEmail().toLowerCase();
                            if (!nom.contains(terme) && !prenom.contains(terme) && !email.contains(terme)) {
                                return false;
                            }
                        }

                        // Filtrage par rôle
                        if (role != null && !role.trim().isEmpty()) {
                            if (!u.getRole().name().equalsIgnoreCase(role)) {
                                return false;
                            }
                        }

                        return true;
                    })
                    .collect(Collectors.toList());

            // Pagination
            int debut = page * size;
            int fin = Math.min(debut + size, utilisateursFiltres.size());

            if (debut >= utilisateursFiltres.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("content", Collections.emptyList());
                response.put("totalElements", utilisateursFiltres.size());
                response.put("totalPages", (int) Math.ceil((double) utilisateursFiltres.size() / size));
                response.put("number", page);
                response.put("size", size);
                return ResponseEntity.ok(response);
            }

            List<Utilisateur> pageUtilisateurs = utilisateursFiltres.subList(debut, fin);

            Map<String, Object> response = new HashMap<>();
            response.put("content", pageUtilisateurs);
            response.put("totalElements", utilisateursFiltres.size());
            response.put("totalPages", (int) Math.ceil((double) utilisateursFiltres.size() / size));
            response.put("number", page);
            response.put("size", size);

            System.out.println("DEBUG: [ADMIN] Retour de " + pageUtilisateurs.size() + " utilisateurs sur " + utilisateursFiltres.size() + " total");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR: [ADMIN] Erreur dans getTous(): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/membresansprojet")
    public ResponseEntity<List<Utilisateur>> getTousLesUtilisateursSansProjet() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.obtenirTousLesUtilisateursSansProjet();
            return ResponseEntity.ok(utilisateurs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Utilisateur> creer(@Valid @RequestBody Utilisateur utilisateur) {
        try {
            String email = utilisateur.getEmail() == null ? null : utilisateur.getEmail().trim().toLowerCase();
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            if (utilisateurService.existeParEmail(email)) {
                return ResponseEntity.badRequest().build();
            }
            utilisateur.setEmail(email);

            Utilisateur utilisateurCree = utilisateurService.enregistrer(utilisateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurCree);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        try {
            if (!utilisateurService.existeParId(id)) {
                return ResponseEntity.notFound().build();
            }

            utilisateurService.supprimer(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email-disponible")
    public ResponseEntity<Boolean> emailDisponible(@RequestParam String email) {
        try {
            String emailNormalise = email == null ? "" : email.trim().toLowerCase();
            boolean disponible = !utilisateurService.existeParEmail(emailNormalise);
            return ResponseEntity.ok(disponible);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}