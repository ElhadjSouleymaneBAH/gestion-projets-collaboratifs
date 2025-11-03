package be.iccbxl.gestionprojets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service de traduction centralisé pour toutes les données de l'application.
 * Traduit les statuts, rôles, types, et autres données selon la langue demandée.
 */
@Service
public class TranslationService {

    @Autowired
    private MessageSource messageSource;

    /**
     * Traduit un statut de projet
     * @param statut Le statut à traduire (ex: "ACTIF", "TERMINE")
     * @param locale La langue cible (fr, en)
     * @return Le statut traduit
     */
    public String translateProjetStatut(String statut, Locale locale) {
        if (statut == null || statut.isEmpty()) {
            return statut;
        }
        try {
            return messageSource.getMessage("statut.projet." + statut, null, statut, locale);
        } catch (Exception e) {
            return statut; // Retourne la valeur originale si pas de traduction
        }
    }

    /**
     * Traduit un statut de tâche
     * @param statut Le statut à traduire (ex: "BROUILLON", "EN_ATTENTE_VALIDATION")
     * @param locale La langue cible (fr, en)
     * @return Le statut traduit
     */
    public String translateTacheStatut(String statut, Locale locale) {
        if (statut == null || statut.isEmpty()) {
            return statut;
        }
        try {
            return messageSource.getMessage("statut.tache." + statut, null, statut, locale);
        } catch (Exception e) {
            return statut;
        }
    }

    /**
     * Traduit une priorité de tâche
     * @param priorite La priorité à traduire (ex: "BASSE", "HAUTE")
     * @param locale La langue cible (fr, en)
     * @return La priorité traduite
     */
    public String translatePriorite(String priorite, Locale locale) {
        if (priorite == null || priorite.isEmpty()) {
            return priorite;
        }
        try {
            return messageSource.getMessage("priorite." + priorite, null, priorite, locale);
        } catch (Exception e) {
            return priorite;
        }
    }

    /**
     * Traduit un rôle utilisateur
     * @param role Le rôle à traduire (ex: "ADMIN", "CHEF_PROJET")
     * @param locale La langue cible (fr, en)
     * @return Le rôle traduit
     */
    public String translateRole(String role, Locale locale) {
        if (role == null || role.isEmpty()) {
            return role;
        }
        try {
            return messageSource.getMessage("role." + role, null, role, locale);
        } catch (Exception e) {
            return role;
        }
    }

    /**
     * Traduit un statut de facture
     * @param statut Le statut à traduire (ex: "GENEREE", "PAYEE")
     * @param locale La langue cible (fr, en)
     * @return Le statut traduit
     */
    public String translateFactureStatut(String statut, Locale locale) {
        if (statut == null || statut.isEmpty()) {
            return statut;
        }
        try {
            return messageSource.getMessage("statut.facture." + statut, null, statut, locale);
        } catch (Exception e) {
            return statut;
        }
    }

    /**
     * Traduit un type d'abonnement
     * @param type Le type à traduire (ex: "PREMIUM", "GRATUIT")
     * @param locale La langue cible (fr, en)
     * @return Le type traduit
     */
    public String translateAbonnementType(String type, Locale locale) {
        if (type == null || type.isEmpty()) {
            return type;
        }
        try {
            return messageSource.getMessage("abonnement.type." + type, null, type, locale);
        } catch (Exception e) {
            return type;
        }
    }

    /**
     * Traduit un statut d'abonnement
     * @param statut Le statut à traduire (ex : "ACTIF", "EXPIRE")
     * @param locale La langue cible (fr, en)
     * @return Le statut traduit
     */
    public String translateAbonnementStatut(String statut, Locale locale) {
        if (statut == null || statut.isEmpty()) {
            return statut;
        }
        try {
            return messageSource.getMessage("abonnement.statut." + statut, null, statut, locale);
        } catch (Exception e) {
            return statut;
        }
    }

    /**
     * Traduit un type de notification
     * @param type Le type à traduire (ex : "TACHE", "PROJET")
     * @param locale La langue cible (fr, en)
     * @return Le type traduit
     */
    public String translateNotificationType(String type, Locale locale) {
        if (type == null || type.isEmpty()) {
            return type;
        }
        try {
            return messageSource.getMessage("notification.type." + type, null, type, locale);
        } catch (Exception e) {
            return type;
        }
    }

    /**
     * Traduit une visibilité de projet
     * @param visibilite La visibilité à traduire (ex: "PRIVE", "PUBLIC")
     * @param locale La langue cible (fr, en)
     * @return La visibilité traduite
     */
    public String translateVisibilite(String visibilite, Locale locale) {
        if (visibilite == null || visibilite.isEmpty()) {
            return visibilite;
        }
        try {
            return messageSource.getMessage("visibilite." + visibilite, null, visibilite, locale);
        } catch (Exception e) {
            return visibilite;
        }
    }

    /**
     * Traduit un mois
     * @param mois Le mois à traduire (ex: "JANVIER", "DECEMBRE")
     * @param locale La langue cible (fr, en)
     * @return Le mois traduit
     */
    public String translateMois(String mois, Locale locale) {
        if (mois == null || mois.isEmpty()) {
            return mois;
        }
        try {
            // Normaliser le mois en majuscules sans accents
            String moisNormalise = mois.toUpperCase()
                    .replace("É", "E")
                    .replace("Û", "U");
            return messageSource.getMessage("mois." + moisNormalise, null, mois, locale);
        } catch (Exception e) {
            return mois;
        }
    }

    /**
     * Traduit la description d'un abonnement Premium
     * @param locale La langue cible (fr, en)
     * @return La description traduite
     */
    public String translateAbonnementDescription(Locale locale) {
        try {
            return messageSource.getMessage("abonnement.description.premium", null, locale);
        } catch (Exception e) {
            return "Abonnement Premium CollabPro - Mensuel";
        }
    }

    /**
     * Traduit une période complète (ex: "Période : Décembre 2025")
     * @param mois Le mois
     * @param annee L'année
     * @param locale La langue cible
     * @return La période traduite
     */
    public String translatePeriode(String mois, String annee, Locale locale) {
        String moisTraduit = translateMois(mois, locale);
        return moisTraduit + " " + annee;
    }

    /**
     * Méthode générique pour traduire n'importe quelle clé
     * @param key La clé de traduction (ex: "label.description")
     * @param locale La langue cible
     * @return La traduction
     */
    public String translate(String key, Locale locale) {
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            return key;
        }
    }

    /**
     * Méthode générique avec paramètres
     * @param key La clé de traduction
     * @param args Les arguments à insérer dans la traduction
     * @param locale La langue cible
     * @return La traduction avec paramètres
     */
    public String translate(String key, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            return key;
        }
    }
}
