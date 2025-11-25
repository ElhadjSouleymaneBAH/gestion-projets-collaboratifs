package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.model.ProjetUtilisateur;
import be.iccbxl.gestionprojets.model.ListeColonne;
import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.enums.PrioriteTache;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.repository.ProjetUtilisateurRepository;
import be.iccbxl.gestionprojets.repository.ListeColonneRepository;
import jakarta.annotation.PostConstruct;
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
    private final ListeColonneRepository listeColonneRepository;

    public TacheService(
            TacheRepository tacheRepository,
            ProjetRepository projetRepository,
            UtilisateurRepository utilisateurRepository,
            ProjetUtilisateurRepository projetUtilisateurRepository,
            ListeColonneRepository listeColonneRepository) {
        this.tacheRepository = tacheRepository;
        this.projetRepository = projetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.projetUtilisateurRepository = projetUtilisateurRepository;
        this.listeColonneRepository = listeColonneRepository;
    }

    // ========== CORRECTION AUTOMATIQUE AU DÉMARRAGE ==========
    @PostConstruct
    public void corrigerTachesSansColonneKanban() {
        System.out.println(" [Kanban] Vérification des tâches sans colonne au démarrage...");

        List<Tache> tachesSansColonne = tacheRepository.findAll()
                .stream()
                .filter(t -> t.getListeColonne() == null && t.getProjet() != null)
                .collect(Collectors.toList());

        if (tachesSansColonne.isEmpty()) {
            System.out.println(" [Kanban] Toutes les tâches ont une colonne assignée.");
            return;
        }

        System.out.println(" [Kanban] " + tachesSansColonne.size() + " tâches sans colonne détectées. Correction en cours...");

        for (Tache tache : tachesSansColonne) {
            assignerColonneSelonStatut(tache);
        }

        System.out.println(" [Kanban] Correction automatique terminée !");
    }

    // ========== ASSIGNER COLONNE SELON STATUT ==========
    private void assignerColonneSelonStatut(Tache tache) {
        if (tache.getProjet() == null) return;

        String nomColonne;
        switch (tache.getStatut()) {
            case TERMINE:
            case ANNULE:
                nomColonne = "Terminé";
                break;
            case BROUILLON:
            case EN_ATTENTE_VALIDATION:
            default:
                nomColonne = "À faire";
        }

        // S'assurer que les colonnes existent pour ce projet
        creerColonnesParDefaut(tache.getProjet());

        // Chercher et assigner la colonne
        Optional<ListeColonne> colonneOpt = listeColonneRepository
                .findByProjetIdAndNom(tache.getProjet().getId(), nomColonne);

        if (colonneOpt.isPresent()) {
            tache.setListeColonne(colonneOpt.get());
            tache.setPosition(0);
            tacheRepository.save(tache);
            System.out.println("    Tâche " + tache.getId() + " (" + tache.getTitre() + ") → " + nomColonne);
        }
    }

    // ========== ASSIGNER UNE TÂCHE (F7) ==========
    public TacheDTO assignerTache(Long tacheId, Long membreId) {
        System.out.println(" [F7] Assignation tâche " + tacheId + " à membre " + membreId);

        Tache tache = tacheRepository.findByIdWithRelations(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + tacheId));

        Utilisateur membre = utilisateurRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + membreId));

        Projet projet = tache.getProjet();
        Long projetId = projet.getId();

        //  Ajouter automatiquement le membre au projet si pas encore membre
        boolean appartientAuProjet = projetUtilisateurRepository
                .existsByProjetIdAndUtilisateurId(projetId, membreId);

        if (!appartientAuProjet) {
            System.out.println(" [F7] Membre " + membreId + " pas encore dans le projet " + projetId + " - Ajout automatique...");

            ProjetUtilisateur pu = new ProjetUtilisateur(projetId, membreId, "MEMBRE");
            projetUtilisateurRepository.save(pu);

            System.out.println(" [F7] Membre " + membre.getPrenom() + " " + membre.getNom() +
                    " ajouté automatiquement au projet " + projet.getTitre());
        }

        // Vérifier le statut de la tâche
        if (tache.getStatut() == StatutTache.TERMINE || tache.getStatut() == StatutTache.ANNULE) {
            throw new RuntimeException("Impossible d'assigner une tâche avec le statut : " + tache.getStatut());
        }

        //  S'assurer que la tâche a une colonne Kanban
        if (tache.getListeColonne() == null) {
            assignerColonneSelonStatut(tache);
        }

        // Assigner la tâche
        tache.assignerA(membre);
        Tache tacheUpdated = tacheRepository.save(tache);

        System.out.println(" [F7] Tâche " + tacheId + " assignée à " +
                membre.getPrenom() + " " + membre.getNom() + " (" + membre.getEmail() + ")");

        return convertirEnDTO(tacheUpdated);
    }

    // ========== CRÉER UNE TÂCHE (F7 + KANBAN) ==========
    public TacheDTO creerTache(TacheDTO tacheDTO) {
        System.out.println(" [F7] Création tâche: " + tacheDTO.getTitre());

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

        //  KANBAN: S'assurer que les colonnes existent
        creerColonnesParDefaut(projet);

        //  KANBAN: Assigner automatiquement à la colonne "À faire"
        Optional<ListeColonne> colonneAFaire = listeColonneRepository
                .findByProjetIdAndNom(projet.getId(), "À faire");

        if (colonneAFaire.isPresent()) {
            ListeColonne colonne = colonneAFaire.get();
            tache.setListeColonne(colonne);

            // Calculer la position (à la fin de la colonne)
            int nouvellePosition = colonne.getTaches() != null ? colonne.getTaches().size() : 0;
            tache.setPosition(nouvellePosition);

            System.out.println(" [Kanban] Tâche assignée à la colonne 'À faire' (id=" + colonne.getId() + ")");
        }

        Tache saved = tacheRepository.save(tache);
        System.out.println(" [F7] Tâche créée avec ID: " + saved.getId());

        return convertirEnDTO(saved);
    }

    // ========== CRÉER LES COLONNES PAR DÉFAUT ==========
    private void creerColonnesParDefaut(Projet projet) {
        if (!listeColonneRepository.existsByProjetId(projet.getId())) {
            System.out.println(" [Kanban] Création des 3 colonnes par défaut pour projet " + projet.getId());

            ListeColonne aFaire = new ListeColonne();
            aFaire.setNom("À faire");
            aFaire.setPosition(0);
            aFaire.setProjet(projet);
            listeColonneRepository.save(aFaire);

            ListeColonne enCours = new ListeColonne();
            enCours.setNom("En cours");
            enCours.setPosition(1);
            enCours.setProjet(projet);
            listeColonneRepository.save(enCours);

            ListeColonne termine = new ListeColonne();
            termine.setNom("Terminé");
            termine.setPosition(2);
            termine.setProjet(projet);
            listeColonneRepository.save(termine);

            System.out.println(" [Kanban] Colonnes créées pour projet " + projet.getId());
        }
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

        // S'assurer que la tâche a une colonne Kanban
        if (tache.getListeColonne() == null) {
            assignerColonneSelonStatut(tache);
        }

        return convertirEnDTO(tacheRepository.save(tache));
    }

    // ========== CHANGER STATUT ==========
    public TacheDTO changerStatutTache(Long id, StatutTache nouveauStatut) {
        Tache tache = tacheRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        boolean ok = tache.changerStatut(nouveauStatut);
        if (!ok) throw new RuntimeException("Transition de statut non autorisée");

        // KANBAN: Déplacer la tâche vers la colonne correspondante au statut
        deplacerTacheSelonStatut(tache, nouveauStatut);

        return convertirEnDTO(tacheRepository.save(tache));
    }

    // ========== DÉPLACER TÂCHE SELON STATUT (KANBAN) ==========
    private void deplacerTacheSelonStatut(Tache tache, StatutTache nouveauStatut) {
        if (tache.getProjet() == null) return;

        // S'assurer que les colonnes existent
        creerColonnesParDefaut(tache.getProjet());

        String nomColonne;
        switch (nouveauStatut) {
            case BROUILLON:
            case EN_ATTENTE_VALIDATION:
                nomColonne = "À faire";
                break;
            case TERMINE:
            case ANNULE:
                nomColonne = "Terminé";
                break;
            default:
                nomColonne = "À faire";
        }

        Optional<ListeColonne> colonneOpt = listeColonneRepository
                .findByProjetIdAndNom(tache.getProjet().getId(), nomColonne);

        if (colonneOpt.isPresent()) {
            tache.setListeColonne(colonneOpt.get());
            System.out.println(" [Kanban] Tâche " + tache.getId() + " déplacée vers '" + nomColonne + "'");
        }
    }

    // ========== ANNULER PAR ADMIN ==========
    public TacheDTO annulerParAdmin(Long tacheId) {
        System.out.println("DEBUG: [F7-ADMIN] Annulation de la tâche ID=" + tacheId);

        Tache tache = tacheRepository.findByIdWithRelations(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + tacheId));

        boolean ok = tache.changerStatut(StatutTache.ANNULE);
        if (!ok) throw new RuntimeException("Impossible d'annuler la tâche ID=" + tacheId);

        // Déplacer vers "Terminé" dans le Kanban
        deplacerTacheSelonStatut(tache, StatutTache.ANNULE);

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

    // ========== CONVERTIR EN DTO ==========
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
        } else {
            dto.setIdAssigne(null);
            dto.setNomAssigne(null);
            dto.setPrenomAssigne(null);
            dto.setEmailAssigne(null);
        }

        dto.setStatut(tache.getStatut());
        dto.setPriorite(tache.getPriorite());
        dto.setDateEcheance(tache.getDateEcheance());
        dto.setDateCreation(tache.getDateCreation());

        //KANBAN: Ajouter l'ID de la colonne
        if (tache.getListeColonne() != null) {
            dto.setIdListeColonne(tache.getListeColonne().getId());
        }

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