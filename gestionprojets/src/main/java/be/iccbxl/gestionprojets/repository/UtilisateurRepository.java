package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Retourne les utilisateurs MEMBRE qui ne participent à aucun projet.
     */
    @Query(value = "SELECT u.* FROM utilisateurs u " +
            "WHERE u.role = 'MEMBRE' " +
            "AND u.id NOT IN (SELECT utilisateur_id FROM projet_utilisateurs)", nativeQuery = true)
    List<Utilisateur> findUtilisateursSansProjet();

    /**
     * Retourne tous les utilisateurs déjà membres d’un projet donné.
     *
     * @param projetId ID du projet
     * @return liste des utilisateurs du projet
     */
    @Query(value = "SELECT u.* FROM utilisateurs u " +
            "JOIN projet_utilisateurs pu ON pu.utilisateur_id = u.id " +
            "WHERE pu.projet_id = :projetId", nativeQuery = true)
    List<Utilisateur> findUtilisateursParProjet(@Param("projetId") Long projetId);
}
