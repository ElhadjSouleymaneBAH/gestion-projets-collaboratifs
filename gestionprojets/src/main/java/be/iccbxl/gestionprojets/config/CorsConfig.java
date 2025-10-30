package be.iccbxl.gestionprojets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://localhost:3000",
                "https://gestion-projets-collaboratifs.vercel.app",
                "https://gestion-projets-collab-git-4c7a64-elhadsouleymanebahs-projects.vercel.app",
                "https://gestion-projets-collaboratifs-production.up.railway.app",
                "https://gestion-projets-collaboratifs-1cz3v6vkc.vercel.app",
                "https://gestion-projets-collaboratifs-middp13nb.vercel.app",
                "https://*.vercel.app"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
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
                        "https://gestion-projets-collaboratifs-production.up.railway.app",
                        "https://gestion-projets-collaboratifs-1cz3v6vkc.vercel.app",
                        "https://gestion-projets-collaboratifs-middp13nb.vercel.app",
                        "https://*.vercel.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}