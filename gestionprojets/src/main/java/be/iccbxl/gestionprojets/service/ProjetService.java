package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.ProjetDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.enums.StatutProjet;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.ProjetUtilisateur;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.ProjetUtilisateurRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import be.iccbxl.gestionprojets.service.ListeColonneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProjetUtilisateurRepository projetUtilisateurRepository;
    private final ListeColonneService listeColonneService;

    public ProjetService(ProjetRepository projetRepository,
                         UtilisateurRepository utilisateurRepository,
                         ProjetUtilisateurRepository projetUtilisateurRepository,
                         ListeColonneService listeColonneService) {
        this.projetRepository = projetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.projetUtilisateurRepository = projetUtilisateurRepository;
        this.listeColonneService = listeColonneService;
    }

    // =====================================================================
    // F3 : CONSULTER PROJETS PUBLICS
    // =====================================================================
    @Transactional(readOnly = true)
    public List<ProjetDTO> obtenirProjetsPublics() {
        return projetRepository.findByPubliqueTrueWithCreateur()
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    // =====================================================================
    // F6 : CRÉER / LIRE / MODIFIER / SUPPRIMER
    // =====================================================================
    public ProjetDTO creerProjet(ProjetDTO projetDTO) {
        if (projetDTO.getIdCreateur() == null) {
            throw new RuntimeException("Le créateur (idCreateur) est obligatoire");
        }
        Utilisateur createur = utilisateurRepository.findById(projetDTO.getIdCreateur())
                .orElseThrow(() -> new RuntimeException("Créateur introuvable: " + projetDTO.getIdCreateur()));

        if (projetDTO.getTitre() == null || projetDTO.getTitre().isBlank()) {
            throw new RuntimeException("Le titre est obligatoire");
        }
        if (projetDTO.getDescription() == null || projetDTO.getDescription().isBlank()) {
            throw new RuntimeException("La description est obligatoire");
        }

        Projet projet = new Projet();
        projet.setTitre(projetDTO.getTitre());
        projet.setDescription(projetDTO.getDescription());
        projet.setStatut(
                projetDTO.getStatut() != null
                        ? StatutProjet.valueOf(projetDTO.getStatut().toUpperCase())
                        : StatutProjet.ACTIF
        );
        projet.setCreateur(createur);
        projet.setPublique(projetDTO.getPublique() != null ? projetDTO.getPublique() : Boolean.FALSE);

        if (projet.getDateCreation() == null) {
            projet.setDateCreation(LocalDateTime.now());
        }

        Projet saved = projetRepository.save(projet);

        // ========== NOUVEAU: Créer les colonnes Kanban par défaut ==========
        try {
            System.out.println("[ProjetService] Création colonnes Kanban pour projet " + saved.getId());
            listeColonneService.creerColonnesParDefaut(saved.getId());
            System.out.println("[ProjetService] Colonnes Kanban créées avec succès");
        } catch (Exception e) {
            System.err.println("[ProjetService] Erreur création colonnes Kanban: " + e.getMessage());
        }
        // ===================================================================

        ProjetUtilisateur lien = new ProjetUtilisateur();
        lien.setProjetId(saved.getId());
        lien.setUtilisateurId(createur.getId());
        lien.setRole("CHEF_PROJET");
        lien.setActif(true);
        projetUtilisateurRepository.save(lien);

        return convertirEnDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjetDTO> obtenirTousProjets() {
        return projetRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProjetDTO> obtenirProjetParId(Long id) {
        return projetRepository.findByIdWithCreateur(id).map(this::convertirEnDTO);
    }

    @Transactional(readOnly = true)
    public Optional<Projet> findById(Long id) {
        return projetRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ProjetDTO> obtenirProjetsParCreateur(Long idCreateur) {
        return projetRepository.findByCreateurIdWithCreateur(idCreateur)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjetDTO> obtenirProjetsParUtilisateur(Long utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé avec ID: " + utilisateurId);
        }

        return projetRepository.findByCreateurIdWithCreateur(utilisateurId)
                .stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    public ProjetDTO modifierProjet(Long id, ProjetDTO dto) {
        Projet p = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        p.setTitre(dto.getTitre());
        p.setDescription(dto.getDescription());
        if (dto.getStatut() != null) {
            p.setStatut(StatutProjet.valueOf(dto.getStatut().toUpperCase()));
        }
        if (dto.getPublique() != null) {
            p.setPublique(dto.getPublique());
        }

        Projet saved = projetRepository.save(p);
        return convertirEnDTO(saved);
    }

    // =====================================================================
    // MÉTHODE CORRIGÉE : supprimerProjet
    // =====================================================================
    public void supprimerProjet(Long id) {
        // Vérifier si le projet existe
        Optional<Projet> projetOpt = projetRepository.findById(id);
        if (projetOpt.isEmpty()) {
            System.err.println("❌ Tentative de suppression d'un projet inexistant: ID=" + id);
            throw new RuntimeException("Projet non trouvé avec ID: " + id);
        }

        Projet projet = projetOpt.get();
        System.out.println("🗑️ Suppression du projet: " + projet.getTitre() + " (ID=" + id + ")");

        try {
            // 1. Compter les membres avant suppression (optionnel, pour les logs)
            long nbMembres = projetUtilisateurRepository.countByProjetIdAndActif(id, true);
            System.out.println("   - Projet avec " + nbMembres + " membre(s)");

            // 2. Supprimer les relations dans projet_utilisateur
            projetUtilisateurRepository.deleteByProjetId(id);
            System.out.println("   - Relations membres supprimées");

            // 3. Note: Si vous avez d'autres tables liées (taches, messages, commentaires),
            //    assurez-vous qu'elles ont CASCADE DELETE ou supprimez-les manuellement ici
            //    Exemple:
            //    tacheRepository.deleteByProjetId(id);
            //    messageRepository.deleteByProjetId(id);

            // 4. Supprimer le projet
            projetRepository.deleteById(id);

            System.out.println("✅ Projet supprimé avec succès: ID=" + id);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression du projet ID=" + id);
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression du projet: " + e.getMessage(), e);
        }
    }

    public ProjetDTO creerProjet(ProjetDTO dto, String emailCreateur) {
        Utilisateur createur = utilisateurRepository.findByEmail(emailCreateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur créateur non trouvé"));

        if (dto.getTitre() == null || dto.getTitre().isBlank()) {
            throw new RuntimeException("Le titre est obligatoire");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new RuntimeException("La description est obligatoire");
        }

        Projet p = new Projet();
        p.setTitre(dto.getTitre());
        p.setDescription(dto.getDescription());
        p.setStatut(
                dto.getStatut() != null
                        ? StatutProjet.valueOf(dto.getStatut().toUpperCase())
                        : StatutProjet.ACTIF
        );
        p.setCreateur(createur);
        p.setPublique(dto.getPublique() != null ? dto.getPublique() : Boolean.FALSE);

        if (p.getDateCreation() == null) {
            p.setDateCreation(LocalDateTime.now());
        }

        Projet saved = projetRepository.save(p);

        // ========== NOUVEAU: Créer les colonnes Kanban par défaut ==========
        try {
            System.out.println("[ProjetService] Création colonnes Kanban pour projet " + saved.getId());
            listeColonneService.creerColonnesParDefaut(saved.getId());
            System.out.println("[ProjetService] Colonnes Kanban créées avec succès");
        } catch (Exception e) {
            System.err.println("[ProjetService] Erreur création colonnes Kanban: " + e.getMessage());
        }
        // ===================================================================

        ProjetUtilisateur lien = new ProjetUtilisateur();
        lien.setProjetId(saved.getId());
        lien.setUtilisateurId(createur.getId());
        lien.setRole("CHEF_PROJET");
        lien.setActif(true);
        projetUtilisateurRepository.save(lien);

        return convertirEnDTO(saved);
    }

    @Transactional(readOnly = true)
    public boolean utilisateurPeutModifierProjet(Long projetId, String emailUtilisateur) {
        Optional<Projet> pOpt = projetRepository.findById(projetId);
        if (pOpt.isEmpty()) return false;

        Utilisateur u = utilisateurRepository.findByEmail(emailUtilisateur).orElse(null);
        if (u == null) return false;

        Projet p = pOpt.get();
        return p.getCreateur() != null && u.getId().equals(p.getCreateur().getId());
    }

    @Transactional(readOnly = true)
    public boolean estMembreDuProjet(Long utilisateurId, Long projetId) {
        Optional<Projet> pOpt = projetRepository.findById(projetId);
        if (pOpt.isPresent() && pOpt.get().getCreateur() != null) {
            if (utilisateurId.equals(pOpt.get().getCreateur().getId())) return true;
        }
        return projetUtilisateurRepository.existsByProjetIdAndUtilisateurIdAndActif(projetId, utilisateurId, true);
    }

    public void ajouterMembreAuProjet(Long projetId, Long utilisateurId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (projetUtilisateurRepository.existsByProjetIdAndUtilisateurId(projetId, utilisateurId)) {
            System.out.println("Utilisateur déjà membre de ce projet — ajout ignoré.");
            return;
        }

        ProjetUtilisateur pu = new ProjetUtilisateur();
        pu.setProjetId(projet.getId());
        pu.setUtilisateurId(utilisateur.getId());
        pu.setRole("MEMBRE");
        pu.setActif(true);
        projetUtilisateurRepository.save(pu);
    }

    public void retirerMembreDuProjet(Long projetId, Long utilisateurId) {
        if (!projetRepository.existsById(projetId)) {
            throw new RuntimeException("Projet non trouvé");
        }
        if (!projetUtilisateurRepository.existsByProjetIdAndUtilisateurId(projetId, utilisateurId)) {
            throw new RuntimeException("L'utilisateur n'est pas membre de ce projet");
        }
        projetUtilisateurRepository.deleteByProjetIdAndUtilisateurId(projetId, utilisateurId);
    }

    @Transactional(readOnly = true)
    public List<UtilisateurDTO> obtenirMembresProjet(Long projetId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        List<Long> utilisateurIds = projetUtilisateurRepository.findUtilisateurIdsByProjetId(projetId);

        return utilisateurRepository.findAllById(utilisateurIds)
                .stream()
                .map(this::convertirUtilisateurEnDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean utilisateurEstMembreProjet(Long projetId, Long utilisateurId) {
        return projetUtilisateurRepository.existsByProjetIdAndUtilisateurId(projetId, utilisateurId);
    }

    private ProjetDTO convertirEnDTO(Projet p) {
        ProjetDTO dto = new ProjetDTO();
        dto.setId(p.getId());
        dto.setTitre(p.getTitre());
        dto.setDescription(p.getDescription());
        dto.setStatut(p.getStatut().name());
        dto.setDateCreation(p.getDateCreation());
        dto.setPublique(p.getPublique());

        if (p.getCreateur() != null) {
            dto.setIdCreateur(p.getCreateur().getId());
            dto.setCreateurNom(p.getCreateur().getNom());
            dto.setCreateurPrenom(p.getCreateur().getPrenom());
            dto.setCreateurEmail(p.getCreateur().getEmail());
        }

        return dto;
    }

    private UtilisateurDTO convertirUtilisateurEnDTO(Utilisateur u) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(u.getId());
        dto.setNom(u.getNom());
        dto.setPrenom(u.getPrenom());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
        dto.setLangue(u.getLangue());
        dto.setDateInscription(u.getDateInscription());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> listerMembres(Long projetId) {
        if (!projetRepository.existsById(projetId)) {
            throw new RuntimeException("Projet non trouvé");
        }

        List<Long> utilisateurIds = projetUtilisateurRepository.findUtilisateurIdsByProjetId(projetId);
        return utilisateurRepository.findAllById(utilisateurIds);
    }
}