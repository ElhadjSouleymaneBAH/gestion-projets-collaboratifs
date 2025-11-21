package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.ListeColonneDTO;
import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.model.ListeColonne;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.repository.ListeColonneRepository;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des colonnes Kanban (ListeColonne)
 *
 * @author Elhadj Souleymane BAH
 * @version 1.0
 */
@Service
@Transactional
public class ListeColonneService {

    private final ListeColonneRepository listeColonneRepository;
    private final ProjetRepository projetRepository;

    private static final String[] COLONNES_PAR_DEFAUT = {
            "À faire",
            "En cours",
            "Terminé"
    };

    public ListeColonneService(ListeColonneRepository listeColonneRepository,
                               ProjetRepository projetRepository) {
        this.listeColonneRepository = listeColonneRepository;
        this.projetRepository = projetRepository;
    }

    public List<ListeColonneDTO> creerColonnesParDefaut(Long idProjet) {
        System.out.println("[ListeColonneService] Création colonnes par défaut pour projet " + idProjet);

        Projet projet = projetRepository.findById(idProjet)
                .orElseThrow(() -> new RuntimeException("Projet introuvable: " + idProjet));

        long colonnesExistantes = listeColonneRepository.countByProjetId(idProjet);
        if (colonnesExistantes > 0) {
            System.out.println("[ListeColonneService] Colonnes déjà existantes");
            return obtenirColonnesParProjet(idProjet);
        }

        for (int i = 0; i < COLONNES_PAR_DEFAUT.length; i++) {
            ListeColonne colonne = new ListeColonne(COLONNES_PAR_DEFAUT[i], i, projet);
            listeColonneRepository.save(colonne);
            System.out.println("[ListeColonneService] Colonne créée: " + COLONNES_PAR_DEFAUT[i]);
        }

        return obtenirColonnesParProjet(idProjet);
    }

    @Transactional(readOnly = true)
    public List<ListeColonneDTO> obtenirColonnesParProjet(Long idProjet) {
        System.out.println("[ListeColonneService] Récupération colonnes pour projet " + idProjet);

        List<ListeColonne> colonnes = listeColonneRepository.findByProjetIdWithTaches(idProjet);

        System.out.println("[ListeColonneService] " + colonnes.size() + " colonnes trouvées");

        return colonnes.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ListeColonneDTO> obtenirColonneParId(Long id) {
        return listeColonneRepository.findById(id)
                .map(this::convertirEnDTO);
    }

    public ListeColonneDTO creerColonne(ListeColonneDTO dto) {
        System.out.println("[ListeColonneService] Création colonne: " + dto.getNom());

        Projet projet = projetRepository.findById(dto.getIdProjet())
                .orElseThrow(() -> new RuntimeException("Projet introuvable: " + dto.getIdProjet()));

        Integer maxPosition = listeColonneRepository.findMaxPositionByProjetId(dto.getIdProjet());
        int nouvellePosition = (maxPosition != null) ? maxPosition + 1 : 0;

        ListeColonne colonne = new ListeColonne(dto.getNom(), nouvellePosition, projet);
        ListeColonne colonneCreee = listeColonneRepository.save(colonne);

        System.out.println("[ListeColonneService] Colonne créée avec ID: " + colonneCreee.getId());

        return convertirEnDTO(colonneCreee);
    }

    public ListeColonneDTO renommerColonne(Long id, String nouveauNom) {
        System.out.println("[ListeColonneService] Renommage colonne " + id + " → " + nouveauNom);

        ListeColonne colonne = listeColonneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colonne introuvable: " + id));

        colonne.setNom(nouveauNom);
        ListeColonne colonneModifiee = listeColonneRepository.save(colonne);

        return convertirEnDTO(colonneModifiee);
    }

    public void reorganiserColonnes(Long idProjet, List<Long> nouvelOrdre) {
        System.out.println("[ListeColonneService] Réorganisation colonnes projet " + idProjet);

        for (int i = 0; i < nouvelOrdre.size(); i++) {
            Long colonneId = nouvelOrdre.get(i);
            ListeColonne colonne = listeColonneRepository.findById(colonneId)
                    .orElseThrow(() -> new RuntimeException("Colonne introuvable: " + colonneId));

            colonne.setPosition(i);
            listeColonneRepository.save(colonne);
        }

        System.out.println("[ListeColonneService] " + nouvelOrdre.size() + " colonnes réorganisées");
    }

    public void supprimerColonne(Long id) {
        System.out.println("[ListeColonneService] Suppression colonne " + id);

        ListeColonne colonne = listeColonneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colonne introuvable: " + id));

        Optional<ListeColonne> colonneParDefaut = listeColonneRepository
                .findByProjetIdAndNom(colonne.getProjet().getId(), "À faire");

        if (colonneParDefaut.isPresent()) {
            List<Tache> taches = colonne.getTaches();
            for (Tache tache : taches) {
                tache.deplacerVersColonne(colonneParDefaut.get());
            }
        }

        listeColonneRepository.deleteById(id);
        System.out.println("[ListeColonneService] Colonne supprimée");
    }

    @Transactional(readOnly = true)
    public boolean projetADesColonnes(Long idProjet) {
        return listeColonneRepository.existsByProjetId(idProjet);
    }

    // ========== CONVERSION DTO ==========

    private ListeColonneDTO convertirEnDTO(ListeColonne listeColonne) {
        ListeColonneDTO dto = new ListeColonneDTO();
        dto.setId(listeColonne.getId());
        dto.setNom(listeColonne.getNom());
        dto.setPosition(listeColonne.getPosition());
        dto.setDateCreation(listeColonne.getDateCreation());

        dto.setIdProjet(listeColonne.getProjet().getId());
        dto.setNomProjet(listeColonne.getProjet().getTitre());

        List<TacheDTO> tachesDTO = listeColonne.getTaches().stream()
                .map(this::convertirTacheEnDTO)
                .collect(Collectors.toList());

        dto.setTaches(tachesDTO);
        dto.setNombreTaches(tachesDTO.size());

        return dto;
    }

    private TacheDTO convertirTacheEnDTO(Tache tache) {
        TacheDTO dto = new TacheDTO();
        dto.setId(tache.getId());
        dto.setTitre(tache.getTitre());
        dto.setDescription(tache.getDescription());
        dto.setStatut(tache.getStatut());
        dto.setPriorite(tache.getPriorite());
        dto.setDateEcheance(tache.getDateEcheance());
        dto.setDateCreation(tache.getDateCreation());

        dto.setIdProjet(tache.getProjet().getId());
        dto.setNomProjet(tache.getProjet().getTitre());

        if (tache.getAssigneA() != null) {
            dto.setIdAssigne(tache.getAssigneA().getId());
            dto.setNomAssigne(tache.getAssigneA().getNom());
            dto.setPrenomAssigne(tache.getAssigneA().getPrenom());
            dto.setEmailAssigne(tache.getAssigneA().getEmail());
        }

        return dto;
    }
}