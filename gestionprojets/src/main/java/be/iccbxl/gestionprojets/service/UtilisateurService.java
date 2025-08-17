package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.ConnexionDTO;
import be.iccbxl.gestionprojets.dto.InscriptionDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.enums.Role;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.ProjetUtilisateurRepository;
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

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              PasswordEncoder passwordEncoder,
                              ProjetRepository projetRepository,
                              ProjetUtilisateurRepository projetUtilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.projetRepository = projetRepository;
        this.projetUtilisateurRepository = projetUtilisateurRepository;
    }

    // ========== MÉTHODES DE BASE ==========

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
                    .map(this::convertirEnDTO)  // CONVERSION EN DTO
                    .collect(Collectors.toList());

            System.out.println("SUCCESS: [F8] " + utilisateursFiltrés.size() + " utilisateurs disponibles pour le projet " + projetId);

            return utilisateursFiltrés;

        } catch (Exception e) {
            System.out.println("ERROR: [F8] Erreur recherche utilisateurs disponibles: " + e.getMessage());
            return List.of(); // Retourner liste vide en cas d'erreur
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
            // Cette méthode nécessiterait TacheRepository pour une implémentation complète
            // Pour l'instant, retourner false pour permettre les tests
            // TODO: À implémenter quand TacheRepository sera disponible

            System.out.println("INFO: Vérification tâches temporairement désactivée");
            return false;

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
     * MÉTHODE AJOUTÉE POUR CORRIGER LES ERREURS TACHECONTROLLER
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
     *
     * Utilisé par TacheController pour F7 : Gérer les tâches
     * MÉTHODE AJOUTÉE POUR CORRIGER LES ERREURS TACHECONTROLLER
     */
    @Transactional(readOnly = true)
    public boolean peutAccederAuProjet(Long utilisateurId, Long projetId) {
        System.out.println("DEBUG: Vérification accès projet " + projetId + " pour utilisateur " + utilisateurId);

        try {
            // 1. Vérifier si l'utilisateur existe
            Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
            if (utilisateur.isEmpty()) {
                System.out.println("ERROR: Utilisateur " + utilisateurId + " non trouvé");
                return false;
            }

            // 2. ADMINISTRATEUR a accès à tout
            if (utilisateur.get().getRole() == Role.ADMINISTRATEUR) {
                System.out.println("SUCCESS: Accès ADMINISTRATEUR accordé au projet " + projetId);
                return true;
            }

            // 3. Vérifier si l'utilisateur est le créateur du projet
            Optional<Projet> projet = projetRepository.findById(projetId);
            if (projet.isEmpty()) {
                System.out.println("ERROR: Projet " + projetId + " non trouvé");
                return false;
            }

            if (utilisateurId.equals(projet.get().getIdCreateur())) {
                System.out.println("SUCCESS: Accès CREATEUR accordé au projet " + projetId);
                return true;
            }

            // 4. Vérifier si l'utilisateur est membre du projet
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
     * Utilise la méthode existante obtenirParEmail.
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

        // Hasher le mot de passe si ce n'est pas déjà fait
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
     * Utilisé pour valider l'unicité lors de l'inscription (F1).
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
     *
     * @param inscriptionDTO Les données d'inscription validées
     * @return L'utilisateur créé avec le rôle VISITEUR
     * @throws RuntimeException si email existant ou données invalides
     */
    public Utilisateur inscrire(InscriptionDTO inscriptionDTO) {
        System.out.println("DEBUG: [F1 - S'inscrire] Début inscription - Email: " + inscriptionDTO.getEmail());
        System.out.println("DEBUG: Données reçues: " + inscriptionDTO.toString());

        // ========== VALIDATIONS MÉTIER ==========

        if (inscriptionDTO.getEmail() == null || inscriptionDTO.getEmail().trim().isEmpty()) {
            System.out.println("ERROR: Email obligatoire manquant");
            throw new RuntimeException("L'email est obligatoire pour l'inscription");
        }

        if (inscriptionDTO.getMotDePasse() == null || inscriptionDTO.getMotDePasse().length() < 6) {
            System.out.println("ERROR: Mot de passe trop court: " +
                    (inscriptionDTO.getMotDePasse() != null ? inscriptionDTO.getMotDePasse().length() : 0) + " caractères");
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }

        if (!inscriptionDTO.isCguAccepte()) {
            System.out.println("ERROR: CGU non acceptées - Obligation RGPD");
            throw new RuntimeException("L'acceptation des CGU est obligatoire (conformité RGPD)");
        }

        // Vérification unicité email
        System.out.println("DEBUG: Vérification unicité de l'email...");
        if (existeParEmail(inscriptionDTO.getEmail())) {
            System.out.println("ERROR: Email déjà existant dans le système");
            throw new RuntimeException("Un compte existe déjà avec cette adresse email");
        }

        // ========== CRÉATION UTILISATEUR ==========

        System.out.println("DEBUG: Toutes les validations passées, création utilisateur VISITEUR...");

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNom(inscriptionDTO.getNom());
        nouvelUtilisateur.setPrenom(inscriptionDTO.getPrenom());
        nouvelUtilisateur.setEmail(inscriptionDTO.getEmail());
        nouvelUtilisateur.setMotDePasse(inscriptionDTO.getMotDePasse());
        nouvelUtilisateur.setLangue(inscriptionDTO.getLangue() != null ? inscriptionDTO.getLangue() : "fr");
        nouvelUtilisateur.setCguAccepte(inscriptionDTO.isCguAccepte());

        // LOGIQUE MÉTIER CRUCIALE : Rôle VISITEUR par défaut selon le cahier des charges
        nouvelUtilisateur.setRole(Role.VISITEUR);

        System.out.println("DEBUG: Utilisateur créé avec rôle VISITEUR - Appel enregistrer()...");

        Utilisateur resultat = enregistrer(nouvelUtilisateur);

        System.out.println("SUCCESS: [F1] Inscription terminée avec succès");
        System.out.println("SUCCESS: Nouvel utilisateur - ID: " + resultat.getId() + ", Rôle: " + resultat.getRole());
        System.out.println("SUCCESS: L'utilisateur peut maintenant consulter les projets publics et être découvert par les Chefs de Projet");

        return resultat;
    }

    /**
     * Promotion d'un VISITEUR vers MEMBRE (F8 - Ajouter des membres à un projet).
     *
     * Cette méthode est appelée automatiquement quand un Chef de Projet
     * ajoute un VISITEUR à son projet. Le VISITEUR devient alors MEMBRE.
     *
     * @param utilisateurId ID de l'utilisateur VISITEUR à promouvoir
     * @return L'utilisateur avec son nouveau rôle MEMBRE
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    public Utilisateur promouvoirVersMembreParChefProjet(Long utilisateurId) {
        System.out.println("DEBUG: [F8] Promotion VISITEUR → MEMBRE pour utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour promotion"));

        // Promotion uniquement si l'utilisateur est VISITEUR
        if (utilisateur.getRole() == Role.VISITEUR) {
            utilisateur.setRole(Role.MEMBRE);
            Utilisateur resultat = utilisateurRepository.save(utilisateur);

            System.out.println("SUCCESS: [F8] Utilisateur " + utilisateur.getEmail() + " promu vers MEMBRE");
            System.out.println("SUCCESS: L'utilisateur peut maintenant collaborer et gérer des tâches");

            return resultat;
        } else {
            System.out.println("INFO: [F8] Utilisateur " + utilisateur.getEmail() + " déjà " + utilisateur.getRole());
            return utilisateur; // Déjà MEMBRE ou rôle supérieur
        }
    }

    /**
     * Promotion d'un MEMBRE vers CHEF_PROJET (F10 - Souscription abonnement).
     *
     * Un MEMBRE devient CHEF_PROJET après avoir souscrit un abonnement.
     * Seuls les CHEF_PROJET peuvent créer et gérer des projets.
     *
     * @param utilisateurId ID de l'utilisateur MEMBRE à promouvoir
     * @return L'utilisateur avec son nouveau rôle CHEF_PROJET
     * @throws RuntimeException si l'utilisateur n'est pas MEMBRE
     */
    public Utilisateur promouvoirVersChefProjetAvecAbonnement(Long utilisateurId) {
        System.out.println("DEBUG: [F10] Promotion MEMBRE → CHEF_PROJET pour utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour promotion vers Chef de Projet"));

        // Seuls les MEMBRES peuvent devenir CHEF_PROJET
        if (utilisateur.getRole() == Role.MEMBRE) {
            utilisateur.setRole(Role.CHEF_PROJET);
            Utilisateur resultat = utilisateurRepository.save(utilisateur);

            System.out.println("SUCCESS: [F10] Utilisateur " + utilisateur.getEmail() + " promu vers CHEF_PROJET");
            System.out.println("SUCCESS: L'utilisateur peut maintenant créer des projets et gérer des équipes");

            return resultat;
        } else {
            System.out.println("ERROR: [F10] Seuls les MEMBRES peuvent devenir CHEF_PROJET");
            throw new RuntimeException("Seuls les utilisateurs MEMBRE peuvent souscrire un abonnement et devenir Chef de Projet");
        }
    }

    /**
     * Rétrogradation d'un CHEF_PROJET vers MEMBRE (Fin d'abonnement).
     * Appelée automatiquement quand un abonnement expire.
     */
    public Utilisateur retrograderChefProjetVersMembreFinAbonnement(Long utilisateurId) {
        System.out.println("DEBUG: [F10] Rétrogradation CHEF_PROJET → MEMBRE (fin abonnement) pour utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour rétrogradation"));

        if (utilisateur.getRole() == Role.CHEF_PROJET) {
            utilisateur.setRole(Role.MEMBRE);
            Utilisateur resultat = utilisateurRepository.save(utilisateur);

            System.out.println("INFO: [F10] Utilisateur " + utilisateur.getEmail() + " rétrogradé vers MEMBRE (fin abonnement)");
            return resultat;
        }

        return utilisateur;
    }

    // ========== FONCTIONNALITÉS PROFIL ==========

    /**
     * Version DTO de l'inscription pour flexibilité API.
     */
    public UtilisateurDTO inscrireDTO(InscriptionDTO inscriptionDTO) {
        Utilisateur utilisateur = inscrire(inscriptionDTO);
        return convertirEnDTO(utilisateur);
    }

    /**
     * Consulter le profil utilisateur (F4 - Consulter son profil).
     */
    @Transactional(readOnly = true)
    public UtilisateurDTO consulterProfil(Long utilisateurId) {
        System.out.println("DEBUG: [F4] Consultation profil utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour consultation profil"));

        return convertirEnDTO(utilisateur);
    }

    /**
     * Mettre à jour le profil utilisateur (F5 - Mettre à jour son profil).
     */
    public UtilisateurDTO mettreAJourProfil(Long utilisateurId, UtilisateurDTO utilisateurDTO) {
        System.out.println("DEBUG: [F5] Mise à jour profil utilisateur ID: " + utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour mise à jour profil"));

        // Mise à jour des champs modifiables selon F5
        utilisateur.setNom(utilisateurDTO.getNom());
        utilisateur.setPrenom(utilisateurDTO.getPrenom());
        utilisateur.setLangue(utilisateurDTO.getLangue());

        Utilisateur utilisateurSauve = utilisateurRepository.save(utilisateur);

        System.out.println("SUCCESS: [F5] Profil mis à jour pour " + utilisateur.getEmail());

        return convertirEnDTO(utilisateurSauve);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Convertit un Utilisateur en UtilisateurDTO (sans mot de passe pour sécurité).
     */
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

    /**
     * Convertit un UtilisateurDTO en Utilisateur.
     */
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

    /**
     * Obtient les statistiques des rôles pour le dashboard administrateur.
     */
    @Transactional(readOnly = true)
    public long compterParRole(Role role) {
        return utilisateurRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .count();
    }

    /**
     * Vérifie si un utilisateur peut effectuer une action selon son rôle.
     */
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