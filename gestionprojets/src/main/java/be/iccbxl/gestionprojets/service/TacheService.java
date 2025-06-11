
package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.enums.StatusTache;
import be.iccbxl.gestionprojets.repository.TacheRepository;
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

    public TacheService(TacheRepository tacheRepository) {
        this.tacheRepository = tacheRepository;
    }

    /**
     * Créer une nouvelle tâche
     */
    public TacheDTO creerTache(TacheDTO tacheDTO) {
        Tache tache = new Tache();
        tache.setTitre(tacheDTO.getTitre());
        tache.setDescription(tacheDTO.getDescription());
        tache.setIdProjet(tacheDTO.getIdProjet());
        tache.setIdAssigne(tacheDTO.getIdAssigne());
        tache.setStatut(StatusTache.BROUILLON); // Statut initial selon diagramme

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
        return tacheRepository.findByIdProjet(idProjet)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les tâches assignées à un utilisateur
     */
    public List<TacheDTO> obtenirTachesParUtilisateur(Long idUtilisateur) {
        return tacheRepository.findByIdAssigne(idUtilisateur)
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
        tache.setIdAssigne(tacheDTO.getIdAssigne());

        // Mise à jour  du statut
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
     * Conversion en TacheDTO
     */
    private TacheDTO convertirEnDTO(Tache tache) {
        TacheDTO dto = new TacheDTO();
        dto.setId(tache.getId());
        dto.setTitre(tache.getTitre());
        dto.setDescription(tache.getDescription());
        dto.setIdProjet(tache.getIdProjet());
        dto.setIdAssigne(tache.getIdAssigne());
        dto.setStatut(tache.getStatut());
        dto.setDateCreation(tache.getDateCreation());
        return dto;
    }
}