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
 * Configuration de sécurité Spring Boot
 * Fonctionnalités ordonnées F1 à F14
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS / CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // ===================================================================
                        // ROUTES SYSTÈME ET INFRASTRUCTURE
                        // ===================================================================
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error", "/favicon.ico", "/actuator/**").permitAll()
                        .requestMatchers("/", "/static/**", "/assets/**", "/public/**").permitAll()

                        //  Routes publiques front-end (accessibles sans JWT)
                        .requestMatchers(
                                "/index.html",
                                "/conditions",
                                "/politique-confidentialite",
                                "/connexion",
                                "/inscription",
                                "/api/public/**"
                        ).permitAll()

                        // ===================================================================
                        // F1 : INSCRIPTION
                        // ===================================================================
                        .requestMatchers(HttpMethod.POST, "/api/auth/inscription").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs/inscription").permitAll()

                        // ===================================================================
                        // F2 : CONNEXION
                        // ===================================================================
                        .requestMatchers(HttpMethod.POST, "/api/auth/connexion").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()

                        // ===================================================================
                        // F3 : PROJETS PUBLICS
                        // ===================================================================
                        .requestMatchers(HttpMethod.GET, "/api/projets/publics").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projets/public/**").permitAll()

                        // ===================================================================
                        // F14 : MULTILINGUE
                        // ===================================================================
                        .requestMatchers("/api/public/translations/**").permitAll()
                        .requestMatchers("/api/public/locale/**").permitAll()

                        // ===================================================================
                        // ADMIN
                        // ===================================================================
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/utilisateurs/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/**").hasAuthority("ADMINISTRATEUR")

                        // ===================================================================
                        // F10 : PAIEMENTS ET ABONNEMENTS
                        // ===================================================================
                        .requestMatchers("/api/paiements/**", "/api/abonnements/**",
                                "/api/transactions/**", "/api/stripe/**")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // F11 : FACTURES
                        // ===================================================================
                        .requestMatchers("/api/factures/**")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // ACCÈS MEMBRE EN LECTURE (conformité CdC F7/F9/F12)
                        // ===================================================================
                        .requestMatchers(HttpMethod.GET,
                                "/api/projets/utilisateur/**",
                                "/api/taches/utilisateur/**")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/projets/*",
                                "/api/projets/*/membres/**")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        .requestMatchers(HttpMethod.POST, "/api/taches")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // F6-F8 : CHEF DE PROJET (gestion complète)
                        // ===================================================================
                        .requestMatchers("/api/projets/**", "/api/taches/**", "/api/chef-projet/**")
                        .hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // F4-F5 : PROFIL
                        // ===================================================================
                        .requestMatchers("/api/utilisateurs/profil", "/api/utilisateurs/me")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // F9-F12 : COLLABORATION TEMPS RÉEL
                        // ===================================================================
                        .requestMatchers("/ws/**", "/ws-native/**", "/topic/**", "/queue/**").permitAll()
                        .requestMatchers("/api/messages/**", "/api/chat/**",
                                "/api/commentaires/**", "/api/comments/**")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // F13 : NOTIFICATIONS
                        // ===================================================================
                        .requestMatchers("/api/notifications/**")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // ROUTES MEMBRES
                        // ===================================================================
                        .requestMatchers("/api/membre/**")
                        .hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ===================================================================
                        // PAR DÉFAUT
                        // ===================================================================
                        .anyRequest().authenticated()
                )

                // Session stateless + filtres
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
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }
}
