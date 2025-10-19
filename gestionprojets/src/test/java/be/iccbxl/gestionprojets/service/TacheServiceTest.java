package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.enums.PrioriteTache;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.repository.ProjetRepository;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de TacheService.
 * Objectif : couvrir la création de tâche (F7) avec
 * validations,
 * récupération du projet,
 * sauvegarde et mapping DTO.

 * NOTE : on reste en test unitaire pur (pas de contexte Spring),
 * Mock des repositories, et on laisse les méthodes métier de Tache s'exécuter.
 */
@ExtendWith(MockitoExtension.class)
class TacheServiceTest {

    @Mock private TacheRepository tacheRepository;
    @Mock private ProjetRepository projetRepository;
    @Mock private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private TacheService tacheService;

    /**
     * Vérifie que creerTache crée correctement une tâche avec titre/projet valides
     * et renvoie un DTO avec l'ID attribué et la priorité par défaut si non fournie.
     */
    @Test
    @DisplayName("creerTache() - OK avec données valides")
    void creerTache_ok() {
        // Arrange
        Long projetId = 5L;
        Projet projet = new Projet();
        projet.setId(projetId);
        projet.setTitre("Projet A");

        TacheDTO input = new TacheDTO();
        input.setTitre("Faire les maquettes");
        input.setDescription("UI/UX");
        input.setIdProjet(projetId);
        // priorité non fournie -> NORMALE par défaut

        when(projetRepository.findById(projetId)).thenReturn(Optional.of(projet));

        // on simule la persistance : on remet l'objet qu'on reçoit avec un id
        when(tacheRepository.save(any(Tache.class))).thenAnswer(inv -> {
            Tache t = inv.getArgument(0);
            t.setId(100L);
            return t;
        });

        // Act
        TacheDTO result = tacheService.creerTache(input);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Faire les maquettes", result.getTitre());
        assertEquals(projetId, result.getIdProjet());
        assertEquals(PrioriteTache.NORMALE, result.getPriorite());
    }

    /**
     * Vérifie que la création échoue si le projet n'existe pas.
     */
    @Test
    @DisplayName("creerTache() - KO si projet inexistant")
    void creerTache_projetInexistant() {
        // Arrange
        TacheDTO input = new TacheDTO();
        input.setTitre("Tâche X");
        input.setIdProjet(999L);
        when(projetRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tacheService.creerTache(input));
        assertTrue(ex.getMessage().contains("Projet non trouvé"));
    }

    /**
     * Vérifie que la création échoue si le titre est vide.
     */
    @Test
    @DisplayName("creerTache() - KO si titre manquant")
    void creerTache_titreManquant() {
        // Arrange
        TacheDTO input = new TacheDTO();
        input.setIdProjet(1L);
        input.setTitre("  "); // blanc

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tacheService.creerTache(input));
        assertTrue(ex.getMessage().contains("titre"));
    }
}
