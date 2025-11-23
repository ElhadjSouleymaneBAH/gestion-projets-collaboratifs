package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.FichierDTO;
import be.iccbxl.gestionprojets.exception.ResourceNotFoundException;
import be.iccbxl.gestionprojets.model.Fichier;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.FichierRepository;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FichierService {

    private final FichierRepository fichierRepository;
    private final ProjetRepository projetRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Upload un fichier dans un projet
     */
    @Transactional
    public FichierDTO uploadFichier(MultipartFile file, Long projetId, Long userId) {
        log.info("[Upload] Début upload fichier pour projet {} par utilisateur {}", projetId, userId);

        // Vérifications
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // Nettoyer le nom du fichier
        String nomOriginal = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(nomOriginal);
        String nomUnique = UUID.randomUUID().toString() + extension;

        // Créer le répertoire si nécessaire
        Path uploadPath = Paths.get(uploadDir, "projet-" + projetId);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            log.error("[Upload] Erreur création répertoire: {}", e.getMessage());
            throw new RuntimeException("Impossible de créer le répertoire d'upload", e);
        }

        // Copier le fichier
        Path cheminFichier = uploadPath.resolve(nomUnique);
        try {
            Files.copy(file.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);
            log.info("[Upload] ✅ Fichier copié: {}", cheminFichier);
        } catch (IOException e) {
            log.error("[Upload] ❌ Erreur copie fichier: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de l'upload du fichier", e);
        }

        // Sauvegarder en BDD
        Fichier fichier = new Fichier();
        fichier.setNom(nomUnique);
        fichier.setNomOriginal(nomOriginal);
        fichier.setTypeMime(file.getContentType());
        fichier.setTaille(file.getSize());
        fichier.setChemin(cheminFichier.toString());
        fichier.setIdProjet(projetId);
        fichier.setIdUploadPar(userId);
        fichier.setDateTelechargement(LocalDateTime.now());

        Fichier savedFichier = fichierRepository.save(fichier);
        log.info("[Upload] ✅ Fichier enregistré en BDD: ID {}", savedFichier.getId());

        return convertToDTO(savedFichier, utilisateur);
    }

    /**
     * Récupère tous les fichiers d'un projet
     */
    @Transactional(readOnly = true)
    public List<FichierDTO> getFichiersByProjet(Long projetId) {
        log.info("[Get] Récupération fichiers du projet {}", projetId);

        List<Fichier> fichiers = fichierRepository.findByIdProjetOrderByDateTelechargementDesc(projetId);

        return fichiers.stream()
                .map(f -> {
                    Utilisateur uploader = utilisateurRepository.findById(f.getIdUploadPar()).orElse(null);
                    return convertToDTO(f, uploader);
                })
                .collect(Collectors.toList());
    }

    /**
     * Télécharge un fichier
     */
    @Transactional(readOnly = true)
    public Resource telechargerFichier(Long fichierId, Long projetId) {
        log.info("[Download] Téléchargement fichier {} du projet {}", fichierId, projetId);

        Fichier fichier = fichierRepository.findByIdAndIdProjet(fichierId, projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Fichier introuvable"));

        try {
            Path filePath = Paths.get(fichier.getChemin()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                log.info("[Download] ✅ Fichier trouvé: {}", fichier.getNomOriginal());
                return resource;
            } else {
                log.error("[Download] ❌ Fichier physique introuvable: {}", filePath);
                throw new ResourceNotFoundException("Fichier physique introuvable: " + fichier.getNomOriginal());
            }
        } catch (MalformedURLException e) {
            log.error("[Download] ❌ Erreur chemin fichier: {}", e.getMessage());
            throw new RuntimeException("Erreur lors du téléchargement", e);
        }
    }

    /**
     * Supprime un fichier
     */
    @Transactional
    public void supprimerFichier(Long fichierId, Long projetId, Long userId) {
        log.info("[Delete] Suppression fichier {} du projet {} par utilisateur {}", fichierId, projetId, userId);

        Fichier fichier = fichierRepository.findByIdAndIdProjet(fichierId, projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Fichier introuvable"));

        // Supprimer le fichier physique
        try {
            Path filePath = Paths.get(fichier.getChemin());
            Files.deleteIfExists(filePath);
            log.info("[Delete] ✅ Fichier physique supprimé");
        } catch (IOException e) {
            log.warn("[Delete] ⚠️ Impossible de supprimer le fichier physique: {}", e.getMessage());
        }

        // Supprimer de la BDD
        fichierRepository.delete(fichier);
        log.info("[Delete] ✅ Fichier supprimé de la BDD");
    }

    /**
     * Récupère les infos d'un fichier
     */
    @Transactional(readOnly = true)
    public FichierDTO getFichierInfo(Long fichierId, Long projetId) {
        Fichier fichier = fichierRepository.findByIdAndIdProjet(fichierId, projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Fichier introuvable"));

        Utilisateur uploader = utilisateurRepository.findById(fichier.getIdUploadPar()).orElse(null);
        return convertToDTO(fichier, uploader);
    }

    /**
     * Convertit une entité Fichier en DTO
     */
    private FichierDTO convertToDTO(Fichier fichier, Utilisateur uploader) {
        String uploaderNom = uploader != null
                ? uploader.getPrenom() + " " + uploader.getNom()
                : "Utilisateur inconnu";

        return FichierDTO.builder()
                .id(fichier.getId())
                .nom(fichier.getNom())
                .nomOriginal(fichier.getNomOriginal())
                .typeMime(fichier.getTypeMime())
                .taille(fichier.getTaille())
                .tailleLisible(FichierDTO.formatTaille(fichier.getTaille()))
                .projetId(fichier.getIdProjet())
                .uploadParId(fichier.getIdUploadPar())
                .uploadParNom(uploaderNom)
                .dateUpload(fichier.getDateTelechargement())
                .build();
    }

    /**
     * Récupère l'extension d'un fichier
     */
    private String getExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return filename.substring(lastIndexOf);
    }
}