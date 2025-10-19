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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service de gestion des tâches.
 *
 * Implémente la fonctionnalité F7 du cahier des charges :
 * - F7 : Gérer les tâches (Chef de Projet, Membre, Administrateur)
 * - Utilisateurs : Chef de Projet, Membre, Administrateur
 * - Importance : 5/5
 * - Contraintes : Tâches assignées uniquement (pour les membres)
 *
 * Logique métier :
 * - Respect du diagramme d'état des tâches
 * - Assignation selon les permissions
 * - Validation des transitions de statut
 * - Admin peut annuler à tout moment
 * - Gestion des priorités et dates d'échéance
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Service
@Transactional
public class TacheService {

    private final TacheRepository tacheRepository;
    private final ProjetRepository projetRepository;
    private final UtilisateurRepository utilisateurRepository;

    public TacheService(TacheRepository tacheRepository, ProjetRepository projetRepository, UtilisateurRepository utilisateurRepository) {
        this.tacheRepository = tacheRepository;
        this.projetRepository = projetRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // ========== F7 : CRÉATION DE TÂCHES ==========

    /**
     * Crée une nouvelle tâche.
     *
     * @param tacheDTO les données de la tâche
     * @return la tâche créée
     * @throws RuntimeException si le projet n'existe pas ou données invalides
     */
    public TacheDTO creerTache(TacheDTO tacheDTO) {
        System.out.println("DEBUG: [F7] Début création tâche - Titre: " + tacheDTO.getTitre() +
                ", Projet: " + tacheDTO.getIdProjet());

        if (tacheDTO.getTitre() == null || tacheDTO.getTitre().trim().isEmpty()) {
            throw new RuntimeException("Le titre de la tâche est obligatoire");
        }

        if (tacheDTO.getIdProjet() == null) {
            throw new RuntimeException("L'ID du projet est obligatoire");
        }

        Tache tache = new Tache();

        Projet projet = projetRepository.findById(tacheDTO.getIdProjet())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec ID: " + tacheDTO.getIdProjet()));

        System.out.println("DEBUG: [F7] Projet trouvé: " + projet.getTitre());

        boolean creationReussie = tache.ajouterTache(
                tacheDTO.getTitre(),
                tacheDTO.getDescription(),
                projet
        );

        if (!creationReussie) {
            throw new RuntimeException("Impossible de créer la tâche : données invalides");
        }

        // Définir la priorité (par défaut NORMALE)
        if (tacheDTO.getPriorite() != null) {
            tache.setPriorite(tacheDTO.getPriorite());
        } else {
            tache.setPriorite(PrioriteTache.NORMALE);
        }

        // Définir la date d'échéance (optionnelle)
        if (tacheDTO.getDateEcheance() != null) {
            tache.setDateEcheance(tacheDTO.getDateEcheance());
        }

        // Assigner à un utilisateur (optionnel)
        if (tacheDTO.getIdAssigne() != null) {
            System.out.println("DEBUG: [F7] Assignation lors de la création à l'utilisateur: " + tacheDTO.getIdAssigne());

            Utilisateur utilisateur = utilisateurRepository.findById(tacheDTO.getIdAssigne())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + tacheDTO.getIdAssigne()));

            tache.assignerA(utilisateur);
            System.out.println("DEBUG: [F7] Tâche assignée à: " + utilisateur.getEmail());
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        System.out.println("SUCCESS: [F7] Tâche créée avec ID: " + tacheSauvee.getId() +
                ", Statut: " + tacheSauvee.getStatut() + ", Priorité: " + tacheSauvee.getPriorite());

        return convertirEnDTO(tacheSauvee);
    }

    // ========== F7 : CONSULTATION DE TÂCHES ==========

    /**
     * Récupère toutes les tâches (usage administrateur).
     */
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirToutesLesTaches() {
        System.out.println("DEBUG: [F7] Consultation toutes tâches (ADMIN)");

        List<Tache> taches = tacheRepository.findAll();
        System.out.println("DEBUG: [F7] " + taches.size() + " tâches trouvées au total");

        return taches.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une tâche par son ID avec JOIN FETCH
     */
    @Transactional(readOnly = true)
    public Optional<TacheDTO> obtenirTacheParId(Long id) {
        System.out.println("DEBUG: [F7] Recherche tâche par ID: " + id);

        Optional<Tache> tache = tacheRepository.findByIdWithRelations(id);
        if (tache.isPresent()) {
            System.out.println("DEBUG: [F7] Tâche trouvée: " + tache.get().getTitre() +
                    " (Statut: " + tache.get().getStatut() + ")");
        } else {
            System.out.println("DEBUG: [F7] Aucune tâche trouvée avec ID: " + id);
        }

        return tache.map(this::convertirEnDTO);
    }

    /**
     * Récupère les tâches d'un projet avec JOIN FETCH
     */
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesParProjet(Long idProjet) {
        System.out.println("DEBUG: [F7] Recherche tâches pour projet ID: " + idProjet);

        List<Tache> taches = tacheRepository.findByProjetIdWithRelations(idProjet);
        System.out.println("DEBUG: [F7] " + taches.size() + " tâches trouvées pour le projet " + idProjet);

        return taches.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les tâches assignées à un utilisateur avec JOIN FETCH
     */
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesParUtilisateur(Long idUtilisateur) {
        System.out.println("DEBUG: [F7] Recherche tâches assignées à l'utilisateur ID: " + idUtilisateur);

        List<Tache> taches = tacheRepository.findByAssigneAIdWithRelations(idUtilisateur);
        System.out.println("DEBUG: [F7] " + taches.size() + " tâches assignées à l'utilisateur " + idUtilisateur);

        return taches.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les tâches en retard d'un projet
     */
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesEnRetard(Long idProjet) {
        System.out.println("DEBUG: [F7] Recherche tâches en retard pour projet ID: " + idProjet);

        List<Tache> tachesProjet = tacheRepository.findByProjetIdWithRelations(idProjet);
        List<Tache> tachesEnRetard = tachesProjet.stream()
                .filter(Tache::estEnRetard)
                .collect(Collectors.toList());

        System.out.println("DEBUG: [F7] " + tachesEnRetard.size() + " tâches en retard trouvées");

        return tachesEnRetard.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    // ========== F7 : MODIFICATION DE TÂCHES ==========

    public TacheDTO modifierTache(Long id, TacheDTO tacheDTO) {
        System.out.println("DEBUG: [F7] Début modification tâche ID: " + id);

        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        System.out.println("DEBUG: [F7] Tâche existante trouvée: " + tache.getTitre() +
                " (Statut actuel: " + tache.getStatut() + ")");

        // Modification des champs de base
        boolean modificationReussie = tache.modifierTache(
                tacheDTO.getTitre(),
                tacheDTO.getDescription(),
                tacheDTO.getStatut()
        );

        if (!modificationReussie) {
            throw new RuntimeException("Impossible de modifier la tâche : données invalides ou transition non autorisée");
        }

        // Mise à jour de la priorité (si fournie)
        if (tacheDTO.getPriorite() != null) {
            tache.setPriorite(tacheDTO.getPriorite());
            System.out.println("DEBUG: [F7] Priorité mise à jour: " + tacheDTO.getPriorite());
        }

        // Mise à jour de la date d'échéance (si fournie)
        if (tacheDTO.getDateEcheance() != null) {
            tache.setDateEcheance(tacheDTO.getDateEcheance());
            System.out.println("DEBUG: [F7] Date d'échéance mise à jour: " + tacheDTO.getDateEcheance());
        }

        // Gestion de l'assignation
        if (tacheDTO.getIdAssigne() != null) {
            System.out.println("DEBUG: [F7] Assignation/réassignation à l'utilisateur: " + tacheDTO.getIdAssigne());

            Utilisateur utilisateur = utilisateurRepository.findById(tacheDTO.getIdAssigne())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + tacheDTO.getIdAssigne()));

            tache.assignerA(utilisateur);
            System.out.println("DEBUG: [F7] Tâche assignée à: " + utilisateur.getEmail());
        } else {
            System.out.println("DEBUG: [F7] Désassignation de la tâche");
            tache.assignerA(null);
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        System.out.println("SUCCESS: [F7] Tâche " + id + " modifiée avec succès (Nouveau statut: " +
                tacheSauvee.getStatut() + ")");

        return convertirEnDTO(tacheSauvee);
    }

    // ========== F7 : GESTION DES STATUTS ==========

    public TacheDTO changerStatutTache(Long id, StatutTache nouveauStatut) {
        System.out.println("DEBUG: [F7] Changement statut tâche " + id + " vers " + nouveauStatut);

        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        StatutTache ancienStatut = tache.getStatut();
        System.out.println("DEBUG: [F7] Transition demandée: " + ancienStatut + " → " + nouveauStatut);

        boolean changementReussi = tache.changerStatut(nouveauStatut);

        if (!changementReussi) {
            throw new RuntimeException("Transition de statut non autorisée selon le diagramme d'état: " +
                    ancienStatut + " → " + nouveauStatut);
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        System.out.println("SUCCESS: [F7] Statut tâche " + id + " changé: " + ancienStatut + " → " + nouveauStatut);

        return convertirEnDTO(tacheSauvee);
    }

    // ========== F7 : ANNULATION PAR ADMIN ==========

    /**
     * Annule une tâche par un administrateur.
     * Conforme à F7 : "Administrateur (à tout moment)"
     *
     * L'administrateur peut annuler n'importe quelle tâche quel que soit son statut,
     * conformément au workflow défini dans le cahier des charges.
     *
     * @param tacheId L'identifiant de la tâche à annuler
     * @return La tâche annulée
     * @throws RuntimeException si la tâche n'existe pas
     */
    public TacheDTO annulerParAdmin(Long tacheId) {
        System.out.println("DEBUG: [F7-ADMIN] Admin annule tâche ID: " + tacheId);

        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + tacheId));

        StatutTache ancienStatut = tache.getStatut();
        System.out.println("DEBUG: [F7-ADMIN] Statut actuel: " + ancienStatut);

        // L'admin peut annuler à tout moment selon F7
        boolean reussi = tache.changerStatut(StatutTache.ANNULE);

        if (!reussi) {
            throw new RuntimeException("Impossible d'annuler la tâche ID: " + tacheId);
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        System.out.println("SUCCESS: [F7-ADMIN] Admin a annulé tâche " + tacheId +
                " (" + ancienStatut + " → ANNULE)");

        return convertirEnDTO(tacheSauvee);
    }

    // ========== F7 : SUPPRESSION DE TÂCHES ==========

    public void supprimerTache(Long id) {
        System.out.println("DEBUG: [F7] Début suppression tâche ID: " + id);

        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        System.out.println("DEBUG: [F7] Tâche trouvée pour suppression: " + tache.getTitre() +
                " (Statut: " + tache.getStatut() + ")");

        boolean suppressionReussie = tache.supprimerTache();

        if (!suppressionReussie) {
            throw new RuntimeException("Impossible de supprimer la tâche ID: " + id);
        }

        tacheRepository.deleteById(id);
        System.out.println("SUCCESS: [F7] Tâche " + id + " supprimée avec succès");
    }

    // ========== F7 : MÉTHODES UTILITAIRES ==========

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
        dto.setPriorite(tache.getPriorite());
        dto.setDateEcheance(tache.getDateEcheance());
        dto.setDateCreation(tache.getDateCreation());

        return dto;
    }

    // ========== F7 : MÉTHODES D'ANALYSE ==========

    @Transactional(readOnly = true)
    public long compterTachesParStatut(Long idProjet, StatutTache statut) {
        System.out.println("DEBUG: [F7] Comptage tâches projet " + idProjet + " avec statut " + statut);

        long count = tacheRepository.findByProjetIdAndStatut(idProjet, statut).size();
        System.out.println("DEBUG: [F7] " + count + " tâches trouvées avec statut " + statut);

        return count;
    }

    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesNonAssignees(Long idProjet) {
        System.out.println("DEBUG: [F7] Recherche tâches non assignées pour projet " + idProjet);

        List<Tache> tachesProjet = tacheRepository.findByProjetIdWithRelations(idProjet);
        List<Tache> tachesNonAssignees = tachesProjet.stream()
                .filter(t -> t.getAssigneA() == null)
                .collect(Collectors.toList());

        System.out.println("DEBUG: [F7] " + tachesNonAssignees.size() + " tâches non assignées trouvées");

        return tachesNonAssignees.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean utilisateurATachesEnCours(Long idUtilisateur) {
        System.out.println("DEBUG: [F7] Vérification tâches en cours pour utilisateur " + idUtilisateur);

        List<Tache> taches = tacheRepository.findByAssigneAIdWithRelations(idUtilisateur);
        boolean aTachesEnCours = taches.stream()
                .anyMatch(t -> t.getStatut() == StatutTache.BROUILLON ||
                        t.getStatut() == StatutTache.EN_ATTENTE_VALIDATION);

        System.out.println("DEBUG: [F7] L'utilisateur " + idUtilisateur +
                (aTachesEnCours ? " a" : " n'a pas") + " de tâches en cours");

        return aTachesEnCours;
    }
}