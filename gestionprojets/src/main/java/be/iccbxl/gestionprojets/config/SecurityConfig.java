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

/**
 * Configuration de la sécurité Spring Boot pour Gestion de Projets Collaboratifs
 * Couvre TOUTES les fonctionnalités F1-F12 du cahier des charges
 * CORRIGÉ : Ajout de la route /error pour éviter les 403 sur les erreurs de paramètres
 *
 * @author ElhadjSouleymaneBAH
 * @version 2.1
 * @see "Cahier des charges - Fonctionnalités F1 à F12"
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, CustomUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configuration complète des accès par fonctionnalité (F1-F12)
     * Respecte strictement le cahier des charges et les rôles définis
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ========== ROUTES PUBLIQUES (VISITEUR) ==========

                        // F1 : S'inscrire - Accès public pour permettre la création de compte
                        .requestMatchers("/api/utilisateurs/inscription").permitAll()

                        // F2 : Se connecter - Accès public pour l'authentification
                        .requestMatchers("/api/auth/**").permitAll()

                        // CORRECTION : Route d'erreur Spring Boot (évite 403 sur erreurs de paramètres)
                        .requestMatchers("/error").permitAll()

                        // F3 : Consulter projets publics - Accès libre aux visiteurs
                        .requestMatchers("/api/projets/publics").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projets").permitAll()

                        // F9 : Collaboration temps réel - WebSocket accessible
                        .requestMatchers("/ws/**", "/ws-native/**").permitAll()

                        // F12 : Multilingue - Ressources publiques sans restriction
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/", "/static/**", "/favicon.ico").permitAll()

                        // ========== ROUTES AUTHENTIFIÉES PAR RÔLE ==========

                        // F4 & F5 : Consulter et modifier son profil (MEMBRE minimum)
                        .requestMatchers("/api/utilisateurs/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // F6 : Gérer projets
                        // - Consultation : MEMBRE peut voir les projets auxquels il participe
                        // - Gestion complète : CHEF_PROJET et ADMINISTRATEUR
                        .requestMatchers(HttpMethod.GET, "/api/projets/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/projets/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // F7 : Gérer tâches
                        // - Consultation : MEMBRE peut voir ses tâches assignées
                        // - Gestion complète : CHEF_PROJET peut créer/modifier/assigner
                        .requestMatchers(HttpMethod.GET, "/api/taches/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/taches/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // F8 : Ajouter membres aux projets (inclus dans F6)
                        // Géré via les endpoints de projets ci-dessus

                        // F9 : Collaboration en temps réel
                        // - Commentaires sur tâches : Tous les membres authentifiés
                        .requestMatchers("/api/commentaires/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        // - Messages projet : Tous les membres du projet
                        .requestMatchers("/api/messages/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // F10 : Paiements et abonnements (CHEF_PROJET obligatoire selon cahier des charges)
                        .requestMatchers("/api/paiements/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/abonnements/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/transactions/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // F11 : Générer factures (automatique après paiement - CHEF_PROJET)
                        .requestMatchers("/api/factures/**").hasAnyAuthority("CHEF_PROJET", "ADMINISTRATEUR")

                        // NOTIFICATIONS (Support F4, F6, F7, F8, F10, F11)
                        // Tous les utilisateurs authentifiés peuvent recevoir des notifications
                        .requestMatchers("/api/notifications/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ========== ROUTES ADMINISTRATEUR SPÉCIALISÉES ==========

                        // Gestion globale des utilisateurs (promotion, gestion des rôles)
                        .requestMatchers("/api/admin/utilisateurs/**").hasAuthority("ADMINISTRATEUR")

                        // Gestion globale des abonnements et paiements
                        .requestMatchers("/api/admin/abonnements/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/factures/**").hasAuthority("ADMINISTRATEUR")

                        // Statistiques et rapports globaux
                        .requestMatchers("/api/admin/stats/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/admin/rapports/**").hasAuthority("ADMINISTRATEUR")

                        // Routes administrateur générales
                        .requestMatchers("/api/admin/**").hasAuthority("ADMINISTRATEUR")
                        .requestMatchers("/api/administrateur/**").hasAuthority("ADMINISTRATEUR")

                        // ========== ROUTES CHEF DE PROJET SPÉCIALISÉES ==========

                        // Fonctionnalités premium accessibles uniquement aux chefs de projet abonnés
                        .requestMatchers("/api/chef-projet/projets/**").hasAuthority("CHEF_PROJET")
                        .requestMatchers("/api/chef-projet/equipes/**").hasAuthority("CHEF_PROJET")
                        .requestMatchers("/api/chef-projet/rapports/**").hasAuthority("CHEF_PROJET")

                        // ========== ROUTES MEMBRE SPÉCIALISÉES ==========

                        // Fonctionnalités accessibles aux membres (consultation, participation)
                        .requestMatchers("/api/membre/taches/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")
                        .requestMatchers("/api/membre/projets/**").hasAnyAuthority("MEMBRE", "CHEF_PROJET", "ADMINISTRATEUR")

                        // ========== SÉCURITÉ PAR DÉFAUT ==========
                        // Toute autre requête nécessite une authentification minimum
                        .anyRequest().authenticated()
                )
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
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}