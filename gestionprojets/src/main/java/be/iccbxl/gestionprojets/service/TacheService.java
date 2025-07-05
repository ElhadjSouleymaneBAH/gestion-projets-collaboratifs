package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.enums.StatusTache;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des tâches
 */
@Service
public class TacheService {

    private final TacheRepository tacheRepository;
    private final ProjetRepository projetRepository;
    private final UtilisateurRepository utilisateurRepository;

    public TacheService(TacheRepository tacheRepository, ProjetRepository projetRepository, UtilisateurRepository utilisateurRepository) {
        this.tacheRepository = tacheRepository;
        this.projetRepository = projetRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Créer une nouvelle tâche
     */
    public TacheDTO creerTache(TacheDTO tacheDTO) {
        Tache tache = new Tache();
        tache.setTitre(tacheDTO.getTitre());
        tache.setDescription(tacheDTO.getDescription());

        // Chercher le projet dans la base
        Projet projet = projetRepository.findById(tacheDTO.getIdProjet())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        tache.setProjet(projet);

        // Si la tâche est assignée à quelqu'un
        if (tacheDTO.getIdAssigne() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(tacheDTO.getIdAssigne())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            tache.setAssigneA(utilisateur);
        }

        tache.setStatut(StatusTache.BROUILLON); // Statut par défaut

        Tache tacheSauvee = tacheRepository.save(tache);
        return convertirEnDTO(tacheSauvee);
    }

    /**
     * Obtenir toutes les tâches
     */
    public List<TacheDTO> obtenirToutesLesTaches() {
        return tacheRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir une tâche par ID
     */
    public Optional<TacheDTO> obtenirTacheParId(Long id) {
        return tacheRepository.findById(id)
                .map(this::convertirEnDTO);
    }

    /**
     * Obtenir les tâches d'un projet
     */
    public List<TacheDTO> obtenirTachesParProjet(Long idProjet) {
        return tacheRepository.findByProjetId(idProjet)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les tâches assignées à un utilisateur
     */
    public List<TacheDTO> obtenirTachesParUtilisateur(Long idUtilisateur) {
        return tacheRepository.findByAssigneAId(idUtilisateur)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Modifier une tâche
     */
    public TacheDTO modifierTache(Long id, TacheDTO tacheDTO) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        tache.setTitre(tacheDTO.getTitre());
        tache.setDescription(tacheDTO.getDescription());

        // Gestion de l'assignation
        if (tacheDTO.getIdAssigne() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(tacheDTO.getIdAssigne())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            tache.assignerA(utilisateur); // Utilise la méthode de l'entité
        } else {
            tache.setAssigneA(null);
        }

        // Mise à jour du statut
        tache.setStatut(tacheDTO.getStatut());

        Tache tacheSauvee = tacheRepository.save(tache);
        return convertirEnDTO(tacheSauvee);
    }

    /**
     * Supprimer une tâche
     */
    public void supprimerTache(Long id) {
        tacheRepository.deleteById(id);
    }

    /**
     * Conversion Tache vers TacheDTO
     */
    private TacheDTO convertirEnDTO(Tache tache) {
        TacheDTO dto = new TacheDTO();
        dto.setId(tache.getId());
        dto.setTitre(tache.getTitre());
        dto.setDescription(tache.getDescription());

        // Récupération des IDs liés
        if (tache.getProjet() != null) {
            dto.setIdProjet(tache.getProjet().getId());
        }
        if (tache.getAssigneA() != null) {
            dto.setIdAssigne(tache.getAssigneA().getId());
        }

        dto.setStatut(tache.getStatut());
        dto.setDateCreation(tache.getDateCreation());
        return dto;
    }
}