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
 * Configuration de la sécurité Spring Boot pour CollabPro
 *
 * - Active le filtrage JWT
 * - Définit les accès par rôle
 * - Configure la session en mode stateless
 * - Autorise les routes publiques (visiteur)
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @since 2025-07-18
 * @see "Cahier des charges - F2 : Authentification par rôles"
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
     * Définition du filtre de sécurité.
     *
     * @param http Configuration HTTP
     * @return SecurityFilterChain
     * @throws Exception en cas d’erreur
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques (Visiteur)
                        .requestMatchers("/", "/ws/**").permitAll() // temporaire
                        .requestMatchers("/ws-native/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/projets/publics").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projets").permitAll() // F3: Consulter projets publics

                        // Routes selon rôles du cahier des charges
                        .requestMatchers("/api/administrateur/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers("/api/chef-projet/**").hasRole("CHEF_PROJET")
                        .requestMatchers("/api/membre/**").hasRole("MEMBRE")

                        // Toute autre requête nécessite une authentification
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
