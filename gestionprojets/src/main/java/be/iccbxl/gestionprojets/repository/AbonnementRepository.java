package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
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
     */
    Optional<Abonnement> findByUtilisateur(Utilisateur utilisateur);

    /**
     * Trouve l'abonnement d'un utilisateur par ID utilisateur.
     */
    Optional<Abonnement> findByUtilisateurId(Long utilisateurId);

    /**
     * Vérifie si un utilisateur a un abonnement par son ID.
     */
    boolean existsByUtilisateurId(Long utilisateurId);

    /**
     * Trouve tous les abonnements actifs par nom.
     */
    List<Abonnement> findByNom(String nom);

    /**
     * Compte les abonnements par utilisateur ID.
     */
    long countByUtilisateurId(Long utilisateurId);
}