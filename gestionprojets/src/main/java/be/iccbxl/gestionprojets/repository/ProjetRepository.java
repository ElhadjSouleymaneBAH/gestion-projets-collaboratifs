package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour {@link Projet}.
 *
 * ⚠️ L'entité Projet possède maintenant un champ {@code Utilisateur createur}
 * (FK mappée sur la colonne {@code id_createur}). Utiliser des méthodes
 * dérivées basées sur {@code createur.id} et non plus {@code idCreateur}.
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
}
