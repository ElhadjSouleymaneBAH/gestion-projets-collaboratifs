package be.iccbxl.gestionprojets.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTOs pour les notifications
 * Architecture simple, cohérente avec InscriptionDTO et UtilisateurDTO
 * Conforme au cahier des charges - Gestion de Projets Collaboratifs
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
public class NotificationDTO {

    /**
     * DTO pour afficher une notification (F4 - Consulter son profil)
     * Pattern identique à UtilisateurDTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationResponse {
        private Long id;

        @NotBlank(message = "Le message est obligatoire")
        @Size(max = 255, message = "Le message ne peut pas dépasser 255 caractères")
        private String message;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime date;

        private Boolean lu;

        private String utilisateurEmail;
        private Long utilisateurId;
    }

    /**
     * DTO pour créer une notification simple
     * Pattern identique à InscriptionDTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateNotificationRequest {
        @NotNull(message = "L'ID utilisateur est obligatoire")
        private Long utilisateurId;

        @NotBlank(message = "Le message est obligatoire")
        @Size(max = 255, message = "Le message ne peut pas dépasser 255 caractères")
        private String message;
    }
}