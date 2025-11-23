package be.iccbxl.gestionprojets.repository;

import be.iccbxl.gestionprojets.model.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FichierRepository extends JpaRepository<Fichier, Long> {

    /**
     * Récupère tous les fichiers d'un projet
     */
    List<Fichier> findByIdProjetOrderByDateTelechargementDesc(Long idProjet);

    /**
     * Récupère tous les fichiers uploadés par un utilisateur
     */
    List<Fichier> findByIdUploadParOrderByDateTelechargementDesc(Long userId);

    /**
     * Récupère un fichier spécifique dans un projet
     */
    Optional<Fichier> findByIdAndIdProjet(Long id, Long idProjet);

    /**
     * Compte le nombre de fichiers dans un projet
     */
    long countByIdProjet(Long idProjet);

    /**
     * Calcule la taille totale des fichiers d'un projet (en bytes)
     */
    @Query("SELECT COALESCE(SUM(f.taille), 0) FROM Fichier f WHERE f.idProjet = :idProjet")
    Long getTailleTotaleByIdProjet(@Param("idProjet") Long idProjet);

    /**
     * Vérifie si un fichier existe dans un projet
     */
    boolean existsByIdAndIdProjet(Long id, Long idProjet);

    /**
     * Supprime tous les fichiers d'un projet
     */
    void deleteByIdProjet(Long idProjet);
}