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
 * Service de gestion des tâches.
 *
 * Implémente la fonctionnalité F7 du cahier des charges.
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
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
     * Crée une nouvelle tâche.
     *
     * @param tacheDTO les données de la tâche
     * @return la tâche créée
     * @throws RuntimeException si le projet n'existe pas
     */
    public TacheDTO creerTache(TacheDTO tacheDTO) {
        Tache tache = new Tache();

        // Chercher le projet dans la base
        Projet projet = projetRepository.findById(tacheDTO.getIdProjet())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        // Utiliser la logique métier de l'entité
        boolean creationReussie = tache.ajouterTache(
                tacheDTO.getTitre(),
                tacheDTO.getDescription(),
                projet
        );

        if (!creationReussie) {
            throw new RuntimeException("Impossible de créer la tâche : données invalides");
        }

        // Si la tâche est assignée à quelqu'un
        if (tacheDTO.getIdAssigne() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(tacheDTO.getIdAssigne())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            tache.assignerA(utilisateur);
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        return convertirEnDTO(tacheSauvee);
    }

    /**
     * Récupère toutes les tâches.
     *
     * @return la liste de toutes les tâches
     */
    public List<TacheDTO> obtenirToutesLesTaches() {
        return tacheRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une tâche par son ID.
     *
     * @param id l'identifiant de la tâche
     * @return la tâche si trouvée
     */
    public Optional<TacheDTO> obtenirTacheParId(Long id) {
        return tacheRepository.findById(id)
                .map(this::convertirEnDTO);
    }

    /**
     * Récupère les tâches d'un projet.
     *
     * @param idProjet l'identifiant du projet
     * @return la liste des tâches du projet
     */
    public List<TacheDTO> obtenirTachesParProjet(Long idProjet) {
        return tacheRepository.findByProjetId(idProjet)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les tâches assignées à un utilisateur.
     *
     * @param idUtilisateur l'identifiant de l'utilisateur
     * @return la liste des tâches assignées
     */
    public List<TacheDTO> obtenirTachesParUtilisateur(Long idUtilisateur) {
        return tacheRepository.findByAssigneAId(idUtilisateur)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Modifie une tâche existante.
     *
     * @param id l'identifiant de la tâche
     * @param tacheDTO les nouvelles données
     * @return la tâche modifiée
     * @throws RuntimeException si la tâche n'existe pas ou si les données sont invalides
     */
    public TacheDTO modifierTache(Long id, TacheDTO tacheDTO) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        // Utiliser la logique métier de l'entité
        boolean modificationReussie = tache.modifierTache(
                id,
                tacheDTO.getTitre(),
                tacheDTO.getDescription(),
                tacheDTO.getStatut()
        );

        if (!modificationReussie) {
            throw new RuntimeException("Impossible de modifier la tâche : données invalides");
        }

        // Gestion de l'assignation
        if (tacheDTO.getIdAssigne() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(tacheDTO.getIdAssigne())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            tache.assignerA(utilisateur);
        } else {
            tache.assignerA(null);
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        return convertirEnDTO(tacheSauvee);
    }

    /**
     * Change le statut d'une tâche selon le diagramme d'état.
     *
     * @param id l'identifiant de la tâche
     * @param nouveauStatut le nouveau statut
     * @return la tâche avec le statut modifié
     * @throws RuntimeException si la transition n'est pas autorisée
     */
    public TacheDTO changerStatutTache(Long id, StatusTache nouveauStatut) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        // Utiliser la logique métier du diagramme d'état
        boolean changementReussi = tache.changerStatut(nouveauStatut);

        if (!changementReussi) {
            throw new RuntimeException("Transition de statut non autorisée selon le diagramme d'état");
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        return convertirEnDTO(tacheSauvee);
    }

    /**
     * Supprime une tâche.
     *
     * @param id l'identifiant de la tâche
     * @throws RuntimeException si la tâche n'existe pas
     */
    public void supprimerTache(Long id) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

        // Utiliser la logique métier pour nettoyer les relations
        boolean suppressionReussie = tache.supprimerTache(id);

        if (!suppressionReussie) {
            throw new RuntimeException("Impossible de supprimer la tâche");
        }

        tacheRepository.deleteById(id);
    }

    /**
     * Convertit une entité Tache en TacheDTO.
     *
     * @param tache l'entité à convertir
     * @return le DTO correspondant
     */
    private TacheDTO convertirEnDTO(Tache tache) {
        TacheDTO dto = new TacheDTO();
        dto.setId(tache.getId());
        dto.setTitre(tache.getTitre());
        dto.setDescription(tache.getDescription());

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