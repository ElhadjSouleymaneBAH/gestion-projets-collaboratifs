package be.iccbxl.gestionprojets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration CORS pour l'ensemble du projet
 * Vue.js (Vite) + Spring Boot + JWT + Stripe
 *
 * @author ElhadjSouleymaneBAH
 * @version FINALE
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🌐 Origines autorisées (local + production)
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://localhost:3000",
                "https://gestion-projets-collaboratifs.vercel.app",
                "https://gestion-projets-collab-git-4c7a64-elhadsouleymanebahs-projects.vercel.app",
                "https://gestion-projets-collaboratifs-production.up.railway.app"
        ));

        //  Méthodes HTTP autorisées
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        //  Headers autorisés
        configuration.addAllowedHeader("*");

        // 🔐 Autoriser les cookies et tokens JWT
        configuration.setAllowCredentials(true);

        // ⏱ Cache de la requête preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://localhost:3000",
                        "https://gestion-projets-collaboratifs.vercel.app",
                        "https://gestion-projets-collab-git-4c7a64-elhadsouleymanebahs-projects.vercel.app",
                        "https://gestion-projets-collaboratifs-production.up.railway.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
