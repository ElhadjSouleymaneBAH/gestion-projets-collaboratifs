package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.enums.TypeMessage;
import be.iccbxl.gestionprojets.enums.StatutMessage;
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
 * author ElhadjSouleymaneBAH
 * version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    // ========== MÉTHODES PRINCIPALES ==========

    /** Sauvegarder un nouveau message (F9) */
    public Message sauvegarderMessage(Message message) {
        if (message.getDateEnvoi() == null) {
            message.setDateEnvoi(LocalDateTime.now());
        }
        if (message.getStatut() == null) {
            message.setStatut(StatutMessage.ENVOYE);
        }
        if (message.getType() == null) {
            message.setType(TypeMessage.TEXT);
        }
        return messageRepository.save(message);
    }

    /** ⇨ AJOUT : récupérer tous les messages (alias admin /api/messages/admin/all) */
    @Transactional(readOnly = true)
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    /** Récupérer tous les messages d'un projet (F9) */
    @Transactional(readOnly = true)
    public List<Message> getMessagesParProjet(Long projetId) {
        return messageRepository.findByProjetIdOrderByDateEnvoiAsc(projetId);
    }

    /** Derniers messages d'un projet */
    @Transactional(readOnly = true)
    public List<Message> getDerniersMessagesParProjet(Long projetId) {
        return messageRepository.findByProjetIdOrderByDateEnvoiDesc(projetId);
    }

    /** Trouver un message par ID */
    @Transactional(readOnly = true)
    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    // ========== GESTION DES STATUTS ==========

    /** Marquer un message comme lu (F9) — 1 argument */
    public void marquerCommeLu(Long messageId) {
        messageRepository.findById(messageId).ifPresent(m -> {
            m.marquerCommeLu();
            messageRepository.save(m);
        });
    }

    /** Messages non lus d'un projet */
    @Transactional(readOnly = true)
    public List<Message> getMessagesNonLus(Long projetId) {
        return messageRepository.findMessagesNonLusByProjet(projetId);
    }

    /** Compter messages non lus pour un utilisateur dans un projet */
    @Transactional(readOnly = true)
    public Long compterMessagesNonLus(Long projetId, Long utilisateurId) {
        return messageRepository.countMessagesNonLusByProjetAndNotUtilisateur(projetId, utilisateurId);
    }

    // ========== FILTRAGE ET RECHERCHE ==========

    @Transactional(readOnly = true)
    public List<Message> getMessagesParUtilisateur(Long utilisateurId) {
        return messageRepository.findByUtilisateurIdOrderByDateEnvoiDesc(utilisateurId);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesParType(TypeMessage type) {
        return messageRepository.findByTypeOrderByDateEnvoiDesc(type);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesParProjetEtType(Long projetId, TypeMessage type) {
        return messageRepository.findByProjetIdAndTypeOrderByDateEnvoiDesc(projetId, type);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesRecents(Long projetId) {
        LocalDateTime depuis = LocalDateTime.now().minusDays(1);
        return messageRepository.findMessagesRecentsByProjet(projetId, depuis);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesDepuis(Long projetId, LocalDateTime depuis) {
        return messageRepository.findMessagesRecentsByProjet(projetId, depuis);
    }

    // ========== STATISTIQUES ==========

    @Transactional(readOnly = true)
    public Long compterMessages(Long projetId) {
        return messageRepository.countByProjetId(projetId);
    }

    @Transactional(readOnly = true)
    public List<Message> getActiviteUtilisateur(List<Long> projetIds) {
        return messageRepository.findLatestMessagesByProjetIds(projetIds);
    }

    // ========== GESTION ==========

    public void supprimerMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    @Transactional(readOnly = true)
    public boolean messageExiste(Long messageId) {
        return messageRepository.existsById(messageId);
    }
}
