package be.iccbxl.gestionprojets.model;

import be.iccbxl.gestionprojets.enums.TypeMessage;
import be.iccbxl.gestionprojets.enums.StatutMessage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité Message pour F9 : Collaboration en temps réel
 *
 * Stocke les messages échangés dans les projets entre :
 * - Membres du projet
 * - Chef de projet et équipe
 *
 * Fonctionnalités :
 * - Chat temps réel par projet
 * - Historique des conversations
 * - Messages avec timestamp
 * - Association utilisateur/projet
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @since 2025-07-14
 * @see "Cahier des charges - F9: Collaborer en temps réel"
 */
@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {

    /**
     * Identifiant unique du message.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Contenu textuel du message.
     * Obligatoire, maximum 1000 caractères.
     */
    @Column(name = "contenu", nullable = false, length = 1000)
    private String contenu;

    /**
     * Date et heure d'envoi du message.
     * Automatiquement définie lors de la création.
     */
    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    /**
     * Type de message avec enum pour plus de sécurité.
     * Valeurs : TEXT, NOTIFICATION, SYSTEM
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private TypeMessage type;

    /**
     * Statut du message avec enum pour plus de sécurité.
     * Valeurs : ENVOYE, DELIVRE, LU
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20)
    private StatutMessage statut;

    /**
     * Date de création (audit).
     */
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    /**
     * Date de modification (audit).
     */
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // ========== RELATIONS JPA ==========

    /**
     * Utilisateur qui a envoyé le message.
     * Relation Many-to-One avec Utilisateur.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    /**
     * Projet dans lequel le message a été envoyé.
     * Relation Many-to-One avec Projet.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    // ========== CONSTRUCTEURS ==========

    /**
     * Constructeur pour créer un nouveau message.
     *
     * @param contenu Le contenu du message (obligatoire)
     * @param utilisateur L'expéditeur (obligatoire)
     * @param projet Le projet de destination (obligatoire)
     */
    public Message(String contenu, Utilisateur utilisateur, Projet projet) {
        this.contenu = contenu;
        this.utilisateur = utilisateur;
        this.projet = projet;
        this.dateEnvoi = LocalDateTime.now();
        this.type = TypeMessage.TEXT;
        this.statut = StatutMessage.ENVOYE;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Constructeur complet pour les messages système.
     *
     * @param contenu Le contenu du message
     * @param utilisateur L'expéditeur
     * @param projet Le projet
     * @param type Le type de message
     */
    public Message(String contenu, Utilisateur utilisateur, Projet projet, TypeMessage type) {
        this.contenu = contenu;
        this.utilisateur = utilisateur;
        this.projet = projet;
        this.type = type;
        this.dateEnvoi = LocalDateTime.now();
        this.statut = StatutMessage.ENVOYE;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    // ========== MÉTHODES DU CYCLE DE VIE JPA ==========

    /**
     * Méthode appelée automatiquement avant la persistance.
     * Initialise les valeurs par défaut.
     */
    @PrePersist
    protected void onCreate() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now();
        }
        if (type == null) {
            type = TypeMessage.TEXT;
        }
        if (statut == null) {
            statut = StatutMessage.ENVOYE;
        }
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (dateModification == null) {
            dateModification = LocalDateTime.now();
        }
    }

    /**
     * Méthode appelée automatiquement avant la mise à jour.
     * Met à jour la date de modification.
     */
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // ========== MÉTHODES MÉTIER ==========

    /**
     * Marque le message comme lu.
     * Utilisé pour les notifications de lecture.
     */
    public void marquerCommeLu() {
        this.statut = StatutMessage.LU;
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Vérifie si le message est un message système.
     *
     * @return true si le type est SYSTEM ou NOTIFICATION
     */
    public boolean estMessageSysteme() {
        return TypeMessage.SYSTEM.equals(this.type) || TypeMessage.NOTIFICATION.equals(this.type);
    }

    /**
     * Vérifie si le message peut être supprimé par l'utilisateur.
     *
     * @param utilisateurActuel L'utilisateur demandant la suppression
     * @return true si l'utilisateur peut supprimer ce message
     */
    public boolean peutEtreSupprimePar(Utilisateur utilisateurActuel) {
        return this.utilisateur.equals(utilisateurActuel) ||
                !estMessageSysteme();
    }

    /**
     * Formate le message pour l'affichage.
     *
     * @return String formatée pour l'interface utilisateur
     */
    public String formaterPourAffichage() {
        return String.format("[%s] %s: %s",
                dateEnvoi.toString(),
                utilisateur.getNom(),
                contenu);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Représentation textuelle du message.
     *
     * @return String contenant les informations principales
     */
    @Override
    public String toString() {
        return String.format("Message{id=%d, contenu='%s', utilisateur=%s, projet=%s, dateEnvoi=%s}",
                id, contenu != null && contenu.length() > 50 ? contenu.substring(0, 50) + "..." : contenu,
                utilisateur != null ? utilisateur.getEmail() : "N/A",
                projet != null ? projet.getTitre() : "N/A",
                dateEnvoi);
    }

    /**
     * Comparaison d'égalité basée sur l'ID.
     * Utilise Objects.equals() pour éviter les NullPointerException.
     *
     * @param obj Objet à comparer
     * @return true si les messages sont identiques
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Message message = (Message) obj;
        return Objects.equals(id, message.id);
    }

    /**
     * Code de hachage basé sur l'ID.
     * Utilise Objects.hashCode() pour gérer les valeurs null.
     *
     * @return Hash code du message
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}