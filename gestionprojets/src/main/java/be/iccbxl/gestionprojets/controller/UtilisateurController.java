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

/**
 * Contrôleur REST pour la gestion des utilisateurs.
 *
 * Implémente les fonctionnalités définies dans le cahier des charges :
 * - F1 : S'inscrire (endpoint public pour les Visiteurs)
 * - F4 : Consulter son profil (membres authentifiés)
 * - F5 : Mettre à jour son profil (membres authentifiés)
 * - F8 : Recherche d'utilisateurs pour ajouter des membres
 *
 * CORRIGÉ : hasAuthority() au lieu de hasRole() pour compatibilité avec JwtFilter
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.1
 * @see "Cahier des charges - Fonctionnalités F1, F4, F5, F8"
 */
@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // F1 : S'INSCRIRE

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
        System.out.println("inscriptionDTO: " + inscriptionDTO);

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
                .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

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
                .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

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

    // F8 : RECHERCHE D'UTILISATEURS (pour ajouter des membres)

    /**
     * Recherche d'utilisateurs pour F8 - Ajouter des membres à un projet.
     *
     * Fonctionnalité F8 : Ajouter des membres à un projet
     * Utilisateurs : Chef de Projet
     * Contraintes : Membres existants
     *
     * Permet de rechercher des utilisateurs (VISITEUR, MEMBRE, CHEF_PROJET)
     * par nom, prénom ou email pour les inviter dans un projet.
     *
     * @param q Le terme de recherche
     * @return Liste des utilisateurs correspondant à la recherche
     * @see "Cahier des charges - F8: Ajouter des membres à un projet"
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<Utilisateur>> rechercherUtilisateurs(@RequestParam String q) {
        try {
            System.out.println("DEBUG: [F8] Recherche utilisateurs avec terme: " + q);

            List<Utilisateur> utilisateurs = utilisateurService.obtenirTousLesUtilisateursSansProjet()
                    .stream()
                    .filter(u ->
                            u.getNom().toLowerCase().contains(q.toLowerCase()) ||
                                    u.getPrenom().toLowerCase().contains(q.toLowerCase()) ||
                                    u.getEmail().toLowerCase().contains(q.toLowerCase())
                    )
                    .collect(Collectors.toList());

            System.out.println("DEBUG: [F8] " + utilisateurs.size() + " utilisateurs trouvés");
            return ResponseEntity.ok(utilisateurs);

        } catch (Exception e) {
            System.err.println("ERROR: [F8] Erreur recherche utilisateurs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<Utilisateur>> getTous() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.obtenirTous();
            return ResponseEntity.ok(utilisateurs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtient tous les utilisateurs disponibles pour F8.
     * Utilisé pour découvrir des utilisateurs à ajouter aux projets.
     */
    @GetMapping("/membresansprojet")
    public ResponseEntity<List<Utilisateur>> getTousLesUtilisateursSansProjet() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.obtenirTousLesUtilisateursSansProjet();
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
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")  // CORRIGÉ
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
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")  // CORRIGÉ
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