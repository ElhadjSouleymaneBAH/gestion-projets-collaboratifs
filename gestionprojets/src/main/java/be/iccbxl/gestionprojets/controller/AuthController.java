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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = {
                "http://localhost:5174",
                "https://gestion-projets-collaboratifs-9goo9m1nl.vercel.app"
        },
        allowCredentials = "true"
)

public class AuthController {
    private final UtilisateurService utilisateurService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UtilisateurRepository utilisateurRepository,
                          UtilisateurService utilisateurService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/connexion")
    public ResponseEntity<?> connexion(@RequestBody AuthRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;
        String motDePasse = request.getMotDePasse();

        System.out.println("=== DEBUG CONNEXION ===");
        System.out.println("Email reçu: [" + request.getEmail() + "]");
        System.out.println("Email nettoyé: [" + email + "]");
        System.out.println("Mot de passe reçu: [" + (motDePasse != null ? "***" + motDePasse.length() + " chars" : "null") + "]");

        try {
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);

            if (utilisateurOpt.isEmpty()) {
                System.out.println(" ERREUR: Utilisateur non trouvé pour email: [" + email + "]");

                System.out.println(" Emails en base de données:");
                utilisateurRepository.findAll().forEach(u ->
                        System.out.println("  - [" + u.getEmail() + "] (ID: " + u.getId() + ")")
                );

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Email non trouvé. Créez d'abord un compte via 'S'inscrire'.");
                errorResponse.put("error", "USER_NOT_FOUND");
                errorResponse.put("email", email);

                return ResponseEntity.status(404).body(errorResponse);
            }

            Utilisateur utilisateur = utilisateurOpt.get();
            System.out.println(" Utilisateur trouvé: ID=" + utilisateur.getId() + ", Email=" + utilisateur.getEmail());
            System.out.println(" Hash en base: " + utilisateur.getMotDePasse().substring(0, 20) + "...");

            boolean motDePasseValide = passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse());
            System.out.println(" Vérification mot de passe: " + (motDePasseValide ? " VALIDE" : " INVALIDE"));

            if (!motDePasseValide) {
                System.out.println(" Mot de passe incorrect pour: " + email);

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Mot de passe incorrect");
                errorResponse.put("error", "INVALID_PASSWORD");

                return ResponseEntity.status(401).body(errorResponse);
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, motDePasse)
            );

            String token = jwtService.generateToken(email, utilisateur.getRole().name());

            System.out.println(" Connexion réussie pour: " + email);
            return ResponseEntity.ok(new AuthResponse(token, utilisateur));

        } catch (AuthenticationException e) {
            System.out.println(" Échec authentification Spring Security: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Identifiants invalides");
            errorResponse.put("error", "AUTHENTICATION_FAILED");
            errorResponse.put("details", e.getMessage());

            return ResponseEntity.status(401).body(errorResponse);

        } catch (Exception e) {
            System.out.println(" Erreur technique lors de la connexion: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur technique lors de la connexion");
            errorResponse.put("error", "TECHNICAL_ERROR");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/debug-user")
    public ResponseEntity<?> debugUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        System.out.println("=== DEBUG USER: " + email + " ===");

        Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("found", false, "email", email));
        }

        Utilisateur user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("found", true);
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("nom", user.getNom());
        response.put("prenom", user.getPrenom());
        response.put("role", user.getRole().name());
        response.put("passwordHash", user.getMotDePasse().substring(0, 20) + "...");

        boolean test1986 = passwordEncoder.matches("1986", user.getMotDePasse());
        response.put("passwordTest1986", test1986);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> sInscrire(@Valid @RequestBody InscriptionDTO inscriptionDTO) {
        System.out.println("DEBUG: Début inscription pour: " + inscriptionDTO.getEmail());

        try {
            if (!inscriptionDTO.isCguAccepte()) {
                System.out.println("ERROR: CGU non acceptées pour: " + inscriptionDTO.getEmail());

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "L'acceptation des CGU est obligatoire pour la conformité RGPD");
                errorResponse.put("error", "CGU_NOT_ACCEPTED");

                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (utilisateurService.existeParEmail(inscriptionDTO.getEmail())) {
                System.out.println("ERROR: Email déjà existant: " + inscriptionDTO.getEmail());

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Un compte existe déjà avec cette adresse email");
                errorResponse.put("error", "EMAIL_ALREADY_EXISTS");

                return ResponseEntity.badRequest().body(errorResponse);
            }

            Utilisateur nouvelUtilisateur = utilisateurService.inscrire(inscriptionDTO);

            System.out.println("SUCCESS: Inscription réussie pour: " + inscriptionDTO.getEmail() + " avec ID: " + nouvelUtilisateur.getId());

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            successResponse.put("success", true);
            successResponse.put("userId", nouvelUtilisateur.getId());
            successResponse.put("email", nouvelUtilisateur.getEmail());
            successResponse.put("nom", nouvelUtilisateur.getNom());
            successResponse.put("prenom", nouvelUtilisateur.getPrenom());

            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);

        } catch (Exception e) {
            System.out.println("ERROR: Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "INSCRIPTION_ERROR");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

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