package be.iccbxl.gestionprojets.config;

import be.iccbxl.gestionprojets.security.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * Sécurisation des connexions WebSocket (STOMP) avec JWT.
 *  Objectif :
 *  - Intercepter la commande CONNECT envoyée par le client STOMP.
 *  - Extraire le token JWT du header "Authorization".
 *  - Valider le token et injecter l’utilisateur dans le contexte Spring Security.
 * Fonctionne en complément de JwtFilter pour les requêtes HTTP REST.
 * Ce fichier résout l’erreur "Session closed." visible dans la console.
 *
 * @author
 *   Elhadj Souleymane BAH
 * @version 1.0
 */
@Configuration
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    public WebSocketSecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Intercepteur STOMP appliqué au canal d’entrée WebSocket.
     * Vérifie et authentifie le token JWT lors du handshake STOMP (CONNECT).
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                // Seule la commande CONNECT est concernée
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        try {
                            String token = authHeader.substring(7);
                            String username = jwtService.extractUsername(token);

                            if (jwtService.validateToken(token)) {
                                String role = jwtService.extractRole(token);

                                // Création de l’objet d’authentification
                                UsernamePasswordAuthenticationToken userAuth =
                                        new UsernamePasswordAuthenticationToken(
                                                username,
                                                null,
                                                List.of(new SimpleGrantedAuthority(role))
                                        );

                                // Injection dans le contexte de sécurité
                                SecurityContextHolder.getContext().setAuthentication(userAuth);

                                // Association à la session WebSocket
                                accessor.setUser(userAuth);

                                System.out.println(" [WS-SECURITY] Connexion WebSocket authentifiée pour : " + username);
                            } else {
                                System.err.println(" [WS-SECURITY] Token invalide reçu via WebSocket");
                            }
                        } catch (Exception e) {
                            System.err.println(" [WS-SECURITY] Erreur validation JWT WebSocket : " + e.getMessage());
                        }
                    } else {
                        System.err.println(" [WS-SECURITY] Aucun header Authorization trouvé dans la connexion STOMP");
                    }
                }

                return message;
            }
        });
    }
}
