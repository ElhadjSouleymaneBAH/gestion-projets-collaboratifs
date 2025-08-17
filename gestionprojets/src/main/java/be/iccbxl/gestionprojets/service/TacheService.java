package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.enums.StatutTache;
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
 * - F7 : Gérer les tâches (Chef de Projet, Membre)
 * - Utilisateurs : Chef de Projet, Membre
 * - Importance : 5/5
 * - Contraintes : Tâches assignées uniquement (pour les membres)
 *
 * Logique métier :
 * - Respect du diagramme d'état des tâches
 * - Assignation selon les permissions
 * - Validation des transitions de statut
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

        // Validation des données de base
        if (tacheDTO.getTitre() == null || tacheDTO.getTitre().trim().isEmpty()) {
            throw new RuntimeException("Le titre de la tâche est obligatoire");
        }

        if (tacheDTO.getIdProjet() == null) {
            throw new RuntimeException("L'ID du projet est obligatoire");
        }

        Tache tache = new Tache();

        // Chercher le projet dans la base
        Projet projet = projetRepository.findById(tacheDTO.getIdProjet())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec ID: " + tacheDTO.getIdProjet()));

        System.out.println("DEBUG: [F7] Projet trouvé: " + projet.getTitre());

        // Utiliser la logique métier de l'entité
        boolean creationReussie = tache.ajouterTache(
                tacheDTO.getTitre(),
                tacheDTO.getDescription(),
                projet
        );

        if (!creationReussie) {
            throw new RuntimeException("Impossible de créer la tâche : données invalides");
        }

        // Si la tâche est assignée à quelqu'un lors de la création
        if (tacheDTO.getIdAssigne() != null) {
            System.out.println("DEBUG: [F7] Assignation lors de la création à l'utilisateur: " + tacheDTO.getIdAssigne());

            Utilisateur utilisateur = utilisateurRepository.findById(tacheDTO.getIdAssigne())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + tacheDTO.getIdAssigne()));

            tache.assignerA(utilisateur);
            System.out.println("DEBUG: [F7] Tâche assignée à: " + utilisateur.getEmail());
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        System.out.println("SUCCESS: [F7] Tâche créée avec ID: " + tacheSauvee.getId() +
                ", Statut: " + tacheSauvee.getStatut());

        return convertirEnDTO(tacheSauvee);
    }

    // ========== F7 : CONSULTATION DE TÂCHES ==========

    /**
     * Récupère toutes les tâches (usage administrateur).
     *
     * @return la liste de toutes les tâches
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
     * Récupère une tâche par son ID.
     *
     * @param id l'identifiant de la tâche
     * @return la tâche si trouvée
     */
    @Transactional(readOnly = true)
    public Optional<TacheDTO> obtenirTacheParId(Long id) {
        System.out.println("DEBUG: [F7] Recherche tâche par ID: " + id);

        Optional<Tache> tache = tacheRepository.findById(id);
        if (tache.isPresent()) {
            System.out.println("DEBUG: [F7] Tâche trouvée: " + tache.get().getTitre() +
                    " (Statut: " + tache.get().getStatut() + ")");
        } else {
            System.out.println("DEBUG: [F7] Aucune tâche trouvée avec ID: " + id);
        }

        return tache.map(this::convertirEnDTO);
    }

    /**
     * Récupère les tâches d'un projet.
     *
     * @param idProjet l'identifiant du projet
     * @return la liste des tâches du projet
     */
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesParProjet(Long idProjet) {
        System.out.println("DEBUG: [F7] Recherche tâches pour projet ID: " + idProjet);

        List<Tache> taches = tacheRepository.findByProjetId(idProjet);
        System.out.println("DEBUG: [F7] " + taches.size() + " tâches trouvées pour le projet " + idProjet);

        return taches.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les tâches assignées à un utilisateur.
     *
     * @param idUtilisateur l'identifiant de l'utilisateur
     * @return la liste des tâches assignées
     */
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesParUtilisateur(Long idUtilisateur) {
        System.out.println("DEBUG: [F7] Recherche tâches assignées à l'utilisateur ID: " + idUtilisateur);

        List<Tache> taches = tacheRepository.findByAssigneAId(idUtilisateur);
        System.out.println("DEBUG: [F7] " + taches.size() + " tâches assignées à l'utilisateur " + idUtilisateur);

        return taches.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    // ========== F7 : MODIFICATION DE TÂCHES ==========

    /**
     * Modifie une tâche existante.
     *
     * @param id l'identifiant de la tâche
     * @param tacheDTO les nouvelles données
     * @return la tâche modifiée
     * @throws RuntimeException si la tâche n'existe pas ou si les données sont invalides
     */
    public TacheDTO modifierTache(Long id, TacheDTO tacheDTO) {
        System.out.println("DEBUG: [F7] Début modification tâche ID: " + id);

        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        System.out.println("DEBUG: [F7] Tâche existante trouvée: " + tache.getTitre() +
                " (Statut actuel: " + tache.getStatut() + ")");

        // Utiliser la logique métier de l'entité
        boolean modificationReussie = tache.modifierTache(
                id,
                tacheDTO.getTitre(),
                tacheDTO.getDescription(),
                tacheDTO.getStatut()
        );

        if (!modificationReussie) {
            throw new RuntimeException("Impossible de modifier la tâche : données invalides ou transition non autorisée");
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

    /**
     * Change le statut d'une tâche selon le diagramme d'état.
     *
     * @param id l'identifiant de la tâche
     * @param nouveauStatut le nouveau statut
     * @return la tâche avec le statut modifié
     * @throws RuntimeException si la transition n'est pas autorisée
     */
    public TacheDTO changerStatutTache(Long id, StatutTache nouveauStatut) {
        System.out.println("DEBUG: [F7] Changement statut tâche " + id + " vers " + nouveauStatut);

        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        StatutTache ancienStatut = tache.getStatut();
        System.out.println("DEBUG: [F7] Transition demandée: " + ancienStatut + " → " + nouveauStatut);

        // Utiliser la logique métier du diagramme d'état
        boolean changementReussi = tache.changerStatut(nouveauStatut);

        if (!changementReussi) {
            throw new RuntimeException("Transition de statut non autorisée selon le diagramme d'état: " +
                    ancienStatut + " → " + nouveauStatut);
        }

        Tache tacheSauvee = tacheRepository.save(tache);
        System.out.println("SUCCESS: [F7] Statut tâche " + id + " changé: " + ancienStatut + " → " + nouveauStatut);

        return convertirEnDTO(tacheSauvee);
    }

    // ========== F7 : SUPPRESSION DE TÂCHES ==========

    /**
     * Supprime une tâche.
     *
     * @param id l'identifiant de la tâche
     * @throws RuntimeException si la tâche n'existe pas ou ne peut être supprimée
     */
    public void supprimerTache(Long id) {
        System.out.println("DEBUG: [F7] Début suppression tâche ID: " + id);

        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec ID: " + id));

        System.out.println("DEBUG: [F7] Tâche trouvée pour suppression: " + tache.getTitre() +
                " (Statut: " + tache.getStatut() + ")");

        // Utiliser la logique métier pour nettoyer les relations
        boolean suppressionReussie = tache.supprimerTache(id);

        if (!suppressionReussie) {
            throw new RuntimeException("Impossible de supprimer la tâche ID: " + id);
        }

        tacheRepository.deleteById(id);
        System.out.println("SUCCESS: [F7] Tâche " + id + " supprimée avec succès");
    }

    // ========== F7 : MÉTHODES UTILITAIRES ==========

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

    // ========== F7 : MÉTHODES D'ANALYSE ==========

    /**
     * Compte les tâches par statut pour un projet donné.
     * Utile pour les statistiques des Chefs de Projet.
     */
    @Transactional(readOnly = true)
    public long compterTachesParStatut(Long idProjet, StatutTache statut) {
        System.out.println("DEBUG: [F7] Comptage tâches projet " + idProjet + " avec statut " + statut);

        long count = tacheRepository.findByProjetIdAndStatut(idProjet, statut).size();
        System.out.println("DEBUG: [F7] " + count + " tâches trouvées avec statut " + statut);

        return count;
    }

    /**
     * Obtient les tâches non assignées d'un projet.
     * Utile pour identifier les tâches à assigner.
     */
    @Transactional(readOnly = true)
    public List<TacheDTO> obtenirTachesNonAssignees(Long idProjet) {
        System.out.println("DEBUG: [F7] Recherche tâches non assignées pour projet " + idProjet);

        List<Tache> tachesProjet = tacheRepository.findByProjetId(idProjet);
        List<Tache> tachesNonAssignees = tachesProjet.stream()
                .filter(t -> t.getAssigneA() == null)
                .collect(Collectors.toList());

        System.out.println("DEBUG: [F7] " + tachesNonAssignees.size() + " tâches non assignées trouvées");

        return tachesNonAssignees.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un utilisateur a des tâches en cours.
     * Utile avant de supprimer un membre d'un projet.
     */
    @Transactional(readOnly = true)
    public boolean utilisateurATachesEnCours(Long idUtilisateur) {
        System.out.println("DEBUG: [F7] Vérification tâches en cours pour utilisateur " + idUtilisateur);

        List<Tache> taches = tacheRepository.findByAssigneAId(idUtilisateur);
        boolean aTachesEnCours = taches.stream()
                .anyMatch(t -> t.getStatut() == StatutTache.BROUILLON ||
                        t.getStatut() == StatutTache.EN_ATTENTE_VALIDATION);

        System.out.println("DEBUG: [F7] L'utilisateur " + idUtilisateur +
                (aTachesEnCours ? " a" : " n'a pas") + " de tâches en cours");

        return aTachesEnCours;
    }
}