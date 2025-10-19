package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.CommentaireDTO;
import be.iccbxl.gestionprojets.model.Commentaire;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.CommentaireRepository;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final TacheRepository tacheRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CommentaireService(CommentaireRepository commentaireRepository,
                              TacheRepository tacheRepository,
                              UtilisateurRepository utilisateurRepository) {
        this.commentaireRepository = commentaireRepository;
        this.tacheRepository = tacheRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /** ⇨ AJOUT : Tous les commentaires (alias admin) */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirTous() {
        return commentaireRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    /** Obtenir tous les commentaires d'une tâche (F9) */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirCommentairesTache(Long tacheId) {
        System.out.println("DEBUG: [F9] Recherche commentaires pour tâche " + tacheId);
        List<Commentaire> commentaires = commentaireRepository.findByTacheIdOrderByDateAsc(tacheId);
        System.out.println("SUCCESS: [F9] " + commentaires.size() + " commentaires trouvés");
        return commentaires.stream().map(this::convertirEnDTO).collect(Collectors.toList());
    }

    /** Créer un nouveau commentaire (F9) */
    public CommentaireDTO creerCommentaire(CommentaireDTO commentaireDTO, String emailAuteur) {
        System.out.println("DEBUG: [F9] Création commentaire par " + emailAuteur);
        Tache tache = tacheRepository.findById(commentaireDTO.getTacheId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée: " + commentaireDTO.getTacheId()));
        Utilisateur auteur = utilisateurRepository.findByEmail(emailAuteur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + emailAuteur));
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(commentaireDTO.getContenu());
        commentaire.setDate(LocalDateTime.now());
        commentaire.setTache(tache);
        commentaire.setAuteur(auteur);
        Commentaire commentaireSauvegarde = commentaireRepository.save(commentaire);
        System.out.println("SUCCESS: [F9] Commentaire créé avec ID: " + commentaireSauvegarde.getId());
        return convertirEnDTO(commentaireSauvegarde);
    }

    /** Obtenir les commentaires d'un utilisateur */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirCommentairesUtilisateur(Long utilisateurId) {
        List<Commentaire> commentaires = commentaireRepository.findByAuteurIdOrderByDateDesc(utilisateurId);
        return commentaires.stream().map(this::convertirEnDTO).collect(Collectors.toList());
    }

    /** Supprimer un commentaire */
    public void supprimerCommentaire(Long commentaireId) {
        System.out.println("DEBUG: [F9] Suppression commentaire " + commentaireId);
        if (!commentaireRepository.existsById(commentaireId)) {
            throw new RuntimeException("Commentaire non trouvé: " + commentaireId);
        }
        commentaireRepository.deleteById(commentaireId);
        System.out.println("SUCCESS: [F9] Commentaire " + commentaireId + " supprimé");
    }

    /** Conversion Commentaire → CommentaireDTO */
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
