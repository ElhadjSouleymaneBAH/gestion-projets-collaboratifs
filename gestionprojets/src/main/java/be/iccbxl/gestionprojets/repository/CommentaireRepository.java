package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    /**
     * Obtenir tous les commentaires d'une tâche
     */
    @Query("SELECT c FROM Commentaire c WHERE c.tache.id = :tacheId ORDER BY c.date ASC")
    List<Commentaire> findByTacheIdOrderByDateAsc(@Param("tacheId") Long tacheId);

    /**
     * Obtenir les commentaires d'un utilisateur
     */
    @Query("SELECT c FROM Commentaire c WHERE c.auteur.id = :utilisateurId ORDER BY c.date DESC")
    List<Commentaire> findByAuteurIdOrderByDateDesc(@Param("utilisateurId") Long utilisateurId);

    /**
     * Compter les commentaires d'une tâche
     */
    @Query("SELECT COUNT(c) FROM Commentaire c WHERE c.tache.id = :tacheId")
    Long countByTacheId(@Param("tacheId") Long tacheId);
}