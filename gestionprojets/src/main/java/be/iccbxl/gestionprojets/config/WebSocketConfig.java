package be.iccbxl.gestionprojets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration WebSocket pour F9 : Collaboration en temps réel
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker simple + heartbeat pour stabilité (10s ping/pong)
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{10_000L, 10_000L})
                .setTaskScheduler(taskScheduler());

        // Préfixes d'application et destinataires utilisateurs
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Aligne les origines avec ta CorsConfig
        String[] allowed = new String[]{
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://localhost:3000",
                "https://*.vercel.app",
                "https://*.netlify.app"
        };

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(allowed)
                .withSockJS();

        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns(allowed);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("websocket-heartbeat-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.initialize();
        return scheduler;
    }
}