package be.iccbxl.gestionprojets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Service de traduction centralisé pour toutes les données de l'application.
 * Traduit les statuts, rôles, types, et autres données selon la langue demandée.
 * Inclut la traduction automatique pour les contenus dynamiques (titres, descriptions).
 */
@Service
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    @Autowired
    private MessageSource messageSource;

    // Cache pour traductions automatiques
    private final Map<String, String> cacheTraduction = new HashMap<>();

    // ==================== TRADUCTION AUTOMATIQUE (MODE FALLBACK) ====================

    /**
     * Traduit automatiquement un texte libre (titres, descriptions)
     * VERSION FALLBACK : Ajoute [EN] devant le texte pour indiquer qu'il devrait être traduit
     * @param texte Texte à traduire
     * @param locale Langue cible
     * @return Texte avec marqueur de langue ou original si français
     */
    public String traduireTexteAutomatique(String texte, Locale locale) {
        if (texte == null || texte.trim().isEmpty()) {
            return texte;
        }

        String langueCible = locale.getLanguage();

        // Si français, pas de traduction
        if ("fr".equalsIgnoreCase(langueCible)) {
            return texte;
        }

        // Clé de cache
        String cacheKey = langueCible + ":" + texte;

        // Vérifier le cache
        if (cacheTraduction.containsKey(cacheKey)) {
            return cacheTraduction.get(cacheKey);
        }

        // Mode fallback : retourner le texte avec marqueur [EN]
        logger.debug("🔄 Traduction fallback (mode test) FR → {} : {}",
                langueCible.toUpperCase(),
                texte.substring(0, Math.min(50, texte.length())));

        String fallback = "[" + langueCible.toUpperCase() + "] " + texte;
        cacheTraduction.put(cacheKey, fallback);
        return fallback;
    }

    /**
     * Vide le cache de traduction
     */
    public void viderCache() {
        cacheTraduction.clear();
        logger.info("🗑️ Cache de traduction vidé");
    }

    // ==================== TRADUCTIONS STATIQUES (EXISTANT) ====================

    public String translateProjetStatut(String statut, Locale locale) {
        if (statut == null || statut.isEmpty()) {
            return statut;
        }
        try {
            return messageSource.getMessage("statut.projet." + statut, null, statut, locale);
        } catch (Exception e) {
            return statut;
        }
    }

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

    public String translateMois(String mois, Locale locale) {
        if (mois == null || mois.isEmpty()) {
            return mois;
        }
        try {
            String moisNormalise = mois.toUpperCase()
                    .replace("É", "E")
                    .replace("Û", "U");
            return messageSource.getMessage("mois." + moisNormalise, null, mois, locale);
        } catch (Exception e) {
            return mois;
        }
    }

    public String translateAbonnementDescription(Locale locale) {
        try {
            return messageSource.getMessage("abonnement.description.premium", null, locale);
        } catch (Exception e) {
            return "Abonnement Premium CollabPro - Mensuel";
        }
    }

    public String translatePeriode(String mois, String annee, Locale locale) {
        String moisTraduit = translateMois(mois, locale);
        return moisTraduit + " " + annee;
    }

    public String translate(String key, Locale locale) {
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            return key;
        }
    }

    public String translate(String key, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            return key;
        }
    }
}