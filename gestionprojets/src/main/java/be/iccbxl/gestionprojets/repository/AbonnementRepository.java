package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour les abonnements.
 * Support F10 : Paiements et abonnements.
 *
 * @author ElhadjSouleymaneBAH
 */
@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {

    /**
     * Trouve l'abonnement d'un utilisateur par objet Utilisateur.
     *
     * @param utilisateur L'objet Utilisateur
     * @return Optional contenant l'abonnement si trouvé
     */
    Optional<Abonnement> findByUtilisateur(Utilisateur utilisateur);

    /**
     * Trouve l'abonnement d'un utilisateur par ID utilisateur (SANS charger l'utilisateur).
     *
     * ⚠️ ATTENTION : Causera LazyInitializationException si on accède à getUtilisateur()
     * en dehors d'une transaction (par exemple dans un contrôleur lors de la conversion en DTO).
     *
     * Solution : Utiliser findByUtilisateurIdWithUtilisateur() dans les contrôleurs.
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return Optional contenant l'abonnement si trouvé
     */
    Optional<Abonnement> findByUtilisateurId(Long utilisateurId);

    /**
     * 🔧 MÉTHODE PRINCIPALE : Trouve l'abonnement d'un utilisateur AVEC l'utilisateur chargé.
     *
     * Utilise JOIN FETCH pour charger l'utilisateur en une seule requête et éviter
     * LazyInitializationException lors de la conversion en DTO.
     *
     * Conforme aux exigences non fonctionnelles du cahier des charges :
     * - Performance : évite le problème N+1 (temps de réponse < 2s)
     * - Maintenance : bonnes pratiques (pattern eager loading explicite)
     *
     * À UTILISER dans les contrôleurs pour la conversion en DTO.
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return Optional contenant l'abonnement avec utilisateur chargé
     */
    @Query("SELECT a FROM Abonnement a LEFT JOIN FETCH a.utilisateur u WHERE u.id = :utilisateurId")
    Optional<Abonnement> findByUtilisateurIdWithUtilisateur(@Param("utilisateurId") Long utilisateurId);

    /**
     * 🔧 NOUVELLE MÉTHODE : Trouve un abonnement par ID AVEC l'utilisateur chargé.
     *
     * Utilise JOIN FETCH pour charger l'utilisateur en une seule requête.
     * Utilisée dans renouvelerAbonnement(), resilierAbonnement() et reactiverAbonnement()
     * pour éviter LazyInitializationException.
     *
     * Conforme aux exigences non fonctionnelles :
     * - Performance : une seule requête SQL au lieu de deux
     * - Sécurité : évite les erreurs runtime
     *
     * @param id L'ID de l'abonnement
     * @return Optional contenant l'abonnement avec utilisateur chargé
     */
    @Query("SELECT a FROM Abonnement a LEFT JOIN FETCH a.utilisateur WHERE a.id = :id")
    Optional<Abonnement> findByIdWithUtilisateur(@Param("id") Long id);

    /**
     * Vérifie si un utilisateur a un abonnement par son ID.
     *
     * Utilisé pour vérifier rapidement l'existence d'un abonnement sans le charger.
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return true si l'utilisateur a un abonnement, false sinon
     */
    boolean existsByUtilisateurId(Long utilisateurId);

    /**
     * Trouve tous les abonnements par nom.
     *
     * @param nom Le nom de l'abonnement (ex: "Plan Premium Mensuel")
     * @return Liste des abonnements correspondants
     */
    List<Abonnement> findByNom(String nom);

    /**
     * Compte les abonnements par utilisateur ID.
     *
     * Utilisé pour vérifier si un utilisateur a déjà souscrit plusieurs fois.
     *
     * @param utilisateurId L'ID de l'utilisateur
     * @return Nombre d'abonnements de l'utilisateur
     */
    long countByUtilisateurId(Long utilisateurId);

    /**
     * 🔧 MÉTHODE PRINCIPALE : Charge TOUS les abonnements AVEC leurs utilisateurs.
     *
     * Utilise JOIN FETCH pour éviter :
     * - Le problème N+1 (une requête par abonnement pour charger l'utilisateur)
     * - Les LazyInitializationException
     * - L'affichage "Inconnu" dans le frontend
     *
     * Conforme aux exigences non fonctionnelles :
     * - Performance : une seule requête SQL au lieu de N+1
     * - Maintenance : code prévisible et sans erreurs runtime
     *
     * À UTILISER dans le contrôleur admin pour lister tous les abonnements (F10 Admin).
     *
     * @return Liste de tous les abonnements avec leurs utilisateurs chargés
     */
    @Query("SELECT a FROM Abonnement a LEFT JOIN FETCH a.utilisateur")
    List<Abonnement> findAllWithUtilisateurs();
}