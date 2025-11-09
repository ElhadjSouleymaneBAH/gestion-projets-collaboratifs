package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.model.Notification;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.NotificationRepository;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service de gestion des notifications avec WebSocket
 * Version 2.0 - Notifications automatiques changement statut tâches
 *
 * @author ElhadjSouleymaneBAH
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProjetRepository projetRepository;
    private final SimpMessagingTemplate messagingTemplate; // ✅ WebSocket

    // ============================================================================
    // ✅ NOUVELLE MÉTHODE : NOTIFICATION AUTOMATIQUE CHANGEMENT STATUT TÂCHE
    // ============================================================================

    /**
     * Envoie automatiquement des notifications lors des changements de statut de tâche
     *
     * SCÉNARIOS :
     * 1. MEMBRE soumet tâche (EN_COURS → EN_ATTENTE_VALIDATION) → Notifie CHEF
     * 2. CHEF valide tâche (EN_ATTENTE_VALIDATION → TERMINE) → Notifie MEMBRE
     * 3. CHEF annule tâche (n'importe quel statut → ANNULE) → Notifie MEMBRE
     * 4. CHEF renvoie en brouillon (EN_ATTENTE_VALIDATION → BROUILLON) → Notifie MEMBRE
     */
    public void notifierChangementStatutTache(TacheDTO tache, StatutTache nouveauStatut) {
        try {
            log.debug("[F13] Notification changement statut tâche {} vers {}", tache.getId(), nouveauStatut);

            // Récupérer le projet pour obtenir le chef de projet
            Projet projet = projetRepository.findById(tache.getIdProjet())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé ID: " + tache.getIdProjet()));

            // SCÉNARIO 1️⃣ : MEMBRE SOUMET TÂCHE → Notifier CHEF
            if (nouveauStatut == StatutTache.EN_ATTENTE_VALIDATION) {
                if (tache.getIdAssigne() != null) {
                    Utilisateur membre = utilisateurRepository.findById(tache.getIdAssigne())
                            .orElse(null);

                    if (membre != null && projet.getCreateur() != null) {
                        String message = String.format(
                                "%s %s a soumis la tâche \"%s\" pour validation dans le projet \"%s\"",
                                membre.getPrenom(),
                                membre.getNom(),
                                tache.getTitre(),
                                projet.getTitre()
                        );

                        envoyerNotification(
                                projet.getCreateur().getId(),
                                "Tâche soumise pour validation",
                                message,
                                "TACHE"
                        );

                        log.info("[F13] ✅ Notification envoyée au chef {} : Tâche {} soumise",
                                projet.getCreateur().getId(), tache.getId());
                    }
                }
            }

            // SCÉNARIO 2️⃣ : CHEF VALIDE TÂCHE → Notifier MEMBRE
            if (nouveauStatut == StatutTache.TERMINE && tache.getIdAssigne() != null) {
                String message = String.format(
                        "Votre tâche \"%s\" a été validée par le chef de projet dans \"%s\"",
                        tache.getTitre(),
                        projet.getTitre()
                );

                envoyerNotification(
                        tache.getIdAssigne(),
                        "Tâche validée ✓",
                        message,
                        "TACHE"
                );

                log.info("[F13] ✅ Notification envoyée au membre {} : Tâche {} validée",
                        tache.getIdAssigne(), tache.getId());
            }

            // SCÉNARIO 3️⃣ : CHEF ANNULE TÂCHE → Notifier MEMBRE
            if (nouveauStatut == StatutTache.ANNULE && tache.getIdAssigne() != null) {
                String message = String.format(
                        "Votre tâche \"%s\" a été annulée dans le projet \"%s\"",
                        tache.getTitre(),
                        projet.getTitre()
                );

                envoyerNotification(
                        tache.getIdAssigne(),
                        "Tâche annulée",
                        message,
                        "TACHE"
                );

                log.info("[F13] ✅ Notification envoyée au membre {} : Tâche {} annulée",
                        tache.getIdAssigne(), tache.getId());
            }

            // SCÉNARIO 4️⃣ : CHEF RENVOIE EN BROUILLON → Notifier MEMBRE
            if (nouveauStatut == StatutTache.BROUILLON && tache.getIdAssigne() != null) {
                String message = String.format(
                        "La tâche \"%s\" a été renvoyée en brouillon dans le projet \"%s\"",
                        tache.getTitre(),
                        projet.getTitre()
                );

                envoyerNotification(
                        tache.getIdAssigne(),
                        "Tâche renvoyée en brouillon",
                        message,
                        "TACHE"
                );

                log.info("[F13] ✅ Notification envoyée au membre {} : Tâche {} en brouillon",
                        tache.getIdAssigne(), tache.getId());
            }

        } catch (Exception e) {
            log.error("[F13] ❌ Erreur notification changement statut tâche {}: {}",
                    tache.getId(), e.getMessage());
            // Ne pas bloquer l'opération si notification échoue
        }
    }

    // ============================================================================
    // MÉTHODE GÉNÉRIQUE D'ENVOI NOTIFICATION (DB + WebSocket)
    // ============================================================================

    /**
     * Envoie une notification (sauvegarde DB + envoi WebSocket temps réel)
     */
    private void envoyerNotification(Long utilisateurId, String titre, String message, String type) {
        try {
            // 1️⃣ Sauvegarder en base de données
            Utilisateur destinataire = utilisateurRepository.findById(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé ID: " + utilisateurId));

            Notification notification = new Notification();
            notification.setUtilisateur(destinataire);
            notification.setMessage(message);
            notification.setDate(LocalDateTime.now());
            notification.setLu(false);

            notification = notificationRepository.save(notification);

            // 2️⃣ Envoyer via WebSocket (temps réel)
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", notification.getId());
            payload.put("titre", titre);
            payload.put("message", message);
            payload.put("type", type);
            payload.put("createdAt", notification.getDate().toString());
            payload.put("lu", false);

            String destination = "/user/" + utilisateurId + "/topic/notifications";
            messagingTemplate.convertAndSend(destination, payload);

            log.info("[F13] ✅ Notification {} envoyée à utilisateur {} via WebSocket",
                    notification.getId(), utilisateurId);

        } catch (Exception e) {
            log.error("[F13] ❌ Erreur envoi notification à utilisateur {}: {}",
                    utilisateurId, e.getMessage());
        }
    }

    // ============================================================================
    // MÉTHODES DE BASE (EXISTANTES - CONSERVÉES)
    // ============================================================================

    public Notification creerNotification(Long utilisateurId, String message) {
        log.debug("[F13] Création notification pour utilisateur {} : {}", utilisateurId, message);

        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("Le message de notification est obligatoire");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + utilisateurId));

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUtilisateur(utilisateur);
        notification.setDate(LocalDateTime.now());
        notification.setLu(false);

        Notification saved = notificationRepository.save(notification);
        log.info("[F13] ✅ Notification {} créée pour utilisateur {}", saved.getId(), utilisateurId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUtilisateur(Long utilisateurId) {
        log.debug("Récupération notifications pour utilisateur {}", utilisateurId);
        return notificationRepository.findByUtilisateurIdOrderByDateDesc(utilisateurId);
    }

    @Transactional(readOnly = true)
    public long compterNotificationsNonLues(Long utilisateurId) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur.isEmpty()) {
            return 0;
        }
        return notificationRepository.countByUtilisateurAndLuFalse(utilisateur.get());
    }

    public void marquerCommeLue(Long notificationId, Long utilisateurId) {
        log.debug("Marquage notification {} comme lue pour utilisateur {}", notificationId, utilisateurId);

        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification non trouvée avec l'ID : " + notificationId);
        }

        Notification notification = notificationOpt.get();

        if (!notification.getUtilisateur().getId().equals(utilisateurId)) {
            throw new RuntimeException("Accès non autorisé à cette notification");
        }

        notification.marquerCommeLue();
        notificationRepository.save(notification);
    }

    public void supprimerNotification(Long notificationId, Long utilisateurId) {
        log.debug("Suppression notification {} pour utilisateur {}", notificationId, utilisateurId);

        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification non trouvée avec l'ID : " + notificationId);
        }

        Notification notification = notificationOpt.get();

        if (!notification.getUtilisateur().getId().equals(utilisateurId)) {
            throw new RuntimeException("Accès non autorisé à cette notification");
        }

        notificationRepository.delete(notification);
    }

    @Transactional(readOnly = true)
    public Optional<Notification> obtenirParId(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsNonLues(Long utilisateurId) {
        log.debug("Récupération notifications non lues pour utilisateur {}", utilisateurId);
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur.isEmpty()) {
            return List.of();
        }
        return notificationRepository.findByUtilisateurAndLuFalseOrderByDateDesc(utilisateur.get());
    }

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

    // ============================================================================
    // NOTIFICATIONS MÉTIER (EXISTANTES - CONSERVÉES)
    // ============================================================================

    public Notification creerNotificationBienvenue(Long utilisateurId) {
        log.debug("Création notification bienvenue pour utilisateur {}", utilisateurId);

        String message = "Bienvenue sur la plateforme de gestion de projets collaboratifs ! " +
                "Vous pouvez maintenant consulter les projets publics et collaborer.";

        return creerNotification(utilisateurId, message);
    }

    public void notifierNouveauProjet(Long chefProjetId, String nomProjet) {
        String message = String.format("Votre projet '%s' a été créé avec succès !", nomProjet);
        creerNotification(chefProjetId, message);
    }

    public void notifierTacheAssignee(Long membreId, String nomTache, String nomProjet) {
        String message = String.format("Nouvelle tâche assignée dans '%s' : %s", nomProjet, nomTache);
        Notification notif = creerNotification(membreId, message);

        // Envoi WebSocket
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", notif.getId());
            payload.put("titre", "Nouvelle tâche assignée");
            payload.put("message", message);
            payload.put("type", "TACHE");
            payload.put("createdAt", notif.getDate().toString());
            payload.put("lu", false);

            messagingTemplate.convertAndSend(
                    "/user/" + membreId + "/topic/notifications",
                    payload
            );
        } catch (Exception e) {
            log.error("Erreur envoi WebSocket notification tâche assignée: {}", e.getMessage());
        }
    }

    public void notifierAjoutProjet(Long membreId, String nomProjet) {
        String message = String.format("Vous avez été ajouté au projet '%s'", nomProjet);
        Notification notif = creerNotification(membreId, message);

        // Envoi WebSocket
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", notif.getId());
            payload.put("titre", "Ajout à un projet");
            payload.put("message", message);
            payload.put("type", "PROJET");
            payload.put("createdAt", notif.getDate().toString());
            payload.put("lu", false);

            messagingTemplate.convertAndSend(
                    "/user/" + membreId + "/topic/notifications",
                    payload
            );
        } catch (Exception e) {
            log.error("Erreur envoi WebSocket notification ajout projet: {}", e.getMessage());
        }
    }

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

    @Transactional(readOnly = true)
    public boolean appartientAUtilisateur(Long notificationId, Long utilisateurId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isEmpty()) {
            return false;
        }
        return notification.get().getUtilisateur().getId().equals(utilisateurId);
    }

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

    @Transactional(readOnly = true)
    public List<Notification> obtenirToutes() {
        return notificationRepository.findAll();
    }

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