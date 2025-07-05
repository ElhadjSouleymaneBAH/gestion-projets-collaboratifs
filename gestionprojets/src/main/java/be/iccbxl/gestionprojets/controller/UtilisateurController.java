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

/**
 * Contrôleur REST pour la gestion des utilisateurs.
 *
 * Implémente les fonctionnalités définies dans le cahier des charges :
 * - F1 : S'inscrire (endpoint public pour les Visiteurs)
 * - F4 : Consulter son profil (membres authentifiés)
 * - F5 : Mettre à jour son profil (membres authentifiés)
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @see "Cahier des charges - Fonctionnalités F1, F4, F5"
 */
@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // F1 : S'INSCRIRE (ENDPOINT PUBLIC)

    /**
     * Inscription publique d'un nouvel utilisateur.
     *
     * Fonctionnalité F1 : S'inscrire
     * Utilisateurs : Visiteur (accès public, sans authentification)
     * Importance : 5/5
     * Contraintes : Remplir un formulaire
     *
     * @param inscriptionDTO Données d'inscription
     * @return L'utilisateur créé ou message d'erreur
     * @see "Cahier des charges - F1: S'inscrire"
     */
    @PostMapping("/inscription")
    public ResponseEntity<?> sInscrire(@Valid @RequestBody InscriptionDTO inscriptionDTO) {
        try {
            // Validation CGU obligatoire (contrainte RGPD)
            if (!inscriptionDTO.isCguAccepte()) {
                return ResponseEntity.badRequest()
                        .body("L'acceptation des CGU est obligatoire pour la conformité RGPD");
            }

            // Vérification email unique
            if (utilisateurService.existeParEmail(inscriptionDTO.getEmail())) {
                return ResponseEntity.badRequest()
                        .body("Un compte existe déjà avec cette adresse email");
            }

            // Création du compte utilisateur (F1)
            Utilisateur nouvelUtilisateur = utilisateurService.inscrire(inscriptionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'inscription");
        }
    }

    //  F4 : CONSULTER SON PROFIL

    /**
     * Consultation du profil utilisateur.
     *
     * Fonctionnalité F4 : Consulter son profil
     * Utilisateurs : Membre (authentification requise)
     * Importance : 3/5
     * Contraintes : Connexion requise
     *
     * @param id L'identifiant de l'utilisateur
     * @param authentication Informations d'authentification
     * @return Le profil utilisateur ou erreur 403/404
     * @see "Cahier des charges - F4: Consulter son profil"
     */
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    //  F5 : METTRE À JOUR SON PROFIL

    /**
     * Mise à jour du profil utilisateur.
     *
     * Fonctionnalité F5 : Mettre à jour son profil
     * Utilisateurs : Membre (authentification requise)
     * Importance : 3/5
     * Contraintes : Format valide requis
     *
     * @param id L'identifiant de l'utilisateur
     * @param utilisateurModifie Les nouvelles données
     * @param authentication Informations d'authentification
     * @return Le profil mis à jour ou erreur 403/404
     * @see "Cahier des charges - F5: Mettre à jour son profil"
     */
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> mettreAJour(@PathVariable Long id,
                                                   @Valid @RequestBody Utilisateur utilisateurModifie,
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

    // ENDPOINTS ADMINISTRATEUR

    /**
     * Liste tous les utilisateurs du système.
     * Réservé aux administrateurs pour la gestion globale.
     *
     * @return Liste de tous les utilisateurs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<Utilisateur>> getTous() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.obtenirTous();
            return ResponseEntity.ok(utilisateurs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Création d'utilisateur par l'administrateur.
     * Différent de l'inscription publique (F1).
     *
     * @param utilisateur L'utilisateur à créer
     * @return L'utilisateur créé
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Utilisateur> creer(@Valid @RequestBody Utilisateur utilisateur) {
        try {
            // Vérification email unique
            if (utilisateurService.existeParEmail(utilisateur.getEmail())) {
                return ResponseEntity.badRequest().build();
            }

            Utilisateur utilisateurCree = utilisateurService.enregistrer(utilisateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurCree);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Suppression d'utilisateur par l'administrateur.
     *
     * @param id L'identifiant de l'utilisateur à supprimer
     * @return Confirmation de suppression
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
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

    // ENDPOINTS UTILITAIRES

    /**
     * Vérification de disponibilité d'email.
     * Utilisé pour validation en temps réel lors de l'inscription.
     *
     * @param email L'adresse email à vérifier
     * @return true si l'email est disponible
     */
    @GetMapping("/email-disponible")
    public ResponseEntity<Boolean> emailDisponible(@RequestParam String email) {
        try {
            boolean disponible = !utilisateurService.existeParEmail(email);
            return ResponseEntity.ok(disponible);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}