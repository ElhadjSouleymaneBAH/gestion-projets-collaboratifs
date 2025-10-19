package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.enums.StatutAbonnement;
import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.AbonnementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des abonnements.
 * Implémente la logique métier de F10 : Paiements et abonnements.
 *
 * @author ElhadjSouleymaneBAH
 */
@Service
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;

    public AbonnementService(AbonnementRepository abonnementRepository) {
        this.abonnementRepository = abonnementRepository;
    }

    // ========== CRUD DE BASE ==========

    /**
     * Sauvegarde ou met à jour un abonnement.
     *
     * @param abonnement L'abonnement à sauvegarder
     * @return L'abonnement sauvegardé
     */
    @Transactional
    public Abonnement save(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    /**
     * Trouve un abonnement par ID (SANS charger l'utilisateur).
     *
     * ⚠️ ATTENTION : Ne pas utiliser pour conversion en DTO dans les contrôleurs.
     * Utiliser findByIdWithUtilisateur() à la place.
     *
     * @param id L'ID de l'abonnement
     * @return Optional contenant l'abonnement si trouvé
     */
    @Transactional(readOnly = true)
    public Optional<Abonnement> findById(Long id) {
        return abonnementRepository.findById(id);
    }

    /**
     * 🔧 MÉTHODE PRINCIPALE : Trouve un abonnement par ID AVEC l'utilisateur chargé.
     *
     * À UTILISER dans les contrôleurs pour éviter LazyInitializationException.
     *
     * @param id L'ID de l'abonnement
     * @return Optional contenant l'abonnement avec utilisateur chargé
     */
    @Transactional(readOnly = true)
    public Optional<Abonnement> findByIdWithUtilisateur(Long id) {
        return abonnementRepository.findByIdWithUtilisateur(id);
    }

    /**
     * Liste tous les abonnements SANS les utilisateurs.
     *
     * @return Liste de tous les abonnements
     */
    @Transactional(readOnly = true)
    public List<Abonnement> findAll() {
        return abonnementRepository.findAll();
    }

    /**
     * 🔧 MÉTHODE PRINCIPALE : Liste tous les abonnements AVEC leurs utilisateurs.
     *
     * Utilise JOIN FETCH pour charger les utilisateurs en UNE SEULE requête.
     * À UTILISER dans le contrôleur admin pour la liste complète.
     *
     * Conforme aux exigences non fonctionnelles :
     * - Performance : évite le problème N+1 (temps de réponse < 2s)
     * - Maintenance : bonnes pratiques
     *
     * @return Liste de tous les abonnements avec utilisateurs chargés
     */
    @Transactional(readOnly = true)
    public List<Abonnement> findAllWithUtilisateurs() {
        return abonnementRepository.findAllWithUtilisateurs();
    }

    /**
     * 🔧 MÉTHODE PRINCIPALE : Trouve l'abonnement d'un utilisateur par ID utilisateur AVEC l'utilisateur chargé.
     *
     * Utilise JOIN FETCH pour éviter LazyInitializationException.
     * À UTILISER dans les contrôleurs pour conversion en DTO.
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return Optional contenant l'abonnement avec utilisateur chargé
     */
    @Transactional(readOnly = true)
    public Optional<Abonnement> findByUtilisateurIdWithUtilisateur(Long utilisateurId) {
        return abonnementRepository.findByUtilisateurIdWithUtilisateur(utilisateurId);
    }

    /**
     * Trouve l'abonnement d'un utilisateur par ID utilisateur (SANS charger l'utilisateur).
     *
     * ⚠️ ATTENTION : Ne pas utiliser pour conversion en DTO.
     * Utiliser findByUtilisateurIdWithUtilisateur() à la place.
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return Optional contenant l'abonnement si trouvé
     */
    @Transactional(readOnly = true)
    public Optional<Abonnement> findByUtilisateurId(Long utilisateurId) {
        return abonnementRepository.findByUtilisateurId(utilisateurId);
    }

    /**
     * Trouve l'abonnement d'un utilisateur par objet Utilisateur.
     *
     * @param utilisateur L'objet Utilisateur
     * @return Optional contenant l'abonnement si trouvé
     */
    @Transactional(readOnly = true)
    public Optional<Abonnement> findByUtilisateur(Utilisateur utilisateur) {
        return abonnementRepository.findByUtilisateur(utilisateur);
    }

    /**
     * Vérifie si un utilisateur a un abonnement actif.
     *
     * Requis pour F6, F7, F8 (abonnement actif obligatoire).
     * Conforme au cahier des charges : "Chef de projet (Abonné) : Doit souscrire à un abonnement mensuel".
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return true si l'utilisateur a un abonnement actif et non expiré, false sinon
     */
    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(Long utilisateurId) {
        Optional<Abonnement> abonnementOpt = abonnementRepository.findByUtilisateurIdWithUtilisateur(utilisateurId);

        if (abonnementOpt.isEmpty()) {
            return false;
        }

        Abonnement abonnement = abonnementOpt.get();
        LocalDate aujourdhui = LocalDate.now();

        // Vérifier statut ACTIF ET date de fin valide
        return abonnement.getStatut() == StatutAbonnement.ACTIF
                && abonnement.getDateFin() != null
                && !abonnement.getDateFin().isBefore(aujourdhui);
    }

    /**
     * Vérifie si un abonnement existe par ID.
     *
     * @param id L'ID de l'abonnement
     * @return true si l'abonnement existe, false sinon
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return abonnementRepository.existsById(id);
    }

    /**
     * Supprime un abonnement par ID.
     *
     * @param id L'ID de l'abonnement
     */
    @Transactional
    public void deleteById(Long id) {
        abonnementRepository.deleteById(id);
    }

    /**
     * Compte le nombre d'abonnements d'un utilisateur.
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return Nombre d'abonnements de l'utilisateur
     */
    @Transactional(readOnly = true)
    public long countByUtilisateurId(Long utilisateurId) {
        return abonnementRepository.countByUtilisateurId(utilisateurId);
    }

    // ========== LOGIQUE MÉTIER F10 ==========

    /**
     * MÉTHODE CORRIGÉE : Renouvelle un abonnement pour un mois supplémentaire.
     *
     * Utilisée dans F10 pour le renouvellement automatique ou manuel.
     *
     * Logique :
     * - Si abonnement actif (date fin future) : ajoute 1 mois à la date de fin actuelle
     * - Si abonnement expiré (date fin passée) : repart de aujourd'hui + 1 mois
     *
     * @param id L'ID de l'abonnement
     * @return L'abonnement renouvelé
     * @throws RuntimeException Si l'abonnement n'existe pas ou si l'utilisateur n'est pas chargé
     */
    @Transactional
    public Abonnement renouvelerAbonnement(Long id) {

        Abonnement abonnement = abonnementRepository.findByIdWithUtilisateur(id)
                .orElseThrow(() -> new RuntimeException("Abonnement non trouvé avec l'ID: " + id));

        // Vérifier que l'utilisateur est bien chargé
        if (abonnement.getUtilisateur() == null) {
            throw new RuntimeException("Utilisateur non chargé pour l'abonnement: " + id);
        }

        LocalDate aujourdhui = LocalDate.now();
        LocalDate nouvelleDateFin;

        if (abonnement.getDateFin() != null && abonnement.getDateFin().isAfter(aujourdhui)) {
            // CAS 1 : Abonnement non expiré → Ajouter 1 mois à la date de fin actuelle
            nouvelleDateFin = abonnement.getDateFin().plusMonths(1);
            System.out.println("DEBUG: [F10] Abonnement actif - Prolongation de " +
                    abonnement.getDateFin() + " à " + nouvelleDateFin);
        } else {
            // CAS 2 : Abonnement expiré → Repartir de aujourd'hui + 1 mois
            abonnement.setDateDebut(aujourdhui);
            nouvelleDateFin = aujourdhui.plusMonths(1);
            System.out.println("DEBUG: [F10] Abonnement expiré - Nouvelle période : " +
                    aujourdhui + " à " + nouvelleDateFin);
        }

        abonnement.setDateFin(nouvelleDateFin);
        abonnement.setStatut(StatutAbonnement.ACTIF);

        System.out.println("DEBUG: [F10] Abonnement " + id + " renouvelé jusqu'au " + nouvelleDateFin);

        return abonnementRepository.save(abonnement);
    }

    /**
     * NOUVELLE MÉTHODE : Résilie un abonnement.
     *
     * Change le statut en RESILIE sans modifier les dates.
     * L'utilisateur perd immédiatement l'accès aux fonctionnalités premium.
     *
     * @param id L'ID de l'abonnement
     * @return L'abonnement résilié
     * @throws RuntimeException Si l'abonnement n'existe pas
     */
    @Transactional
    public Abonnement resilierAbonnement(Long id) {

        Abonnement abonnement = abonnementRepository.findByIdWithUtilisateur(id)
                .orElseThrow(() -> new RuntimeException("Abonnement non trouvé avec l'ID: " + id));

        abonnement.setStatut(StatutAbonnement.RESILIE);

        System.out.println("DEBUG: [F10] Abonnement " + id + " résilié");

        return abonnementRepository.save(abonnement);
    }

    /**
     * 🔧 NOUVELLE MÉTHODE : Réactive un abonnement expiré ou résilié.
     *
     * Réinitialise les dates à partir d'aujourd'hui pour la durée initiale de l'abonnement.
     * Utilisé lors de la re-souscription d'un utilisateur ayant déjà un abonnement inactif.
     *
     * @param id L'ID de l'abonnement
     * @return L'abonnement réactivé
     * @throws RuntimeException Si l'abonnement n'existe pas
     */
    @Transactional
    public Abonnement reactiverAbonnement(Long id) {
        // 🔧 CORRECTION : Utiliser findByIdWithUtilisateur
        Abonnement abonnement = abonnementRepository.findByIdWithUtilisateur(id)
                .orElseThrow(() -> new RuntimeException("Abonnement non trouvé avec l'ID: " + id));

        LocalDate aujourdhui = LocalDate.now();
        Integer duree = (abonnement.getDuree() != null && abonnement.getDuree() > 0)
                ? abonnement.getDuree()
                : 1; // Durée par défaut : 1 mois

        abonnement.setDateDebut(aujourdhui);
        abonnement.setDateFin(aujourdhui.plusMonths(duree));
        abonnement.setStatut(StatutAbonnement.ACTIF);

        System.out.println("DEBUG: [F10] Abonnement " + id + " réactivé du " +
                aujourdhui + " au " + abonnement.getDateFin());

        return abonnementRepository.save(abonnement);
    }
}