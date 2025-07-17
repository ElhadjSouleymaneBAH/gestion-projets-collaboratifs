package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.ConnexionDTO;
import be.iccbxl.gestionprojets.dto.InscriptionDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.enums.Role;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs.
 *
 * Implémente les fonctionnalités :
 * - F1 : S'inscrire
 * - F4 : Consulter son profil
 * - F5 : Mettre à jour son profil
 * - F9 : Support collaboration temps réel
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
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
     * Obtient un utilisateur par son ID.
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> obtenirParId(Long id) {
        return utilisateurRepository.findById(id);
    }

    /**
     * Obtient un utilisateur par son email.
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> obtenirParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Alias pour compatibilité WebSocket (F9)
     * Utilise la méthode existante obtenirParEmail
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findByEmail(String email) {
        return obtenirParEmail(email);
    }

    /**
     * Enregistre un utilisateur avec hachage du mot de passe.
     */
    public Utilisateur enregistrer(Utilisateur utilisateur) {
        // Hasher le mot de passe si ce n'est pas déjà fait
        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().startsWith("$2a$")) {
            String motDePasseEncode = passwordEncoder.encode(utilisateur.getMotDePasse());
            utilisateur.setMotDePasse(motDePasseEncode);
        }
        return utilisateurRepository.save(utilisateur);
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
     *
     * @param email L'adresse email à vérifier
     * @return true si l'email existe déjà, false sinon
     */
    @Transactional(readOnly = true)
    public boolean existeParEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Vérifie si un utilisateur existe par son ID.
     *
     * @param id L'identifiant de l'utilisateur
     * @return true si l'utilisateur existe, false sinon
     */
    @Transactional(readOnly = true)
    public boolean existeParId(Long id) {
        return utilisateurRepository.existsById(id);
    }

    // ========== FONCTIONNALITÉS MÉTIER ==========

    /**
     * Inscription d'un nouvel utilisateur (F1).
     *
     * @param inscriptionDTO Les données d'inscription
     * @return L'utilisateur créé (pour compatibilité avec le Controller)
     * @throws RuntimeException si l'email existe déjà ou données invalides
     */
    public Utilisateur inscrire(InscriptionDTO inscriptionDTO) {
        // Validations
        if (inscriptionDTO.getEmail() == null || inscriptionDTO.getEmail().trim().isEmpty()) {
            throw new RuntimeException("L'email est obligatoire");
        }

        if (inscriptionDTO.getMotDePasse() == null || inscriptionDTO.getMotDePasse().length() < 8) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 8 caractères");
        }

        if (!inscriptionDTO.isCguAccepte()) {
            throw new RuntimeException("L'acceptation des CGU est obligatoire (RGPD)");
        }

        // Vérifier si l'email existe déjà
        if (existeParEmail(inscriptionDTO.getEmail())) {
            throw new RuntimeException("Un compte existe déjà avec cette adresse email");
        }

        // Créer nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(inscriptionDTO.getNom());
        utilisateur.setPrenom(inscriptionDTO.getPrenom());
        utilisateur.setEmail(inscriptionDTO.getEmail());
        utilisateur.setMotDePasse(inscriptionDTO.getMotDePasse());
        utilisateur.setLangue(inscriptionDTO.getLangue() != null ? inscriptionDTO.getLangue() : "fr");
        utilisateur.setCguAccepte(inscriptionDTO.isCguAccepte());
        utilisateur.setRole(Role.MEMBRE); // Rôle par défaut pour F1

        // Enregistrer avec hashage du mot de passe
        return enregistrer(utilisateur);
    }

    /**
     * Version DTO de l'inscription (pour flexibilité).
     */
    public UtilisateurDTO inscrireDTO(InscriptionDTO inscriptionDTO) {
        Utilisateur utilisateur = inscrire(inscriptionDTO);
        return convertirEnDTO(utilisateur);
    }

    /**
     * Consulter le profil utilisateur (F4).
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return Le profil utilisateur en DTO
     */
    @Transactional(readOnly = true)
    public UtilisateurDTO consulterProfil(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return convertirEnDTO(utilisateur);
    }

    /**
     * Mettre à jour le profil utilisateur (F5).
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @param utilisateurDTO Les nouvelles données
     * @return Le profil mis à jour
     */
    public UtilisateurDTO mettreAJourProfil(Long utilisateurId, UtilisateurDTO utilisateurDTO) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mettre à jour les champs modifiables (F5)
        utilisateur.setNom(utilisateurDTO.getNom());
        utilisateur.setPrenom(utilisateurDTO.getPrenom());
        utilisateur.setLangue(utilisateurDTO.getLangue());

        Utilisateur utilisateurSauve = utilisateurRepository.save(utilisateur);
        return convertirEnDTO(utilisateurSauve);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Convertit un Utilisateur en UtilisateurDTO (sans mot de passe).
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
}