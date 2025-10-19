package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.ProjetDTO;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.ProjetUtilisateurRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de ProjetService.
 * Objectif : vérifier la logique métier essentielle :
 * - création de projet avec validations sur le créateur et les champs obligatoires.
 * - mapping entité -> DTO.
 */
@ExtendWith(MockitoExtension.class)
class ProjetServiceTest {

    @Mock private ProjetRepository projetRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private ProjetUtilisateurRepository projetUtilisateurRepository;

    @InjectMocks
    private ProjetService projetService;

    /**
     * Vérifie qu'un projet valide est créé lorsque :
     * - le créateur existe,
     * - titre et description sont fournis.
     * On contrôle aussi les champs renvoyés dans le DTO.
     */
    @Test
    @DisplayName("creerProjet() - OK quand données valides")
    void creerProjet_ok() {
        // Arrange
        Long createurId = 10L;
        Utilisateur createur = new Utilisateur();
        createur.setId(createurId);
        createur.setNom("Doe");
        createur.setPrenom("John");
        createur.setEmail("john.doe@test.com");

        ProjetDTO input = new ProjetDTO();
        input.setIdCreateur(createurId);
        input.setTitre("Nouveau projet");
        input.setDescription("Description claire");
        input.setPublique(Boolean.TRUE);

        when(utilisateurRepository.findById(createurId)).thenReturn(Optional.of(createur));

        // on renvoie l'entité sauvegardée avec un id
        when(projetRepository.save(any(Projet.class))).thenAnswer(inv -> {
            Projet p = inv.getArgument(0);
            p.setId(1L);
            if (p.getDateCreation() == null) {
                p.setDateCreation(LocalDateTime.now());
            }
            return p;
        });

        // Act
        ProjetDTO result = projetService.creerProjet(input);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Nouveau projet", result.getTitre());
        assertEquals("Description claire", result.getDescription());
        assertEquals(createurId, result.getIdCreateur());
        assertTrue(Boolean.TRUE.equals(result.getPublique()));
        assertEquals("John", result.getCreateurPrenom());
        assertEquals("Doe", result.getCreateurNom());
        assertEquals("john.doe@test.com", result.getCreateurEmail());
    }

    /**
     * Vérifie que la création échoue si le créateur n'existe pas.
     */
    @Test
    @DisplayName("creerProjet() - KO si créateur introuvable")
    void creerProjet_createurIntrouvable() {
        // Arrange
        ProjetDTO input = new ProjetDTO();
        input.setIdCreateur(999L);
        input.setTitre("Titre");
        input.setDescription("Desc");
        when(utilisateurRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> projetService.creerProjet(input));
        assertTrue(ex.getMessage().contains("Créateur introuvable"));
    }
}
