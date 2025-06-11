package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.ConnexionDTO;
import be.iccbxl.gestionprojets.dto.InscriptionDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Vos méthodes existantes (gardées)
    public List<Utilisateur> obtenirTous() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> obtenirParId(Long id) {
        return utilisateurRepository.findById(id);
    }

    public Optional<Utilisateur> obtenirParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public Utilisateur enregistrer(Utilisateur utilisateur) {
        // Hasher le mot de passe
        String motDePasseEncode = passwordEncoder.encode(utilisateur.getMotDePasse());
        utilisateur.setMotDePasse(motDePasseEncode);
        return utilisateurRepository.save(utilisateur);
    }

    public void supprimer(Long id) {
        utilisateurRepository.deleteById(id);
    }

    // Nouvelles méthodes avec DTOs pour les exigences

    /**
     * Inscription d'un nouvel utilisateur
     */
    public UtilisateurDTO inscrire(InscriptionDTO inscriptionDTO) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(inscriptionDTO.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Créer nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(inscriptionDTO.getNom());
        utilisateur.setPrenom(inscriptionDTO.getPrenom());
        utilisateur.setEmail(inscriptionDTO.getEmail());
        utilisateur.setMotDePasse(inscriptionDTO.getMotDePasse());
        utilisateur.setLangue(inscriptionDTO.getLangue());
        utilisateur.setCguAccepte(inscriptionDTO.isCguAccepte());

        // Enregistrer avec hashage du mot de passe
        Utilisateur utilisateurSauve = enregistrer(utilisateur);

        // Convertir en DTO pour la réponse
        return convertirEnDTO(utilisateurSauve);
    }

    /**
     * Consulter le profil utilisateur
     */
    public UtilisateurDTO consulterProfil(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return convertirEnDTO(utilisateur);
    }

    /**
     * Mettre à jour le profil utilisateur
     */
    public UtilisateurDTO mettreAJourProfil(Long utilisateurId, UtilisateurDTO utilisateurDTO) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mettre à jour les champs modifiables
        utilisateur.setNom(utilisateurDTO.getNom());
        utilisateur.setPrenom(utilisateurDTO.getPrenom());
        utilisateur.setLangue(utilisateurDTO.getLangue());

        Utilisateur utilisateurSauve = utilisateurRepository.save(utilisateur);
        return convertirEnDTO(utilisateurSauve);
    }

    /**
     *  UtilisateurDTO
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
}