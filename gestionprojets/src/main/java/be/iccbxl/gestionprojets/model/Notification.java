package be.iccbxl.gestionprojets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité Notification selon le diagramme de classes
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Boolean lu = false;

    // Relation avec Utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

    /**
     * Marquer comme lue
     */
    public void marquerCommeLue() {
        this.lu = true;
    }

    /**
     * Envoyer une notification
     */
    public boolean envoyerNotification(String message) {
        if (message != null && !message.trim().isEmpty()) {
            this.message = message;
            return true;
        }
        return false;
    }
}