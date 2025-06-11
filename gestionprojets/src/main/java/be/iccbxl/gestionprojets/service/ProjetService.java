package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.ProjetDTO;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des projets
 */
@Service
public class ProjetService {

    private final ProjetRepository projetRepository;

    public ProjetService(ProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }

    /**
     * Créer un nouveau projet
     */
    public ProjetDTO creerProjet(ProjetDTO projetDTO) {
        Projet projet = new Projet();
        projet.setTitre(projetDTO.getTitre());
        projet.setDescription(projetDTO.getDescription());
        projet.setIdCreateur(projetDTO.getIdCreateur());
        projet.setStatut("ACTIF");

        Projet projetSauve = projetRepository.save(projet);
        return convertirEnDTO(projetSauve);
    }

    /**
     * Obtenir tous les projets
     */
    public List<ProjetDTO> obtenirTousProjets() {
        return projetRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir un projet par ID
     */
    public Optional<ProjetDTO> obtenirProjetParId(Long id) {
        return projetRepository.findById(id)
                .map(this::convertirEnDTO);
    }

    /**
     * Obtenir les projets d'un créateur
     */
    public List<ProjetDTO> obtenirProjetsParCreateur(Long idCreateur) {
        return projetRepository.findByIdCreateur(idCreateur)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Modifier un projet
     */
    public ProjetDTO modifierProjet(Long id, ProjetDTO projetDTO) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        projet.setTitre(projetDTO.getTitre());
        projet.setDescription(projetDTO.getDescription());
        projet.setStatut(projetDTO.getStatut());

        Projet projetSauve = projetRepository.save(projet);
        return convertirEnDTO(projetSauve);
    }

    /**
     * Supprimer un projet
     */
    public void supprimerProjet(Long id) {
        projetRepository.deleteById(id);
    }

    /**
     * Conversion Projet
     */
    private ProjetDTO convertirEnDTO(Projet projet) {
        ProjetDTO dto = new ProjetDTO();
        dto.setId(projet.getId());
        dto.setTitre(projet.getTitre());
        dto.setDescription(projet.getDescription());
        dto.setIdCreateur(projet.getIdCreateur());
        dto.setStatut(projet.getStatut());
        dto.setDateCreation(projet.getDateCreation());
        return dto;
    }
}