package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.InscriptionDTO;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.enums.Role;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.security.JwtService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour l'authentification et l'inscription des utilisateurs.
 *
 * Implémente les fonctionnalités F1 (S'inscrire) et F2 (Se connecter) du cahier des charges.
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UtilisateurService utilisateurService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UtilisateurRepository utilisateurRepository,
                          UtilisateurService utilisateurService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Authentifie un utilisateur existant.
     * Fonctionnalité F2 : Se connecter
     *
     * @param request Données de connexion (email et mot de passe)
     * @return Token JWT et informations utilisateur si succès, erreur sinon
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        System.out.println("DEBUG: Tentative de connexion pour: " + request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );

            Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            String token = jwtService.generateToken(request.getEmail(), utilisateur.getRole().name());

            System.out.println("DEBUG: Connexion réussie pour: " + request.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, utilisateur));

        } catch (AuthenticationException e) {
            System.out.println("ERROR: Échec de connexion pour: " + request.getEmail() + " - " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Identifiants invalides");
            errorResponse.put("error", "AUTHENTICATION_FAILED");

            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    /**
     * Inscrit un nouvel utilisateur dans le système.
     * Fonctionnalité F1 : S'inscrire
     *
     * Valide les données, vérifie l'unicité de l'email et crée le compte utilisateur
     * avec le rôle MEMBRE par défaut. L'acceptation des CGU est obligatoire (RGPD).
     *
     * @param inscriptionDTO Données d'inscription validées
     * @return Confirmation de création si succès, erreur détaillée sinon
     */
    @PostMapping("/inscription")
    public ResponseEntity<?> sInscrire(@Valid @RequestBody InscriptionDTO inscriptionDTO) {
        System.out.println("DEBUG: Début inscription pour: " + inscriptionDTO.getEmail());
        System.out.println("inscriptionDTO: " + inscriptionDTO.toString());

        try {
            // Validation CGU obligatoire (contrainte RGPD)
            if (!inscriptionDTO.isCguAccepte()) {
                System.out.println("ERROR: CGU non acceptées pour: " + inscriptionDTO.getEmail());

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "L'acceptation des CGU est obligatoire pour la conformité RGPD");
                errorResponse.put("error", "CGU_NOT_ACCEPTED");

                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Vérification email unique
            if (utilisateurService.existeParEmail(inscriptionDTO.getEmail())) {
                System.out.println("ERROR: Email déjà existant: " + inscriptionDTO.getEmail());

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Un compte existe déjà avec cette adresse email");
                errorResponse.put("error", "EMAIL_ALREADY_EXISTS");

                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Création du compte utilisateur (F1)
            Utilisateur nouvelUtilisateur = utilisateurService.inscrire(inscriptionDTO);

            System.out.println("SUCCESS: Inscription réussie pour: " + inscriptionDTO.getEmail() + " avec ID: " + nouvelUtilisateur.getId());

            // Créer une réponse structurée pour le frontend
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            successResponse.put("success", true);
            successResponse.put("userId", nouvelUtilisateur.getId());
            successResponse.put("email", nouvelUtilisateur.getEmail());
            successResponse.put("nom", nouvelUtilisateur.getNom());
            successResponse.put("prenom", nouvelUtilisateur.getPrenom());

            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);

        } catch (RuntimeException e) {
            System.out.println("ERROR: Erreur métier lors de l'inscription: " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "BUSINESS_ERROR");

            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            System.out.println("ERROR: Erreur technique lors de l'inscription: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur technique lors de l'inscription. Veuillez réessayer.");
            errorResponse.put("error", "TECHNICAL_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * DTO pour les données de connexion.
     */
    public static class AuthRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String motDePasse;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    }

    /**
     * Réponse d'authentification contenant le token JWT et les informations utilisateur.
     */
    public static class AuthResponse {
        private final String token;
        private final String type = "Bearer";
        private final UserInfo user;

        public AuthResponse(String token, Utilisateur utilisateur) {
            this.token = token;
            this.user = new UserInfo(utilisateur);
        }

        public String getToken() { return token; }
        public String getType() { return type; }
        public UserInfo getUser() { return user; }

        /**
         * Informations utilisateur pour la réponse d'authentification.
         */
        public static class UserInfo {
            private final Long id;
            private final String nom;
            private final String prenom;
            private final String email;
            private final Role role;

            public UserInfo(Utilisateur utilisateur) {
                this.id = utilisateur.getId();
                this.nom = utilisateur.getNom();
                this.prenom = utilisateur.getPrenom();
                this.email = utilisateur.getEmail();
                this.role = utilisateur.getRole();
            }

            public Long getId() { return id; }
            public String getNom() { return nom; }
            public String getPrenom() { return prenom; }
            public String getEmail() { return email; }
            public Role getRole() { return role; }
        }
    }
}