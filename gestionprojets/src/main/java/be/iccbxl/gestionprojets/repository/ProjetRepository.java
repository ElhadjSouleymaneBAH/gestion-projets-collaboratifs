package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour {@link Projet}.
 *
  */
@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

    /**
     * Trouve les projets par créateur (via la relation {@code createur.id}).
     * @param createurId identifiant de l'utilisateur créateur
     * @return liste des projets créés par cet utilisateur
     */
    List<Projet> findByCreateurId(Long createurId);

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
     * @return liste des projets où publique = true
     */
    List<Projet> findByPubliqueTrue();
}