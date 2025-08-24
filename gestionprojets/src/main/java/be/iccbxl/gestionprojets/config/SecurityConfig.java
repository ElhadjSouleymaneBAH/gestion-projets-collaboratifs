package be.iccbxl.gestionprojets.config;

import be.iccbxl.gestionprojets.security.JwtFilter;
import be.iccbxl.gestionprojets.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration de sécurité Spring Boot - VERSION PRODUCTION
 * Fonctionnalités ordonnées F1 à F14 selon le cahier des charges
 *
 * ORDRE CRITIQUE: Du plus spécifique au plus générique pour éviter les conflits
 *
 * @author ElhadjSouleymaneBAH
 * @version 3.0 - PRODUCTION
 * @see "Cahier des charges - Application Web de Gestion de Projets Collaboratifs"
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtFilter jwtFilter,
                          CustomUserDetailsService userDetailsService,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * Configuration des accès par fonctionnalité - ORDRE PRODUCTION
     * Respecte strictement le cahier des charges et l'ordre des URLs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configuration CORS et CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // ===================================================================
                        // ROUTES SYSTÈME ET INFRASTRUCTURE (Plus spécifiques en premier)
                        // ===================================================================

                        // Préflights CORS
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Routes d'erreur et techniques

                        .requestMatchers("/error", "/favicon.ico", "/actuator/**").permitAll()
                        .requestMatchers("/", "/static/**", "/assets/**", "/public/**").permitAll()

                        // ===================================================================
                        // FONCTIONNALITÉ F1 : S'INSCRIRE (Visiteur)
                        // Accès public pour création de compte
                        // ===================================================================


                        .requestMatchers(HttpMethod.POST, "/api/auth/inscription").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs/inscription").permitAll()

                        // ===================================================================
                        // FONCTIONNALITÉ F2 : SE CONNECTER (Visiteur)
                        // Accès public pour authentification
                        // ===================================================================

                        .requestMatchers(HttpMethod.POST, "/api/auth/connexion").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()

                        // ===================================================================
                        // FONCTIONNALITÉ F3 : CONSULTER PROJETS PUBLICS (Visiteur)
                        // Lecture seule pour visiteurs non connectés
                        // ===================================================================

                        .requestMatchers(HttpMethod.GET, "/api/projets/publics").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projets/public/**").permitAll()

                        // ===================================================================
                        // FONCTIONNALITÉ F14 : MULTILINGUE (Tous)
                        // Services de traduction accessibles à tous
                        // ===================================================================

                        .requestMatchers("/api/public/translations/**").permitAll()
                        .requestMatchers("/api/public/locale/**").permitAll()

                        // ===================================================================
                        // ROUTES ADMINISTRATEUR (Plus spécifiques - avant routes génériques)
                        // Accès exclusif ADMINISTRATEUR
                        // ===================================================================

                        // Gestion globale des utilisateurs
                        .requestMatchers("/api/admin/utilisateurs/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/users/**").hasAuthority("ADMINISTRATEUR")

                        // Gestion globale des abonnements et paiements (F10 + F11 Admin)
                        .requestMatchers("/api/admin/abonnements/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/paiements/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/transactions/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/factures/**").hasAuthority("ADMINISTRATEUR")

                        // Statistiques et rapports globaux
                        .requestMatchers("/api/admin/stats/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/rapports/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/analytics/**").hasAuthority("ADMINISTRATEUR")

                        // Gestion système
                        .requestMatchers("/api/admin/systeme/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/config/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/**").hasAuthority("ADMINISTRATEUR")

                        // ===================================================================
                        // ROUTES CHEF DE PROJET (Abonnés premium)
                        // Accès exclusif CHEF_PROJET + ADMINISTRATEUR
                        // ===================================================================

                        // F6 : Gérer projets (Création, modification, suppression)
                        .requestMatchers(HttpMethod.POST, "/api/projets").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/projets/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/projets/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PATCH, "/api/projets/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // F7 : Gérer tâches (Création et validation par Chef de projet)
                        .requestMatchers(HttpMethod.POST, "/api/taches").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/taches/*/valider").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/taches/*/statut").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/taches/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // F8 : Ajouter membres aux projets (Gestion équipe)
                        .requestMatchers(HttpMethod.POST, "/api/projets/*/membres").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/projets/*/membres/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/projets/*/membres/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // F10 : Paiements et abonnements (Chef de projet peut gérer ses abonnements)
                        .requestMatchers("/api/paiements/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/abonnements/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/transactions/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/stripe/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // F11 : Générer factures (Chef de projet peut voir ses factures)
                        .requestMatchers(HttpMethod.GET, "/api/factures/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.POST, "/api/factures/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // Fonctionnalités premium chef de projet
                        .requestMatchers("/api/chef-projet/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // FONCTIONNALITÉ F4 & F5 : PROFIL UTILISATEUR
                        // Consultation et modification profil (MEMBRE minimum)
                        // ===================================================================

                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/profil").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/profil").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PATCH, "/api/utilisateurs/profil").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/utilisateurs/me").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // FONCTIONNALITÉ F6 : CONSULTER PROJETS (Membres authentifiés)
                        // Lecture projets auxquels l'utilisateur participe
                        // ===================================================================

                        .requestMatchers(HttpMethod.GET, "/api/projets").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.GET, "/api/projets/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // FONCTIONNALITÉ F7 : CONSULTER ET MODIFIER TÂCHES
                        // Membres peuvent voir et modifier leurs tâches assignées
                        // ===================================================================

                        .requestMatchers(HttpMethod.GET, "/api/taches").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.GET, "/api/taches/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/taches/*/terminer").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/taches/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PATCH, "/api/taches/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // FONCTIONNALITÉ F9 & F12 : COLLABORATION TEMPS RÉEL
                        // Messages, commentaires, WebSocket pour tous les membres
                        // ===================================================================

                        // WebSocket collaboration
                        .requestMatchers("/ws/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/websocket/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // Messages de projet
                        .requestMatchers("/api/messages/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/chat/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // Commentaires sur tâches/projets (F12)
                        .requestMatchers("/api/commentaires/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/comments/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // FONCTIONNALITÉ F13 : NOTIFICATIONS
                        // Système de notifications pour tous les membres authentifiés
                        // ===================================================================

                        .requestMatchers("/api/notifications/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // ROUTES MEMBRES (Fonctionnalités de base)
                        // ===================================================================

                        .requestMatchers("/api/membre/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // ROUTES GÉNÉRIQUES UTILISATEURS (Moins spécifiques)
                        // ===================================================================

                        .requestMatchers("/api/utilisateurs/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/users/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // SÉCURITÉ PAR DÉFAUT
                        // Toute autre requête nécessite une authentification minimum
                        // ===================================================================

                        .anyRequest().authenticated()
                )

                // Configuration session et filtres
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Force 12 pour production
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false); // Pour debug en dev, true en prod
        return provider;
    }
}