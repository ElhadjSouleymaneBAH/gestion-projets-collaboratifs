package be.iccbxl.gestionprojets.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre JWT pour l'authentification et l'autorisation
 * CORRECTION : Suppression du préfixe "ROLE_" pour compatibilité avec hasAuthority()
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Routes publiques - pas de vérification JWT
        String path = request.getServletPath();
        if (path.startsWith("/api/auth/") || path.startsWith("/api/public/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token depuis le header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = null;

        // Extraction du username depuis le token JWT
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Token invalide, on continue sans authentification
            filterChain.doFilter(request, response);
            return;
        }

        // Authentification si username valide et pas déjà authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.validateToken(token)) {
                String role = jwtService.extractRole(token);

                // CORRECTION : Sans préfixe "ROLE_" pour correspondre à hasAuthority()
                // ANCIEN : new SimpleGrantedAuthority("ROLE_" + role);
                // NOUVEAU : new SimpleGrantedAuthority(role);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                List<SimpleGrantedAuthority> authorities = List.of(authority);

                // Création de l'objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Mise à jour du contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuation de la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}