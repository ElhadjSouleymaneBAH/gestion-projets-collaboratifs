package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour
 *
 * @author ElhadjSouleymaneBAH
 */
@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

    /**
     * Trouve les projets par créateur (via la relation {@code createur.id}).
     */
    List<Projet> findByCreateurId(Long createurId);

    /**
     * Trouve les projets par créateur AVEC le créateur chargé.
     * Utilise JOIN FETCH pour éviter LazyInitializationException.
     * À utiliser pour la conversion en DTO dans les contrôleurs.
     */
    @Query("SELECT p FROM Projet p LEFT JOIN FETCH p.createur WHERE p.createur.id = :createurId")
    List<Projet> findByCreateurIdWithCreateur(@Param("createurId") Long createurId);

    /**
     * Trouve un projet par ID AVEC le créateur chargé.
     * Utilise JOIN FETCH pour éviter LazyInitializationException.
     */
    @Query("SELECT p FROM Projet p LEFT JOIN FETCH p.createur WHERE p.id = :id")
    Optional<Projet> findByIdWithCreateur(@Param("id") Long id);

    /**
     * Trouve les projets par statut (ex.: ACTIF, SUSPENDU, TERMINE, ARCHIVE).
     */
    List<Projet> findByStatut(String statut);

    /**
     * Recherche plein texte sur le titre (insensible à la casse).
     */
    List<Projet> findByTitreContainingIgnoreCase(String titre);

    /**
     * F3 - Trouve les projets publics pour les visiteurs non connectés.
     * Respecte le cahier des charges : consultation projets publics sans authentification.
     */
    List<Projet> findByPubliqueTrue();

    /**
     * Trouve les projets publics AVEC le créateur chargé.
     */
    @Query("SELECT p FROM Projet p LEFT JOIN FETCH p.createur WHERE p.publique = true")
    List<Projet> findByPubliqueTrueWithCreateur();

    /**
     * Trouve les projets créés par un chef de projet.
     */
    @Query("SELECT p FROM Projet p WHERE p.createur.id = :idChef")
    List<Projet> findByChefDeProjetId(@Param("idChef") Long idChef);

    /**
     * Récupère les membres d’un projet via la table de jointure ProjetUtilisateur.
     * Retourne une liste d’utilisateurs associés à ce projet.
     */
    @Query("SELECT u FROM Utilisateur u " +
            "JOIN ProjetUtilisateur pu ON pu.utilisateurId = u.id " +
            "WHERE pu.projetId = :projetId AND pu.actif = true")
    List<Utilisateur> findMembresByProjetId(@Param("projetId") Long projetId);
}
