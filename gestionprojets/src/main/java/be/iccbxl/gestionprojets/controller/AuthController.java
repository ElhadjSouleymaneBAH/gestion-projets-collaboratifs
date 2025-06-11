package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.enums.Role;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.security.JwtService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UtilisateurRepository utilisateurRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );

            Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            String token = jwtService.generateToken(request.getEmail(), utilisateur.getRole().name());
            return ResponseEntity.ok(new AuthResponse(token, utilisateur));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Identifiants invalides");
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
                this.role = utilisateur.getRole(); // Maintenant ça marche !
            }

            public Long getId() { return id; }
            public String getNom() { return nom; }
            public String getPrenom() { return prenom; }
            public String getEmail() { return email; }
            public Role getRole() { return role; }
        }
    }
}