package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.enums.PrioriteTache;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.repository.ProjetUtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TacheService {

    private final TacheRepository tacheRepository;
    private final ProjetRepository projetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProjetUtilisateurRepository projetUtilisateurRepository;

    public TacheService(
            TacheRepository tacheRepository,
            ProjetRepository projetRepository,
            UtilisateurRepository utilisateurRepository,
            ProjetUtilisateurRepository projetUtilisateurRepository) {
        this.tacheRepository = tacheRepository;
        this.projetRepository = projetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.projetUtilisateurRepository = projetUtilisateurRepository;
    }

    // ==========  ASSIGNER UNE TÂCHE ==========
    public TacheDTO assignerTache(Long tacheId, Long membreId) {
        System.out.println("DEBUG: [F7] Assignation tâche " + tacheId + " à membre " + membreId);

        Tache tache = tacheRepository.findByIdWithRelations(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + tacheId));

        Utilisateur membre = utilisateurRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + membreId));

        Long projetId = tache.getProjet().getId();
        boolean appartientAuProjet = projetUtilisateurRepository
                .existsByProjetIdAndUtilisateurId(projetId, membreId);

        if (!appartientAuProjet) {
            throw new RuntimeException("L'utilisateur " + membre.getPrenom() + " " + membre.getNom() +
                    " n'appartient pas au projet. Veuillez d'abord l'ajouter comme membre.");
        }

        if (tache.getStatut() == StatutTache.TERMINE || tache.getStatut() == StatutTache.ANNULE) {
            throw new RuntimeException("Impossible d'assigner une tâche avec le statut : " + tache.getStatut());
        }

        tache.assignerA(membre);
        Tache tacheUpdated = tacheRepository.save(tache);

        System.out.println("SUCCESS: [F7] Tâche " + tacheId + " assignée à " +
                membre.getPrenom() + " " + membre.getNom() + " (" + membre.getEmail() + ")");

        return convertirEnDTO(tacheUpdated);
    }

    // ========== CRÉER ==========
    public TacheDTO creerTache(TacheDTO tacheDTO) {
        if (tacheDTO.getTitre() == null || tacheDTO.getTitre().trim().isEmpty()) {
            throw new RuntimeException("Le titre de la tâche est obligatoire");
        }

        if (tacheDTO.getIdProjet() == null) {
            throw new RuntimeException("L'ID du projet est obligatoire");
        }

        Projet projet = projetRepository.findById(tacheDTO.getIdProjet())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec ID: " + tacheDTO.getIdProjet()));

        Tache tache = new Tache();
        boolean creationReussie = tache.ajouterTache(tacheDTO.getTitre(), tacheDTO.getDescription(), projet);

        if (!creationReussie) {
            throw new RuntimeException("Impossible de créer la tâche : données invalides");
        }

        tache.setPriorite(tacheDTO.getPriorite() != null ? tacheDTO.getPriorite() : PrioriteTache.NORMALE);
        tache.setDateEcheance(tacheDTO.getDateEcheance());
        tache.assignerA(null);

        Tache saved = tacheRepository.save(tache);
        return convertirEnDTO(saved);
    }

    // ========== CONSULTER ==========
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirToutesLesTaches() {
        return tacheRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TacheDTO> obtenirTacheParId(Long id) {
        return tacheRepository.findByIdWithRelations(id).map(this::convertirEnDTO);
    }

    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesParProjet(Long idProjet) {
        return tacheRepository.findByProjetIdWithRelations(idProjet)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesParUtilisateur(Long idUtilisateur) {
        return tacheRepository.findByAssigneAIdWithRelations(idUtilisateur)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    // ========== MODIFIER ==========
    public TacheDTO modifierTache(Long id, TacheDTO tacheDTO) {
        Tache tache = tacheRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        boolean modifie = tache.modifierTache(tacheDTO.getTitre(), tacheDTO.getDescription(), tacheDTO.getStatut());
        if (!modifie) throw new RuntimeException("Impossible de modifier la tâche : données invalides");

        tache.setPriorite(tacheDTO.getPriorite());
        tache.setDateEcheance(tacheDTO.getDateEcheance());

        if (tacheDTO.getIdAssigne() != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(tacheDTO.getIdAssigne())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + tacheDTO.getIdAssigne()));
            tache.assignerA(utilisateur);
        } else {
            tache.assignerA(null);
        }

        return convertirEnDTO(tacheRepository.save(tache));
    }

    // ========== CHANGER STATUT ==========
    public TacheDTO changerStatutTache(Long id, StatutTache nouveauStatut) {
        Tache tache = tacheRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        boolean ok = tache.changerStatut(nouveauStatut);
        if (!ok) throw new RuntimeException("Transition de statut non autorisée");

        return convertirEnDTO(tacheRepository.save(tache));
    }

    // ========== ANNULER PAR ADMIN ==========
    public TacheDTO annulerParAdmin(Long tacheId) {
        System.out.println("DEBUG: [F7-ADMIN] Annulation de la tâche ID=" + tacheId);

        Tache tache = tacheRepository.findByIdWithRelations(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + tacheId));

        boolean ok = tache.changerStatut(StatutTache.ANNULE);
        if (!ok) throw new RuntimeException("Impossible d'annuler la tâche ID=" + tacheId);

        Tache saved = tacheRepository.save(tache);
        System.out.println("SUCCESS: [F7-ADMIN] Tâche " + tacheId + " annulée.");
        return convertirEnDTO(saved);
    }

    // ========== SUPPRIMER ==========
    public void supprimerTache(Long id) {
        Tache tache = tacheRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        if (!tache.supprimerTache()) {
            throw new RuntimeException("Impossible de supprimer la tâche ID: " + id);
        }

        tacheRepository.deleteById(id);
    }

    // ========== CONVERTIR ==========
    private TacheDTO convertirEnDTO(Tache tache) {
        TacheDTO dto = new TacheDTO();
        dto.setId(tache.getId());
        dto.setTitre(tache.getTitre());
        dto.setDescription(tache.getDescription());

        if (tache.getProjet() != null) {
            dto.setIdProjet(tache.getProjet().getId());
            dto.setNomProjet(tache.getProjet().getTitre());
        }

        if (tache.getAssigneA() != null) {
            Utilisateur assigne = tache.getAssigneA();
            dto.setIdAssigne(assigne.getId());
            dto.setNomAssigne(assigne.getPrenom() + " " + assigne.getNom());
            dto.setPrenomAssigne(assigne.getPrenom());
            dto.setEmailAssigne(assigne.getEmail());
        }

        dto.setStatut(tache.getStatut());
        dto.setPriorite(tache.getPriorite());
        dto.setDateEcheance(tache.getDateEcheance());
        dto.setDateCreation(tache.getDateCreation());
        return dto;
    }

    // ========== TÂCHES PAR CHEF DE PROJET ==========
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesParChefDeProjet(Long idChef) {
        List<Projet> projets = projetRepository.findByChefDeProjetId(idChef);
        if (projets.isEmpty()) return List.of();

        List<Long> idsProjets = projets.stream().map(Projet::getId).toList();
        List<Tache> taches = tacheRepository.findByProjetIdInWithRelations(idsProjets);

        return taches.stream().map(this::convertirEnDTO).collect(Collectors.toList());
    }

}
