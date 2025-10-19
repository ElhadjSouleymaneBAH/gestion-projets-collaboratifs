package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.model.Notification;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.NotificationRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service simple pour la gestion des notifications
 * Architecture cohérente avec UtilisateurService
 * Conforme au cahier des charges
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    // ============================================================================
    // MÉTHODES DE BASE (pattern UtilisateurService)
    // ============================================================================

    /**
     * Créer une notification (F13) - Version simplifiée pour F12
     *
     * Conforme au cahier des charges :
     * "Permet d'envoyer et de recevoir des notifications automatiques
     *  lors des actions dans les projets (création de tâches, commentaires, modifications)"
     *
     * @param utilisateurId ID du destinataire
     * @param message Contenu de la notification
     * @return Notification créée
     */
    public Notification creerNotification(Long utilisateurId, String message) {
        log.debug("[F13] Création notification pour utilisateur {} : {}", utilisateurId, message);

        // Validation du message
        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("Le message de notification est obligatoire");
        }

        // Vérification de l'existence de l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + utilisateurId));

        // Création de la notification
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUtilisateur(utilisateur);
        notification.setDate(LocalDateTime.now());
        notification.setLu(false);

        Notification saved = notificationRepository.save(notification);
        log.info("[F13] ✅ Notification {} créée pour utilisateur {}", saved.getId(), utilisateurId);
        return saved;
    }

    /**
     * Récupérer toutes les notifications d'un utilisateur
     * Pattern identique à UtilisateurService.obtenirParId()
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUtilisateur(Long utilisateurId) {
        log.debug("Récupération notifications pour utilisateur {}", utilisateurId);
        return notificationRepository.findByUtilisateurIdOrderByDateDesc(utilisateurId);
    }

    /**
     * Compter les notifications non lues
     * Pattern similaire à UtilisateurService.existeParEmail()
     */
    @Transactional(readOnly = true)
    public long compterNotificationsNonLues(Long utilisateurId) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur.isEmpty()) {
            return 0;
        }
        return notificationRepository.countByUtilisateurAndLuFalse(utilisateur.get());
    }

    /**
     * Marquer une notification comme lue
     * Pattern similaire à UtilisateurService.enregistrer()
     */
    public void marquerCommeLue(Long notificationId, Long utilisateurId) {
        log.debug("Marquage notification {} comme lue pour utilisateur {}", notificationId, utilisateurId);

        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification non trouvée avec l'ID : " + notificationId);
        }

        Notification notification = notificationOpt.get();

        // Vérification sécurité (comme dans UtilisateurController)
        if (!notification.getUtilisateur().getId().equals(utilisateurId)) {
            throw new RuntimeException("Accès non autorisé à cette notification");
        }

        notification.marquerCommeLue();
        notificationRepository.save(notification);
    }

    /**
     * Supprimer une notification
     * Pattern identique à UtilisateurService.supprimer()
     */
    public void supprimerNotification(Long notificationId, Long utilisateurId) {
        log.debug("Suppression notification {} pour utilisateur {}", notificationId, utilisateurId);

        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification non trouvée avec l'ID : " + notificationId);
        }

        Notification notification = notificationOpt.get();

        // Vérification sécurité
        if (!notification.getUtilisateur().getId().equals(utilisateurId)) {
            throw new RuntimeException("Accès non autorisé à cette notification");
        }

        notificationRepository.delete(notification);
    }

    /**
     * Obtenir une notification par ID
     * Pattern similaire à UtilisateurService.obtenirParId()
     */
    @Transactional(readOnly = true)
    public Optional<Notification> obtenirParId(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    /**
     * Récupérer les notifications non lues (utilisée dans Controller)
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsNonLues(Long utilisateurId) {
        log.debug("Récupération notifications non lues pour utilisateur {}", utilisateurId);
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur.isEmpty()) {
            return List.of();
        }
        return notificationRepository.findByUtilisateurAndLuFalseOrderByDateDesc(utilisateur.get());
    }

    /**
     * Marquer toutes les notifications comme lues (utilisée dans Controller)
     */
    public int marquerToutesCommeLues(Long utilisateurId) {
        log.debug("Marquage toutes notifications comme lues pour utilisateur {}", utilisateurId);

        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur.isEmpty()) {
            return 0;
        }

        List<Notification> notifications = notificationRepository.findByUtilisateurAndLuFalseOrderByDateDesc(utilisateur.get());
        int count = 0;
        for (Notification notification : notifications) {
            notification.marquerCommeLue();
            notificationRepository.save(notification);
            count++;
        }

        return count;
    }

    /**
     * Notification de bienvenue (F1 - S'inscrire)
     */
    public Notification creerNotificationBienvenue(Long utilisateurId) {
        log.debug("Création notification bienvenue pour utilisateur {}", utilisateurId);

        String message = "Bienvenue sur la plateforme de gestion de projets collaboratifs ! " +
                "Vous pouvez maintenant consulter les projets publics et collaborer.";

        return creerNotification(utilisateurId, message);
    }

    /**
     * Notifications pour les projets (F6, F7, F8)
     */
    public void notifierNouveauProjet(Long chefProjetId, String nomProjet) {
        String message = String.format("Votre projet '%s' a été créé avec succès !", nomProjet);
        creerNotification(chefProjetId, message);
    }

    public void notifierTacheAssignee(Long membreId, String nomTache, String nomProjet) {
        String message = String.format("Nouvelle tâche assignée dans '%s' : %s", nomProjet, nomTache);
        creerNotification(membreId, message);
    }

    public void notifierAjoutProjet(Long membreId, String nomProjet) {
        String message = String.format("Vous avez été ajouté au projet '%s'", nomProjet);
        creerNotification(membreId, message);
    }

    /**
     * Notifications de paiement (F10, F11)
     */
    public void notifierPaiementAbonnement(Long chefProjetId, String statut, Double montant) {
        String message;
        if ("COMPLETE".equals(statut)) {
            message = String.format("Paiement confirmé (%.2f€). Accès Chef de Projet activé.", montant);
        } else if ("ECHEC".equals(statut)) {
            message = String.format("Échec paiement (%.2f€). Veuillez vérifier vos informations.", montant);
        } else {
            message = String.format("Statut paiement : %s (%.2f€)", statut, montant);
        }

        creerNotification(chefProjetId, message);
    }

    public void notifierFactureGeneree(Long chefProjetId, String numeroFacture) {
        String message = String.format("Facture %s générée et disponible en téléchargement", numeroFacture);
        creerNotification(chefProjetId, message);
    }

    // ============================================================================
    // MÉTHODES DE VÉRIFICATION
    // ============================================================================

    /**
     * Vérifier si une notification appartient à un utilisateur
     */
    @Transactional(readOnly = true)
    public boolean appartientAUtilisateur(Long notificationId, Long utilisateurId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isEmpty()) {
            return false;
        }
        return notification.get().getUtilisateur().getId().equals(utilisateurId);
    }

    /**
     * Vérifier si l'utilisateur a des notifications non lues
     */
    @Transactional(readOnly = true)
    public boolean hasNotificationsNonLues(Long utilisateurId) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur.isEmpty()) {
            return false;
        }
        return notificationRepository.existsByUtilisateurAndLuFalse(utilisateur.get());
    }

    // ============================================================================
    // MÉTHODES ADMINISTRATEUR
    // ============================================================================

    /**
     * Obtenir toutes les notifications (Admin)
     */
    @Transactional(readOnly = true)
    public List<Notification> obtenirToutes() {
        return notificationRepository.findAll();
    }

    /**
     * Nettoyer les anciennes notifications (maintenance)
     */
    @Transactional
    public int nettoyerAnciennesNotifications(int jours) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(jours);
        log.info("Nettoyage des notifications antérieures au {}", cutoffDate);

        List<Notification> anciennes = notificationRepository.findAll().stream()
                .filter(n -> n.getDate().isBefore(cutoffDate))
                .toList();

        anciennes.forEach(notificationRepository::delete);

        log.info("{} anciennes notifications supprimées", anciennes.size());
        return anciennes.size();
    }
}
