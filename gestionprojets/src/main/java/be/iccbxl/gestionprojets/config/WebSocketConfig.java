package be.iccbxl.gestionprojets.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration WebSocket pour F9 : Collaboration en temps réel
 *
 * Configuration simplifiée sans conflit de beans
 * Spring Boot crée automatiquement SimpMessagingTemplate
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @since 2025-07-14
 * @see "Cahier des charges - F9: Collaborer en temps réel"
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configuration du broker de messages.
     *
     * Définit les destinations pour :
     * - /topic : Messages broadcast (public)
     * - /queue : Messages privés (utilisateur spécifique)
     * - /app : Endpoint pour recevoir les messages clients
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Activer le broker simple en mémoire
        config.enableSimpleBroker("/topic", "/queue");

        // Préfixe pour les messages envoyés depuis le client
        config.setApplicationDestinationPrefixes("/app");

        // Préfixe pour les messages privés utilisateur
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Configuration des endpoints STOMP.
     *
     * Définit les points d'entrée WebSocket :
     * - /ws : Endpoint principal WebSocket
     * - SockJS : Fallback pour navigateurs non compatibles
     * - CORS : Autorisation frontend Vue.js
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint principal WebSocket avec SockJS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // À restreindre en production
                .withSockJS(); // Fallback pour compatibilité navigateurs

        // Endpoint alternatif sans SockJS (WebSocket natif)
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");
    }

    
}