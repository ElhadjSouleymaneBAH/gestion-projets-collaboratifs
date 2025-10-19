package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.enums.StatutAbonnement;
import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.AbonnementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de AbonnementService.
 * Couvre la logique F10 :
 * - hasActiveSubscription() selon statut/date fin
 * - renouvelerAbonnement() pour les cas "actif" et "expiré".
 */
@ExtendWith(MockitoExtension.class)
class AbonnementServiceTest {

    @Mock private AbonnementRepository abonnementRepository;

    @InjectMocks
    private AbonnementService abonnementService;

    /**
     * Vérifie que hasActiveSubscription() renvoie true quand
     * l'abonnement est ACTIF et non expiré.
     */
    @Test
    @DisplayName("hasActiveSubscription() - true si ACTIF et date fin future")
    void hasActiveSubscription_true() {
        Long userId = 7L;
        Utilisateur u = new Utilisateur();
        u.setId(userId);

        Abonnement abo = new Abonnement();
        abo.setId(1L);
        abo.setUtilisateur(u);
        abo.setStatut(StatutAbonnement.ACTIF);
        abo.setDateFin(LocalDate.now().plusDays(10));

        when(abonnementRepository.findByUtilisateurIdWithUtilisateur(userId))
                .thenReturn(Optional.of(abo));

        assertTrue(abonnementService.hasActiveSubscription(userId));
    }

    /**
     * Vérifie que hasActiveSubscription() renvoie false si pas d'abonnement,
     * ou si expiré.
     */
    @Test
    @DisplayName("hasActiveSubscription() - false si inexistant ou expiré")
    void hasActiveSubscription_false() {
        Long userId = 8L;

        when(abonnementRepository.findByUtilisateurIdWithUtilisateur(userId))
                .thenReturn(Optional.empty());
        assertFalse(abonnementService.hasActiveSubscription(userId));

        Utilisateur u = new Utilisateur();
        u.setId(userId);
        Abonnement expiré = new Abonnement();
        expiré.setUtilisateur(u);
        expiré.setStatut(StatutAbonnement.ACTIF);
        expiré.setDateFin(LocalDate.now().minusDays(1));

        when(abonnementRepository.findByUtilisateurIdWithUtilisateur(userId))
                .thenReturn(Optional.of(expiré));
        assertFalse(abonnementService.hasActiveSubscription(userId));
    }

    /**
     * Vérifie le renouvellement d'un abonnement ACTIF :
     * on ajoute '1' mois à la date de fin actuelle.
     */
    @Test
    @DisplayName("renouvelerAbonnement() - prolonge de 1 mois si actif")
    void renouvelerAbonnement_actif() {
        Long aboId = 12L;
        Utilisateur u = new Utilisateur();
        u.setId(3L);

        LocalDate finActuelle = LocalDate.now().plusDays(5);

        Abonnement abo = new Abonnement();
        abo.setId(aboId);
        abo.setUtilisateur(u);
        abo.setStatut(StatutAbonnement.ACTIF);
        abo.setDateDebut(LocalDate.now().minusMonths(1));
        abo.setDateFin(finActuelle);

        when(abonnementRepository.findByIdWithUtilisateur(aboId)).thenReturn(Optional.of(abo));
        when(abonnementRepository.save(abo)).thenReturn(abo);

        Abonnement result = abonnementService.renouvelerAbonnement(aboId);

        assertNotNull(result);
        assertEquals(finActuelle.plusMonths(1), result.getDateFin());
        assertEquals(StatutAbonnement.ACTIF, result.getStatut());
    }

    /**
     * Vérifie le renouvellement d'un abonnement expiré :
     * on repart d'aujourd'hui + 1 mois.
     */
    @Test
    @DisplayName("renouvelerAbonnement() - repart d'aujourd'hui si expiré")
    void renouvelerAbonnement_expire() {
        Long aboId = 15L;
        Utilisateur u = new Utilisateur();
        u.setId(9L);

        LocalDate today = LocalDate.now();

        Abonnement abo = new Abonnement();
        abo.setId(aboId);
        abo.setUtilisateur(u);
        abo.setStatut(StatutAbonnement.RESILIE); // peu importe, expiré prime
        abo.setDateFin(today.minusDays(1));

        when(abonnementRepository.findByIdWithUtilisateur(aboId)).thenReturn(Optional.of(abo));
        when(abonnementRepository.save(abo)).thenReturn(abo);

        Abonnement result = abonnementService.renouvelerAbonnement(aboId);

        assertNotNull(result);
        assertEquals(today.plusMonths(1), result.getDateFin());
        assertEquals(today, result.getDateDebut());
        assertEquals(StatutAbonnement.ACTIF, result.getStatut());
    }
}
