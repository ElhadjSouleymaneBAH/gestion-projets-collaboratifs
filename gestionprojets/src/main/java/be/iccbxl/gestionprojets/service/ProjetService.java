package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.ProjetDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.model.ProjetUtilisateur;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.repository.ProjetUtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des projets selon le cahier des charges.
 *
 * Implémente les fonctionnalités définies dans le cahier des charges :
 * - F3 : Consulter les projets publics (Visiteur)
 * - F6 : Gérer les projets (Chef de Projet)
 * - F8 : Ajouter des membres à un projet (Chef de Projet)
 * - F9 : Support collaboration temps réel (Membre, Chef de Projet)
 *
 * Logique métier F8 :
 * - Chef de Projet peut ajouter/retirer des membres
 * - Contraintes : Membres existants (pas de doublons)
 * - Importance : 4/5
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Service
@Transactional
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProjetUtilisateurRepository projetUtilisateurRepository;

    public ProjetService(ProjetRepository projetRepository,
                         UtilisateurRepository utilisateurRepository,
                         ProjetUtilisateurRepository projetUtilisateurRepository) {
        this.projetRepository = projetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.projetUtilisateurRepository = projetUtilisateurRepository;
    }

    // ========== MÉTHODES DE BASE ==========

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
    @Transactional(readOnly = true)
    public List<ProjetDTO> obtenirTousProjets() {
        return projetRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir un projet par ID
     */
    @Transactional(readOnly = true)
    public Optional<ProjetDTO> obtenirProjetParId(Long id) {
        return projetRepository.findById(id)
                .map(this::convertirEnDTO);
    }

    /**
     * Trouver un projet par ID avec Optional - Support F9
     * Utilisé pour F9 : Collaboration en temps réel
     */
    @Transactional(readOnly = true)
    public Optional<Projet> findById(Long id) {
        return projetRepository.findById(id);
    }

    /**
     * Obtenir les projets d'un créateur
     */
    @Transactional(readOnly = true)
    public List<ProjetDTO> obtenirProjetsParCreateur(Long idCreateur) {
        return projetRepository.findByIdCreateur(idCreateur)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les projets auxquels un utilisateur participe
     * Inclut les projets créés + les projets où il est membre (F8)
     *
     * MÉTHODE AJOUTÉE - Corrige l'erreur de compilation ProjetController ligne 128
     *
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des projets de l'utilisateur
     */
    @Transactional(readOnly = true)
    public List<ProjetDTO> obtenirProjetsParUtilisateur(Long utilisateurId) {
        System.out.println("DEBUG: [F6/F8] Recherche projets pour utilisateur ID: " + utilisateurId);

        // Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(utilisateurId)) {
            System.out.println("ERROR: [F6] Utilisateur non trouvé: " + utilisateurId);
            throw new RuntimeException("Utilisateur non trouvé avec ID: " + utilisateurId);
        }

        // 1. Projets créés par l'utilisateur (F6 - Chef de Projet)
        List<Projet> projetsCreees = projetRepository.findByIdCreateur(utilisateurId);
        System.out.println("DEBUG: [F6] " + projetsCreees.size() + " projets créés par l'utilisateur");

        // 2. Projets où l'utilisateur est membre (F8 - Membre ajouté par Chef de Projet)
        List<Long> projetIdsCommeMembre = projetUtilisateurRepository.findProjetIdsByUtilisateurId(utilisateurId);
        List<Projet> projetsCommeMembre = projetRepository.findAllById(projetIdsCommeMembre);
        System.out.println("DEBUG: [F8] " + projetsCommeMembre.size() + " projets où l'utilisateur est membre");

        // 3. Combiner et éliminer les doublons (un Chef de Projet ne doit pas voir son projet 2 fois)
        List<Projet> tousProjets = new ArrayList<>();
        tousProjets.addAll(projetsCreees);

        // Ajouter les projets de membre qui ne sont pas déjà dans la liste
        for (Projet projet : projetsCommeMembre) {
            if (!tousProjets.stream().anyMatch(p -> p.getId().equals(projet.getId()))) {
                tousProjets.add(projet);
            }
        }

        System.out.println("DEBUG: [F6/F8] Total " + tousProjets.size() + " projets pour utilisateur " + utilisateurId);

        return tousProjets.stream()
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
        if (!projetRepository.existsById(id)) {
            throw new RuntimeException("Projet non trouvé");
        }

        // Supprimer d'abord les associations projet-utilisateur
        projetUtilisateurRepository.deleteByProjetId(id);

        // Puis supprimer le projet
        projetRepository.deleteById(id);
    }

    // ========== MÉTHODES SUPPLÉMENTAIRES POUR LE CONTRÔLEUR ==========

    /**
     * Créer un nouveau projet avec email du créateur
     * Version surchargée pour le contrôleur
     */
    public ProjetDTO creerProjet(ProjetDTO projetDTO, String emailCreateur) {
        // Récupérer l'utilisateur créateur
        Utilisateur createur = utilisateurRepository.findByEmail(emailCreateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur créateur non trouvé"));

        // Créer le projet
        Projet projet = new Projet();
        projet.setTitre(projetDTO.getTitre());
        projet.setDescription(projetDTO.getDescription());
        projet.setIdCreateur(createur.getId());
        projet.setStatut("ACTIF");

        Projet projetSauve = projetRepository.save(projet);
        return convertirEnDTO(projetSauve);
    }

    /**
     * Vérifier si un utilisateur peut modifier un projet
     * Méthode d'autorisation pour F6
     */
    @Transactional(readOnly = true)
    public boolean utilisateurPeutModifierProjet(Long projetId, String emailUtilisateur) {
        Optional<Projet> projetOpt = projetRepository.findById(projetId);
        if (projetOpt.isEmpty()) {
            return false;
        }

        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailUtilisateur)
                .orElse(null);
        if (utilisateur == null) {
            return false;
        }

        Projet projet = projetOpt.get();
        // L'utilisateur peut modifier s'il est le créateur
        return projet.getIdCreateur().equals(utilisateur.getId());
    }

    /**
     * Vérifier si un utilisateur est membre d'un projet
     * Utilisé pour F9 : Collaboration en temps réel
     */
    @Transactional(readOnly = true)
    public boolean estMembreDuProjet(Long utilisateurId, Long projetId) {
        // Vérifier si l'utilisateur est le créateur du projet
        Optional<Projet> projetOpt = projetRepository.findById(projetId);
        if (projetOpt.isPresent()) {
            Projet projet = projetOpt.get();
            if (projet.getIdCreateur().equals(utilisateurId)) {
                return true;
            }
        }

        // Vérifier si l'utilisateur est membre via projet_utilisateurs
        return projetUtilisateurRepository.existsByProjetIdAndUtilisateurIdAndActif(
                projetId, utilisateurId, true
        );
    }

    /**
     * Ajouter un membre à un projet
     * Fonctionnalité F8 du cahier des charges
     */
    public void ajouterMembreAuProjet(Long projetId, Long utilisateurId) {
        // Vérifier que le projet existe
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        // Vérifier que l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur n'est pas déjà membre
        if (projetUtilisateurRepository.existsByProjetIdAndUtilisateurId(projetId, utilisateurId)) {
            throw new RuntimeException("L'utilisateur est déjà membre de ce projet");
        }

        // Créer l'association projet-utilisateur
        ProjetUtilisateur projetUtilisateur = new ProjetUtilisateur();
        projetUtilisateur.setProjetId(projetId);
        projetUtilisateur.setUtilisateurId(utilisateurId);
        projetUtilisateur.setRole("MEMBRE");
        projetUtilisateur.setActif(true);

        projetUtilisateurRepository.save(projetUtilisateur);
    }

    /**
     * Retirer un membre d'un projet
     * Extension de F8 pour la gestion complète
     */
    public void retirerMembreDuProjet(Long projetId, Long utilisateurId) {
        // Vérifier que le projet existe
        if (!projetRepository.existsById(projetId)) {
            throw new RuntimeException("Projet non trouvé");
        }

        // Vérifier que l'utilisateur est membre du projet
        if (!projetUtilisateurRepository.existsByProjetIdAndUtilisateurId(projetId, utilisateurId)) {
            throw new RuntimeException("L'utilisateur n'est pas membre de ce projet");
        }

        // Supprimer l'association
        projetUtilisateurRepository.deleteByProjetIdAndUtilisateurId(projetId, utilisateurId);
    }

    /**
     * Obtenir les membres d'un projet
     * Support de F8 pour visualiser les membres
     */
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> obtenirMembresProjet(Long projetId) {
        // Vérifier que le projet existe
        if (!projetRepository.existsById(projetId)) {
            throw new RuntimeException("Projet non trouvé");
        }

        // Récupérer les IDs des utilisateurs membres
        List<Long> utilisateurIds = projetUtilisateurRepository.findUtilisateurIdsByProjetId(projetId);

        // Récupérer les utilisateurs et les convertir en DTO
        return utilisateurRepository.findAllById(utilisateurIds)
                .stream()
                .map(this::convertirUtilisateurEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Vérifier si un utilisateur est membre d'un projet
     * Utile pour les autorisations et l'interface
     */
    @Transactional(readOnly = true)
    public boolean utilisateurEstMembreProjet(Long projetId, Long utilisateurId) {
        return projetUtilisateurRepository.existsByProjetIdAndUtilisateurId(projetId, utilisateurId);
    }

    // ========== MÉTHODES DE CONVERSION ==========

    /**
     * Conversion Projet vers ProjetDTO
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

    /**
     * Conversion Utilisateur vers UtilisateurDTO
     */
    private UtilisateurDTO convertirUtilisateurEnDTO(Utilisateur utilisateur) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setRole(utilisateur.getRole());
        dto.setLangue(utilisateur.getLangue());
        dto.setDateInscription(utilisateur.getDateInscription());
        return dto;
    }
}