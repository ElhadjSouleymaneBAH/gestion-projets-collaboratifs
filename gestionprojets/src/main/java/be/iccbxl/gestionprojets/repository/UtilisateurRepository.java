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

    @Query(value = "Select u.*FROM utilisateurs u WHERE u.role = 'MEMBRE' and u.id not in ( Select utilisateur_id FROM projet_utilisateurs)", nativeQuery = true)
    List<Utilisateur> findUtilisateurId();// u.nom,u.prenom,u.id récuperer les utilisateur qui n'ont pas de projets
}
