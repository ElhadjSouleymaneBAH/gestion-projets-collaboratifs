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
     * Créer une nouvelle notification (méthode principale)
     */
    public Notification creerNotification(Long utilisateurId, String message) {
        log.debug("Création notification pour utilisateur {} : {}", utilisateurId, message);

        // Validation similaire à UtilisateurService
        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("Le message de notification est obligatoire");
        }

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(utilisateurId);
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + utilisateurId);
        }

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUtilisateur(utilisateurOpt.get());
        notification.setDate(LocalDateTime.now());
        notification.setLu(false);

        return notificationRepository.save(notification);
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
     * Pattern similaire à UtilisateurService.inscrire()
     */
    public Notification creerNotificationBienvenue(Long utilisateurId) {
        log.debug("Création notification bienvenue pour utilisateur {}", utilisateurId);

        String message = "Bienvenue sur la plateforme de gestion de projets collaboratifs ! " +
                "Vous pouvez maintenant consulter les projets publics et collaborer.";

        return creerNotification(utilisateurId, message);
    }

    /**
     * Notifications pour les projets (F6, F7, F8)
     * Pattern similaire aux méthodes métier de UtilisateurService
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
     * Pattern similaire aux méthodes de promotion dans UtilisateurService
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
    // MÉTHODES DE VÉRIFICATION (pattern UtilisateurService)
    // ============================================================================

    /**
     * Vérifier si une notification appartient à un utilisateur
     * Pattern similaire aux vérifications dans UtilisateurService
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
     * Pattern similaire à UtilisateurService.existeParEmail()
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
    // MÉTHODES ADMINISTRATEUR (pattern UtilisateurService)
    // ============================================================================

    /**
     * Obtenir toutes les notifications (Admin)
     * Pattern identique à UtilisateurService.obtenirTous()
     */
    @Transactional(readOnly = true)
    public List<Notification> obtenirToutes() {
        return notificationRepository.findAll();
    }

    /**
     * Nettoyer les anciennes notifications (maintenance)
     * Pattern de maintenance comme dans votre architecture
     */
    @Transactional
    public int nettoyerAnciennesNotifications(int jours) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(jours);
        log.info("Nettoyage des notifications antérieures au {}", cutoffDate);

        // Implementation simple sans méthode repository complexe
        List<Notification> anciennes = notificationRepository.findAll().stream()
                .filter(n -> n.getDate().isBefore(cutoffDate))
                .toList();

        anciennes.forEach(notificationRepository::delete);

        log.info("{} anciennes notifications supprimées", anciennes.size());
        return anciennes.size();
    }
}