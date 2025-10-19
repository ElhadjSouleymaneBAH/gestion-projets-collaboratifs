package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.InscriptionDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.enums.Role;
import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.ProjetUtilisateurRepository;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des utilisateurs selon le cahier des charges.
 *
 * Implémente les fonctionnalités avec la logique métier correcte :
 * - F1 : S'inscrire (VISITEUR par défaut)
 * - F4 : Consulter son profil
 * - F5 : Mettre à jour son profil
 * - F8 : Gestion des membres (VISITEUR → MEMBRE via Chef de Projet)
 * - F9 : Support collaboration temps réel
 * - F10 : Gestion des abonnements (MEMBRE → CHEF_PROJET)
 *
 * Logique des rôles :
 * 1. VISITEUR : Utilisateur inscrit, peut voir projets publics et être découvert
 * 2. MEMBRE : VISITEUR ajouté à un projet par un Chef de Projet
 * 3. CHEF_PROJET : MEMBRE qui a souscrit un abonnement
 * 4. ADMINISTRATEUR : Gestion globale de la plateforme
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjetRepository projetRepository;
    private final ProjetUtilisateurRepository projetUtilisateurRepository;
    private final TacheRepository tacheRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              PasswordEncoder passwordEncoder,
                              ProjetRepository projetRepository,
                              ProjetUtilisateurRepository projetUtilisateurRepository,
                              TacheRepository tacheRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.projetRepository = projetRepository;
        this.projetUtilisateurRepository = projetUtilisateurRepository;
        this.tacheRepository = tacheRepository;
    }

    // ========== MÉTHODES DE BASE ==========

    /**
     * 🔧 MÉTHODE AJOUTÉE : Sauvegarde ou met à jour un utilisateur.
     *
     * CRITIQUE pour F10 : Permet la mise à jour du rôle lors de la souscription/résiliation d'abonnement.
     *
     * Utilisations :
     * - AbonnementController : Promotion MEMBRE → CHEF_PROJET lors de la souscription
     * - AbonnementController : Rétrogradation CHEF_PROJET → MEMBRE lors de la résiliation
     * - Mise à jour du profil utilisateur (F5)
     *
     * @param utilisateur L'utilisateur à sauvegarder (avec modifications éventuelles de rôle)
     * @return L'utilisateur sauvegardé avec les modifications persistées
     */
    @Transactional
    public Utilisateur save(Utilisateur utilisateur) {
        System.out.println("DEBUG: [save] Sauvegarde utilisateur ID: " + utilisateur.getId() +
                " - Rôle: " + utilisateur.getRole());

        Utilisateur resultat = utilisateurRepository.save(utilisateur);

        System.out.println("SUCCESS: [save] Utilisateur sauvegardé avec succès - ID: " + resultat.getId());
        return resultat;
    }

    /**
     * Obtient tous les utilisateurs (usage administrateur).
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirTous() {
        return utilisateurRepository.findAll();
    }

    /**
     * Obtient les utilisateurs disponibles pour F8 (recherche de membres).
     * Utilisé par les Chefs de Projet pour découvrir des utilisateurs à ajouter.
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirTousLesUtilisateursSansProjet() {
        return utilisateurRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.VISITEUR || u.getRole() == Role.MEMBRE || u.getRole() == Role.CHEF_PROJET)
                .collect(Collectors.toList());
    }

    /**
     * Obtient les utilisateurs disponibles pour être ajoutés à un projet.
     *
     * Fonctionnalité F8 : Ajouter des membres à un projet
     * - Contrainte : "Membres existants" (éviter doublons)
     * - Retourne les VISITEUR et MEMBRE qui ne sont pas déjà dans le projet
     *
     * @param projetId ID du projet pour éviter les doublons
     * @return Liste des utilisateurs DTO disponibles pour ce projet
     */
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> obtenirUtilisateursDisponiblesPourProjet(Long projetId) {
        System.out.println("DEBUG: [F8] Recherche utilisateurs disponibles pour projet " + projetId);

        try {
            // 1. Récupérer tous les utilisateurs VISITEUR et MEMBRE
            List<Utilisateur> utilisateursDisponibles = utilisateurRepository.findAll()
                    .stream()
                    .filter(u -> u.getRole() == Role.VISITEUR || u.getRole() == Role.MEMBRE)
                    .collect(Collectors.toList());

            // 2. Exclure ceux déjà membres du projet
            List<Long> membresExistants = projetUtilisateurRepository.findUtilisateurIdsByProjetId(projetId);

            List<UtilisateurDTO> utilisateursFiltrés = utilisateursDisponibles.stream()
                    .filter(u -> !membresExistants.contains(u.getId()))
                    .map(this::convertirEnDTO)
                    .collect(Collectors.toList());

            System.out.println("SUCCESS: [F8] " + utilisateursFiltrés.size() + " utilisateurs disponibles pour le projet " + projetId);

            return utilisateursFiltrés;

        } catch (Exception e) {
            System.out.println("ERROR: [F8] Erreur recherche utilisateurs disponibles: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Vérifie si un utilisateur a des tâches en cours.
     *
     * Utilisé avant de retirer un membre d'un projet pour éviter
     * de laisser des tâches orphelines.
     *
     * @param utilisateurId ID de l'utilisateur à vérifier
     * @return true si l'utilisateur a des tâches en cours
     */
    @Transactional(readOnly = true)
    public boolean utilisateurATachesEnCours(Long utilisateurId) {
        System.out.println("DEBUG: Vérification tâches en cours pour utilisateur " + utilisateurId);

        try {
            List<StatutTache> actifs = List.of(
                    StatutTache.BROUILLON,
                    StatutTache.EN_ATTENTE_VALIDATION
            );

            boolean aTachesActives = tacheRepository.existsByAssigneAIdAndTachesActives(utilisateurId, actifs);
            System.out.println("INFO: Utilisateur " + utilisateurId + " a des tâches actives ? " + aTachesActives);
            return aTachesActives;

        } catch (Exception e) {
            System.out.println("ERROR: Erreur vérification tâches en cours: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtient un utilisateur par son ID.
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> obtenirParId(Long id) {
        return utilisateurRepository.findById(id);
    }

    /**
     * Obtient un utilisateur par son email.
     * Utilisé pour l'authentification (F2).
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> obtenirParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Obtient l'ID d'un utilisateur par son email.
     * Utilisé pour l'authentification et les contrôles d'accès.
     */
    @Transactional(readOnly = true)
    public Long obtenirIdParEmail(String email) {
        System.out.println("DEBUG: Recherche ID utilisateur pour email: " + email);

        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isEmpty()) {
            System.out.println("ERROR: Utilisateur non trouvé pour email: " + email);
            throw new RuntimeException("Utilisateur non trouvé avec l'email: " + email);
        }

        Long userId = utilisateur.get().getId();
        System.out.println("SUCCESS: ID trouvé: " + userId + " pour email: " + email);
        return userId;
    }

    /**
     * Vérifie si un utilisateur peut accéder à un projet.
     *
     * Règles d'accès selon le cahier des charges :
     * 1. ADMINISTRATEUR : accès total à tous les projets
     * 2. Créateur du projet : accès complet à son projet
     * 3. Membre du projet : accès via table projet_utilisateurs
     */
    @Transactional(readOnly = true)
    public boolean peutAccederAuProjet(Long utilisateurId, Long projetId) {
        System.out.println("DEBUG: Vérification accès projet " + projetId + " pour utilisateur " + utilisateurId);

        try {
            Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
            if (utilisateur.isEmpty()) {
                System.out.println("ERROR: Utilisateur " + utilisateurId + " non trouvé");
                return false;
            }

            if (utilisateur.get().getRole() == Role.ADMINISTRATEUR) {
                System.out.println("SUCCESS: Accès ADMINISTRATEUR accordé au projet " + projetId);
                return true;
            }

            Optional<Projet> projet = projetRepository.findById(projetId);
            if (projet.isEmpty()) {
                System.out.println("ERROR: Projet " + projetId + " non trouvé");
                return false;
            }

            if (utilisateurId.equals(projet.get().getIdCreateur())) {
                System.out.println("SUCCESS: Accès CREATEUR accordé au projet " + projetId);
                return true;
            }

            boolean estMembre = projetUtilisateurRepository.existsByProjetIdAndUtilisateurIdAndActif(
                    projetId, utilisateurId, true);

            if (estMembre) {
                System.out.println("SUCCESS: Accès MEMBRE accordé au projet " + projetId);
                return true;
            }

            System.out.println("INFO: Accès refusé au projet " + projetId + " pour utilisateur " + utilisateurId);
            return false;

        } catch (Exception e) {
            System.out.println("ERROR: Erreur vérification accès projet: " + e.getMessage());
            return false;
        }
    }

    /**
     * Alias pour compatibilité WebSocket (F9).
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findByEmail(String email) {
        return obtenirParEmail(email);
    }

    /**
     * Enregistre un utilisateur avec hachage automatique du mot de passe.
     */
    public Utilisateur enregistrer(Utilisateur utilisateur) {
        System.out.println("DEBUG: Début enregistrer() pour utilisateur: " + utilisateur.getEmail());

        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().startsWith("$2a$")) {
            System.out.println("DEBUG: Hachage du mot de passe avec BCrypt...");
            String motDePasseEncode = passwordEncoder.encode(utilisateur.getMotDePasse());
            utilisateur.setMotDePasse(motDePasseEncode);
            System.out.println("DEBUG: Mot de passe haché avec succès");
        }

        System.out.println("DEBUG: Sauvegarde en base de données...");
        Utilisateur resultat = utilisateurRepository.save(utilisateur);
        System.out.println("DEBUG: Utilisateur sauvegardé avec ID: " + resultat.getId() + ", Rôle: " + resultat.getRole());

        return resultat;
    }

    /**
     * Supprime un utilisateur par son ID.
     */
    public void supprimer(Long id) {
        utilisateurRepository.deleteById(id);
    }

    // ========== MÉTHODES DE VÉRIFICATION ==========

    /**
     * Vérifie si un email existe déjà dans le système.
     */
    @Transactional(readOnly = true)
    public boolean existeParEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Vérifie si un utilisateur existe par son ID.
     */
    @Transactional(readOnly = true)
    public boolean existeParId(Long id) {
        return utilisateurRepository.existsById(id);
    }

    // ========== FONCTIONNALITÉS MÉTIER PRINCIPALES ==========

    /**
     * Inscription d'un nouvel utilisateur (F1 - S'inscrire).
     *
     * LOGIQUE MÉTIER CRUCIALE :
     * - Crée un utilisateur avec le rôle VISITEUR par défaut
     * - Un VISITEUR peut consulter les projets publics (F3)
     * - Un VISITEUR peut être découvert et ajouté par un Chef de Projet (F8)
     * - Quand ajouté à un projet, le VISITEUR devient automatiquement MEMBRE
     */
    public Utilisateur inscrire(InscriptionDTO inscriptionDTO) {
        System.out.println("DEBUG: [F1 - S'inscrire] Début inscription - Email: " + inscriptionDTO.getEmail());

        if (inscriptionDTO.getEmail() == null || inscriptionDTO.getEmail().trim().isEmpty()) {
            System.out.println("ERROR: Email obligatoire manquant");
            throw new RuntimeException("L'email est obligatoire pour l'inscription");
        }

        if (inscriptionDTO.getMotDePasse() == null || inscriptionDTO.getMotDePasse().length() < 6) {
            System.out.println("ERROR: Mot de passe trop court");
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }

        if (!inscriptionDTO.isCguAccepte()) {
            System.out.println("ERROR: CGU non acceptées - Obligation RGPD");
            throw new RuntimeException("L'acceptation des CGU est obligatoire (conformité RGPD)");
        }

        final String emailNormalise = inscriptionDTO.getEmail().trim().toLowerCase();

        System.out.println("DEBUG: Vérification unicité de l'email...");
        if (existeParEmail(emailNormalise)) {
            System.out.println("ERROR: Email déjà existant dans le système");
            throw new RuntimeException("Un compte existe déjà avec cette adresse email");
        }

        System.out.println("DEBUG: Toutes les validations passées, création utilisateur VISITEUR...");

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNom(inscriptionDTO.getNom());
        nouvelUtilisateur.setPrenom(inscriptionDTO.getPrenom());
        nouvelUtilisateur.setEmail(emailNormalise);
        nouvelUtilisateur.setMotDePasse(inscriptionDTO.getMotDePasse());
        nouvelUtilisateur.setLangue(inscriptionDTO.getLangue() != null ? inscriptionDTO.getLangue() : "fr");
        nouvelUtilisateur.setCguAccepte(inscriptionDTO.isCguAccepte());
        nouvelUtilisateur.setRole(Role.VISITEUR);

        Utilisateur resultat = enregistrer(nouvelUtilisateur);

        System.out.println("SUCCESS: [F1] Inscription terminée avec succès");
        System.out.println("SUCCESS: Nouvel utilisateur - ID: " + resultat.getId() + ", Rôle: " + resultat.getRole());

        return resultat;
    }

    /**
     * Promotion d'un VISITEUR vers MEMBRE (F8).
     */
    public Utilisateur promouvoirVersMembreParChefProjet(Long utilisateurId) {
        System.out.println("DEBUG: [F8] Promotion VISITEUR → MEMBRE pour utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour promotion"));

        if (utilisateur.getRole() == Role.VISITEUR) {
            utilisateur.setRole(Role.MEMBRE);
            Utilisateur resultat = utilisateurRepository.save(utilisateur);

            System.out.println("SUCCESS: [F8] Utilisateur " + utilisateur.getEmail() + " promu vers MEMBRE");
            return resultat;
        } else {
            System.out.println("INFO: [F8] Utilisateur " + utilisateur.getEmail() + " déjà " + utilisateur.getRole());
            return utilisateur;
        }
    }

    /**
     * Promotion MEMBRE → CHEF_PROJET (F10 - Abonnement).
     */
    public Utilisateur promouvoirVersChefProjetAvecAbonnement(Long utilisateurId) {
        System.out.println("DEBUG: [F10] Promotion MEMBRE → CHEF_PROJET pour utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour promotion vers Chef de Projet"));

        if (utilisateur.getRole() == Role.MEMBRE) {
            utilisateur.setRole(Role.CHEF_PROJET);
            Utilisateur resultat = utilisateurRepository.save(utilisateur);

            System.out.println("SUCCESS: [F10] Utilisateur " + utilisateur.getEmail() + " promu vers CHEF_PROJET");
            return resultat;
        } else {
            System.out.println("ERROR: [F10] Seuls les MEMBRES peuvent devenir CHEF_PROJET");
            throw new RuntimeException("Seuls les utilisateurs MEMBRE peuvent souscrire un abonnement et devenir Chef de Projet");
        }
    }

    /**
     * Rétrogradation CHEF_PROJET → MEMBRE (Fin d'abonnement).
     */
    public Utilisateur retrograderChefProjetVersMembreFinAbonnement(Long utilisateurId) {
        System.out.println("DEBUG: [F10] Rétrogradation CHEF_PROJET → MEMBRE (fin abonnement) pour utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour rétrogradation"));

        if (utilisateur.getRole() == Role.CHEF_PROJET) {
            utilisateur.setRole(Role.MEMBRE);
            Utilisateur resultat = utilisateurRepository.save(utilisateur);

            System.out.println("INFO: [F10] Utilisateur " + utilisateur.getEmail() + " rétrogradé vers MEMBRE");
            return resultat;
        }

        return utilisateur;
    }

    // ========== FONCTIONNALITÉS PROFIL ==========

    public UtilisateurDTO inscrireDTO(InscriptionDTO inscriptionDTO) {
        Utilisateur utilisateur = inscrire(inscriptionDTO);
        return convertirEnDTO(utilisateur);
    }

    @Transactional(readOnly = true)
    public UtilisateurDTO consulterProfil(Long utilisateurId) {
        System.out.println("DEBUG: [F4] Consultation profil utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour consultation profil"));

        return convertirEnDTO(utilisateur);
    }

    public UtilisateurDTO mettreAJourProfil(Long utilisateurId, UtilisateurDTO utilisateurDTO) {
        System.out.println("DEBUG: [F5] Mise à jour profil utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour mise à jour profil"));

        utilisateur.setNom(utilisateurDTO.getNom());
        utilisateur.setPrenom(utilisateurDTO.getPrenom());
        utilisateur.setLangue(utilisateurDTO.getLangue());

        Utilisateur utilisateurSauve = utilisateurRepository.save(utilisateur);

        System.out.println("SUCCESS: [F5] Profil mis à jour pour " + utilisateur.getEmail());

        return convertirEnDTO(utilisateurSauve);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private UtilisateurDTO convertirEnDTO(Utilisateur utilisateur) {
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

    public Utilisateur convertirEnUtilisateur(UtilisateurDTO dto) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(dto.getId());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setRole(dto.getRole());
        utilisateur.setLangue(dto.getLangue());
        utilisateur.setDateInscription(dto.getDateInscription());
        return utilisateur;
    }

    // ========== MÉTHODES D'ANALYSE MÉTIER ==========

    @Transactional(readOnly = true)
    public long compterParRole(Role role) {
        return utilisateurRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .count();
    }

    public boolean peutEffectuerAction(Long utilisateurId, String action) {
        Optional<Utilisateur> utilisateur = obtenirParId(utilisateurId);
        if (utilisateur.isEmpty()) return false;

        Role role = utilisateur.get().getRole();

        return switch (action) {
            case "CONSULTER_PROJETS_PUBLICS" -> role == Role.VISITEUR || role == Role.MEMBRE ||
                    role == Role.CHEF_PROJET || role == Role.ADMINISTRATEUR;
            case "COLLABORER" -> role == Role.MEMBRE || role == Role.CHEF_PROJET || role == Role.ADMINISTRATEUR;
            case "CREER_PROJETS" -> role == Role.CHEF_PROJET || role == Role.ADMINISTRATEUR;
            case "GERER_UTILISATEURS" -> role == Role.ADMINISTRATEUR;
            default -> false;
        };
    }
}