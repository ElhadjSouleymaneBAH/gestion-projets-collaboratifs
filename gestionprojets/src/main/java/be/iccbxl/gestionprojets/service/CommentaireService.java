package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.CommentaireDTO;
import be.iccbxl.gestionprojets.model.Commentaire;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.CommentaireRepository;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des commentaires
 * Conforme au cahier des charges F12 + F13
 *
 * F12 : Commenter les tâches/projets
 * F13 : Système de notifications (génération automatique)
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Service
@Transactional
@Slf4j
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final TacheRepository tacheRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    public CommentaireService(CommentaireRepository commentaireRepository,
                              TacheRepository tacheRepository,
                              UtilisateurRepository utilisateurRepository,
                              NotificationService notificationService) {
        this.commentaireRepository = commentaireRepository;
        this.tacheRepository = tacheRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.notificationService = notificationService;
    }

    // ============================================================================
    // F12 : COMMENTER LES TÂCHES/PROJETS
    // ============================================================================

    /**
     * Créer un nouveau commentaire (F12)
     * Génère automatiquement une notification (F13)
     *
     * Conforme au cahier des charges :
     * - "Permet d'ajouter des commentaires sur les tâches et les projets"
     * - "Chaque message ou action génère automatiquement des notifications"
     */
    public CommentaireDTO creerCommentaire(CommentaireDTO commentaireDTO, String emailAuteur) {
        log.info("[F12] Création commentaire par {}", emailAuteur);

        // 1. Validation des données
        if (commentaireDTO.getContenu() == null || commentaireDTO.getContenu().trim().isEmpty()) {
            throw new RuntimeException("Le contenu du commentaire est obligatoire");
        }

        // 2. Récupérer la tâche
        Tache tache = tacheRepository.findById(commentaireDTO.getTacheId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée: " + commentaireDTO.getTacheId()));

        // 3. Récupérer l'auteur du commentaire
        Utilisateur auteur = utilisateurRepository.findByEmail(emailAuteur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + emailAuteur));

        // 4. Créer le commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(commentaireDTO.getContenu());
        commentaire.setDate(LocalDateTime.now());
        commentaire.setTache(tache);
        commentaire.setAuteur(auteur);

        Commentaire commentaireSauvegarde = commentaireRepository.save(commentaire);
        log.info("[F12] ✅ Commentaire créé avec ID: {}", commentaireSauvegarde.getId());

        // ========================================================================
        // F13 : GÉNÉRATION AUTOMATIQUE DE NOTIFICATION
        // ========================================================================
        // Conforme au cahier des charges :
        // "Les notifications peuvent être consultées dans l'interface et/ou
        //  envoyées par e-mail. Chaque message ou action génère automatiquement
        //  des notifications pour les membres concernés."

        // NOTE : Utilisation de assigneA (Utilisateur) tel que défini dans l'entité Tache
        if (tache.getAssigneA() != null && !tache.getAssigneA().getId().equals(auteur.getId())) {
            try {
                String nomCompletAuteur = auteur.getPrenom() + " " + auteur.getNom();
                String message = String.format(
                        "Nouveau commentaire de %s sur '%s'",
                        nomCompletAuteur,
                        tache.getTitre()
                );

                // Créer la notification pour l'utilisateur assigné
                notificationService.creerNotification(tache.getAssigneA().getId(), message);

                log.info("[F13] ✅ Notification créée automatiquement pour utilisateur {}", tache.getAssigneA().getId());
            } catch (Exception e) {
                log.error("[F13] ❌ Échec création notification: {}", e.getMessage());
                // On continue même si la notification échoue (robustesse)
            }
        } else {
            log.debug("[F13] ℹ️ Aucune notification créée (pas d'assigné ou auto-commentaire)");
        }

        return convertirEnDTO(commentaireSauvegarde);
    }

    /**
     * Obtenir tous les commentaires d'une tâche (F12)
     * Tri chronologique pour faciliter la lecture
     */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirCommentairesTache(Long tacheId) {
        log.debug("[F12] Recherche commentaires pour tâche {}", tacheId);
        List<Commentaire> commentaires = commentaireRepository.findByTacheIdOrderByDateAsc(tacheId);
        log.debug("[F12] {} commentaires trouvés", commentaires.size());
        return commentaires.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les commentaires d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirCommentairesUtilisateur(Long utilisateurId) {
        log.debug("[F12] Recherche commentaires de l'utilisateur {}", utilisateurId);
        List<Commentaire> commentaires = commentaireRepository.findByAuteurIdOrderByDateDesc(utilisateurId);
        return commentaires.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Supprimer un commentaire (F12)
     */
    public void supprimerCommentaire(Long commentaireId) {
        log.info("[F12] Suppression commentaire {}", commentaireId);

        if (!commentaireRepository.existsById(commentaireId)) {
            throw new RuntimeException("Commentaire non trouvé: " + commentaireId);
        }

        commentaireRepository.deleteById(commentaireId);
        log.info("[F12] ✅ Commentaire {} supprimé", commentaireId);
    }

    /**
     * Obtenir tous les commentaires (Admin)
     */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirTous() {
        return commentaireRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    // ============================================================================
    // MÉTHODES UTILITAIRES
    // ============================================================================

    /**
     * Conversion Commentaire → CommentaireDTO
     */
    private CommentaireDTO convertirEnDTO(Commentaire commentaire) {
        CommentaireDTO dto = new CommentaireDTO();
        dto.setId(commentaire.getId());
        dto.setContenu(commentaire.getContenu());
        dto.setDate(commentaire.getDate());
        dto.setTacheId(commentaire.getTache().getId());

        if (commentaire.getAuteur() != null) {
            dto.setAuteurId(commentaire.getAuteur().getId());
            dto.setAuteurNom(commentaire.getAuteur().getNom());
            dto.setAuteurPrenom(commentaire.getAuteur().getPrenom());
            dto.setAuteurEmail(commentaire.getAuteur().getEmail());
        }

        return dto;
    }
}
