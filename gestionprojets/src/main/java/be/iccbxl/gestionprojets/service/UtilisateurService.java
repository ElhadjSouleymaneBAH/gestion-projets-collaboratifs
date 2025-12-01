package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.InscriptionDTO;
import be.iccbxl.gestionprojets.dto.MiseAJourProfilDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.enums.Role;
import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.ProjetUtilisateurRepository;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    // --------- Utilitaires persistance ----------
    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirTous() { return utilisateurRepository.findAll(); }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> obtenirParId(Long id) { return utilisateurRepository.findById(id); }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> obtenirParEmail(String email) { return utilisateurRepository.findByEmail(email); }

    /**
     * Ajout de compatibilité avec les anciens appels "findByEmail"
     * (utilisé dans MessageController, etc.)
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     *  Récupère l'identifiant d'un utilisateur à partir de son email.
     */
    @Transactional(readOnly = true)
    public Long obtenirIdParEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email invalide ou vide");
        }
        String emailNormalise = email.trim().toLowerCase(Locale.ROOT);
        return utilisateurRepository.findByEmail(emailNormalise)
                .map(Utilisateur::getId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + emailNormalise));
    }

    public Utilisateur enregistrer(Utilisateur utilisateur) {
        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().startsWith("$2a$")) {
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        }
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * SUPPRESSION CONFORME RGPD (Soft Delete + Anonymisation)
     *
     * Cette méthode anonymise les données personnelles de l'utilisateur
     * conformément au RGPD Article 17 (Droit à l'oubli) tout en conservant
     * les données financières pour conformité comptable et traçabilité.
     *
     * Les transactions, factures et abonnements restent dans la base de données
     * avec un lien vers un utilisateur anonymisé, permettant ainsi :
     * - La conformité avec les obligations comptables (conservation 10 ans)
     * - L'audit et la traçabilité financière
     * - Le respect du RGPD (données personnelles effacées)
     *
     * @param id L'identifiant de l'utilisateur à supprimer
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    @Transactional
    public void supprimer(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        //  ANONYMISATION des données personnelles (RGPD Article 17)
        utilisateur.setNom("SUPPRIME");
        utilisateur.setPrenom("SUPPRIME");
        utilisateur.setEmail("supprime_" + id + "@anonyme.local");
        utilisateur.setMotDePasse("ANONYMISE_" + System.currentTimeMillis());
        utilisateur.setAdresse(null);
        utilisateur.setRole(Role.VISITEUR); // Rôle neutre sans privilèges
        utilisateur.setCguAccepte(false);

        // Sauvegarde de l'utilisateur anonymisé
        utilisateurRepository.save(utilisateur);


        System.out.println("[RGPD] Utilisateur ID " + id + " anonymisé le "
                + java.time.LocalDateTime.now()
                + " conformément à l'Article 17 du RGPD");

        //  Les relations (transactions, factures) sont conservées
        // pour respecter les obligations légales de conservation comptable (10 ans)
        // L'abonnement est automatiquement supprimé par la contrainte CASCADE
    }

    @Transactional(readOnly = true)
    public boolean existeParEmail(String email) { return utilisateurRepository.existsByEmail(email); }

    @Transactional(readOnly = true)
    public boolean existeParId(Long id) { return utilisateurRepository.existsById(id); }

    // --------- F1 : Inscription ----------
    public Utilisateur inscrire(InscriptionDTO inscriptionDTO) {
        if (inscriptionDTO.getEmail() == null || inscriptionDTO.getEmail().trim().isEmpty())
            throw new RuntimeException("L'email est obligatoire pour l'inscription");
        if (inscriptionDTO.getMotDePasse() == null || inscriptionDTO.getMotDePasse().length() < 6)
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        if (!inscriptionDTO.isCguAccepte())
            throw new RuntimeException("L'acceptation des CGU est obligatoire (conformité RGPD)");

        final String emailNormalise = inscriptionDTO.getEmail().trim().toLowerCase(Locale.ROOT);
        if (existeParEmail(emailNormalise))
            throw new RuntimeException("Un compte existe déjà avec cette adresse email");

        Utilisateur u = new Utilisateur();
        u.setNom(inscriptionDTO.getNom());
        u.setPrenom(inscriptionDTO.getPrenom());
        u.setEmail(emailNormalise);
        u.setMotDePasse(inscriptionDTO.getMotDePasse());
        u.setLangue(inscriptionDTO.getLangue() != null ? inscriptionDTO.getLangue() : "fr");
        u.setCguAccepte(inscriptionDTO.isCguAccepte());
        u.setRole(Role.MEMBRE);
        u.setAdresse(inscriptionDTO.getAdresse());

        return enregistrer(u);
    }

    // --------- F8/F10 raccourcis rôle ----------
    public Utilisateur promouvoirVersMembreParChefProjet(Long utilisateurId) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour promotion"));
        if (u.getRole() == Role.VISITEUR) u.setRole(Role.MEMBRE);
        return utilisateurRepository.save(u);
    }

    public Utilisateur promouvoirVersChefProjetAvecAbonnement(Long utilisateurId) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour promotion vers Chef de Projet"));
        if (u.getRole() != Role.MEMBRE)
            throw new RuntimeException("Seuls les utilisateurs MEMBRE peuvent devenir Chef de Projet");
        u.setRole(Role.CHEF_PROJET);
        return utilisateurRepository.save(u);
    }

    public Utilisateur retrograderChefProjetVersMembreFinAbonnement(Long utilisateurId) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour rétrogradation"));
        if (u.getRole() == Role.CHEF_PROJET) u.setRole(Role.MEMBRE);
        return utilisateurRepository.save(u);
    }

    // --------- F4 ----------
    @Transactional(readOnly = true)
    public UtilisateurDTO consulterProfil(Long utilisateurId) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour consultation profil"));
        return convertirEnDTO(u);
    }

    // --------- F5 : Mise à jour du profil ----------
    public Utilisateur mettreAJourProfil(Long utilisateurId, MiseAJourProfilDTO dto) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour la mise à jour du profil"));

        if (dto.getNom() != null) u.setNom(dto.getNom());
        if (dto.getPrenom() != null) u.setPrenom(dto.getPrenom());
        if (dto.getAdresse() != null) u.setAdresse(dto.getAdresse());
        if (dto.getLangue() != null) u.setLangue(dto.getLangue());

        return utilisateurRepository.save(u);
    }

    // --------- Outils DTO ----------
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

    // --------- Divers métier utiles ----------
    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirTousLesUtilisateursSansProjet() {
        return utilisateurRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.MEMBRE)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean utilisateurATachesEnCours(Long utilisateurId) {
        List<StatutTache> actifs = List.of(StatutTache.BROUILLON, StatutTache.EN_ATTENTE_VALIDATION);
        return tacheRepository.existsByAssigneAIdAndTachesActives(utilisateurId, actifs);
    }

    @Transactional(readOnly = true)
    public boolean peutAccederAuProjet(Long utilisateurId, Long projetId) {
        Optional<Utilisateur> u = utilisateurRepository.findById(utilisateurId);
        if (u.isEmpty()) return false;
        if (u.get().getRole() == Role.ADMINISTRATEUR) return true;

        Optional<Projet> p = projetRepository.findById(projetId);
        if (p.isEmpty()) return false;

        if (utilisateurId.equals(p.get().getIdCreateur())) return true;

        return projetUtilisateurRepository.existsByProjetIdAndUtilisateurIdAndActif(projetId, utilisateurId, true);
    }

    // --------- NOUVELLE MÉTHODE----------
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> obtenirUtilisateursDisponiblesPourProjet(Long projetId) {
        List<Utilisateur> dispo = utilisateurRepository.findUtilisateursSansProjet();
        return dispo.stream().map(this::convertirEnDTO).toList();
    }
}