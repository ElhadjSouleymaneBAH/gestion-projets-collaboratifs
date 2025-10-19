package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour {@link Projet}.
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
     * Trouve les projets par créateur AVEC le créateur chargé
     * Utilise JOIN FETCH pour éviter LazyInitializationException
     * À UTILISER pour la conversion en DTO dans les contrôleurs
     */
    @Query("SELECT p FROM Projet p LEFT JOIN FETCH p.createur WHERE p.createur.id = :createurId")
    List<Projet> findByCreateurIdWithCreateur(@Param("createurId") Long createurId);

    /**
     * rouve un projet par ID AVEC le créateur chargé
     * Utilise JOIN FETCH pour éviter LazyInitializationException
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
     * Trouve les projets publics AVEC le créateur chargé
     */
    @Query("SELECT p FROM Projet p LEFT JOIN FETCH p.createur WHERE p.publique = true")
    List<Projet> findByPubliqueTrueWithCreateur();
}