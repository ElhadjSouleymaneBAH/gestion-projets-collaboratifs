package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.AbonnementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des abonnements.
 * Support F10 : Paiements et abonnements.
 *
 * @author ElhadjSouleymaneBAH
 */
@Service
public class AbonnementService {

    @Autowired
    private AbonnementRepository abonnementRepository;

    /**
     * Trouve un abonnement par ID utilisateur
     */
    public Optional<Abonnement> findByUtilisateurId(Long utilisateurId) {
        return abonnementRepository.findByUtilisateurId(utilisateurId);
    }

    /**
     * Trouve un abonnement par utilisateur
     */
    public Optional<Abonnement> findByUtilisateur(Utilisateur utilisateur) {
        return abonnementRepository.findByUtilisateur(utilisateur);
    }

    /**
     * Vérifie si un utilisateur a un abonnement actif
     */
    public boolean hasActiveSubscription(Long utilisateurId) {
        Optional<Abonnement> abonnement = findByUtilisateurId(utilisateurId);
        return abonnement.isPresent() && abonnement.get().estValide();
    }

    /**
     * Sauvegarde un abonnement
     */
    public Abonnement save(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    /**
     * Trouve tous les abonnements
     */
    public List<Abonnement> findAll() {
        return abonnementRepository.findAll();
    }

    /**
     * Trouve les abonnements par nom
     */
    public List<Abonnement> findByNom(String nom) {
        return abonnementRepository.findByNom(nom);
    }

    /**
     * Supprime un abonnement par ID
     */
    public void deleteById(Long id) {
        abonnementRepository.deleteById(id);
    }

    /**
     * Trouve un abonnement par ID
     */
    public Optional<Abonnement> findById(Long id) {
        return abonnementRepository.findById(id);
    }

    /**
     * Vérifie si un abonnement existe par ID
     */
    public boolean existsById(Long id) {
        return abonnementRepository.existsById(id);
    }
}