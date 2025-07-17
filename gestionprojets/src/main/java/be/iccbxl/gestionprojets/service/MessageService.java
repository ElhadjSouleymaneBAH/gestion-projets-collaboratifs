package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.model.Message;
import be.iccbxl.gestionprojets.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des messages de chat
 * Support de F9 : Collaboration en temps réel
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @since 2025-07-14
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    // ========== MÉTHODES PRINCIPALES ==========

    /**
     * Sauvegarder un nouveau message
     * Utilisé pour F9 : Envoyer des messages temps réel
     */
    public Message sauvegarderMessage(Message message) {
        return messageRepository.save(message);
    }

    /**
     * Récupérer tous les messages d'un projet
     * Utilisé pour F9 : Afficher l'historique du chat
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesParProjet(Long projetId) {
        return messageRepository.findByProjetIdOrderByDateEnvoiAsc(projetId);
    }

    /**
     * Récupérer les derniers messages d'un projet
     * Utilisé pour F9 : Chat avec pagination
     */
    @Transactional(readOnly = true)
    public List<Message> getDerniersMessagesParProjet(Long projetId) {
        return messageRepository.findByProjetIdOrderByDateEnvoiDesc(projetId);
    }

    /**
     * Trouver un message par ID
     */
    @Transactional(readOnly = true)
    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    // ========== GESTION DES STATUTS ==========

    /**
     * Marquer un message comme lu
     * Utilisé pour F9 : Suivi des messages lus
     */
    public void marquerCommeLu(Long messageId, Long utilisateurId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.marquerCommeLu();
            messageRepository.save(message);
        }
    }

    /**
     * Récupérer les messages non lus d'un projet
     * Utilisé pour F9 : Notifications de messages non lus
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesNonLus(Long projetId) {
        return messageRepository.findMessagesNonLusByProjet(projetId);
    }

    /**
     * Compter les messages non lus pour un utilisateur dans un projet
     * Utilisé pour F9 : Badges de notification
     */
    @Transactional(readOnly = true)
    public Long compterMessagesNonLus(Long projetId, Long utilisateurId) {
        return messageRepository.countMessagesNonLusByProjetAndNotUtilisateur(projetId, utilisateurId);
    }

    // ========== FILTRAGE ET RECHERCHE ==========

    /**
     * Récupérer les messages par utilisateur
     * Utilisé pour le profil utilisateur
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesParUtilisateur(Long utilisateurId) {
        return messageRepository.findByUtilisateurIdOrderByDateEnvoiDesc(utilisateurId);
    }

    /**
     * Récupérer les messages par type
     * Utilisé pour F9 : Filtrer notifications/système
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesParType(String type) {
        return messageRepository.findByTypeOrderByDateEnvoiDesc(type);
    }

    /**
     * Récupérer les messages par projet et type
     * Utilisé pour F9 : Filtrage avancé
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesParProjetEtType(Long projetId, String type) {
        return messageRepository.findByProjetIdAndTypeOrderByDateEnvoiDesc(projetId, type);
    }

    /**
     * Récupérer les messages récents d'un projet (dernières 24h)
     * Utilisé pour F9 : Activité récente
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesRecents(Long projetId) {
        LocalDateTime depuis = LocalDateTime.now().minusDays(1);
        return messageRepository.findMessagesRecentsByProjet(projetId, depuis);
    }

    /**
     * Récupérer les messages récents depuis une date
     * Utilisé pour F9 : Synchronisation temps réel
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesDepuis(Long projetId, LocalDateTime depuis) {
        return messageRepository.findMessagesRecentsByProjet(projetId, depuis);
    }

    // ========== STATISTIQUES ==========

    /**
     * Compter les messages d'un projet
     * Statistiques pour l'interface
     */
    @Transactional(readOnly = true)
    public Long compterMessages(Long projetId) {
        return messageRepository.countByProjetId(projetId);
    }

    /**
     * Récupérer l'activité de tous les projets d'un utilisateur
     * Utilisé pour le tableau de bord
     */
    @Transactional(readOnly = true)
    public List<Message> getActiviteUtilisateur(List<Long> projetIds) {
        return messageRepository.findLatestMessagesByProjetIds(projetIds);
    }

    // ========== GESTION ==========

    /**
     * Supprimer un message
     * Utilisé pour la modération
     */
    public void supprimerMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    /**
     * Vérifier si un message existe
     * Utilisé pour la validation
     */
    @Transactional(readOnly = true)
    public boolean messageExiste(Long messageId) {
        return messageRepository.existsById(messageId);
    }
}