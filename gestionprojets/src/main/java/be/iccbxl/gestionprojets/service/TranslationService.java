package be.iccbxl.gestionprojets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Service centralisé de traduction pour toutes les données de l'application.
 * Traduit les statuts, rôles, types, etc., ainsi que les contenus dynamiques (titres, descriptions)
 * grâce à l’API Google Translate gratuite.
 */
@Service
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    @Autowired
    private MessageSource messageSource;

    // Cache local pour éviter des appels répétés à Google Translate
    private final Map<String, String> cacheTraduction = new HashMap<>();

    // ==================== TRADUCTION AUTOMATIQUE (API GOOGLE TRANSLATE) ====================

    /**
     * Traduit automatiquement un texte libre (titre, description, etc.)
     *
     * @param texte  Texte source
     * @param locale Langue cible
     * @return Texte traduit ou original si erreur / français
     */
    public String traduireTexteAutomatique(String texte, Locale locale) {
        if (texte == null || texte.trim().isEmpty()) {
            return texte;
        }

        String langueCible = locale.getLanguage();

        // Si la langue demandée est le français, inutile de traduire
        if ("fr".equalsIgnoreCase(langueCible)) {
            return texte;
        }

        // Vérification du cache
        String cacheKey = langueCible + ":" + texte;
        if (cacheTraduction.containsKey(cacheKey)) {
            return cacheTraduction.get(cacheKey);
        }

        try {
            // Construction de l’URL d’appel à l’API Google Translate
            String apiUrl = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=fr&tl="
                    + langueCible + "&dt=t&q=" + URLEncoder.encode(texte, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();

            // ✅ Extraction plus robuste du texte traduit
            String traduit = body.split("\\[\\[\\[\"")[1].split("\",")[0]
                    .replace("\\n", " ")
                    .replace("\\\"", "\"");

            // Mise en cache
            cacheTraduction.put(cacheKey, traduit);

            logger.debug("🌍 Traduction réussie FR → {} : {} → {}",
                    langueCible.toUpperCase(), texte, traduit);

            // Petite pause pour éviter un trop grand nombre de requêtes en rafale
            Thread.sleep(150);

            return traduit;
        } catch (Exception e) {
            logger.warn("⚠️ Échec de traduction automatique pour '{}': {}", texte, e.getMessage());
            return texte;
        }
    }

    /**
     * Vide le cache des traductions (utile pour tester)
     */
    public void viderCache() {
        cacheTraduction.clear();
        logger.info("🗑️ Cache de traduction vidé");
    }

    // ==================== TRADUCTIONS STATIQUES (MESSAGES.PROPERTIES) ====================

    public String translateProjetStatut(String statut, Locale locale) {
        return traduireDepuisMessages("statut.projet." + statut, statut, locale);
    }

    public String translateTacheStatut(String statut, Locale locale) {
        return traduireDepuisMessages("statut.tache." + statut, statut, locale);
    }

    public String translatePriorite(String priorite, Locale locale) {
        return traduireDepuisMessages("priorite." + priorite, priorite, locale);
    }

    public String translateRole(String role, Locale locale) {
        return traduireDepuisMessages("role." + role, role, locale);
    }

    public String translateFactureStatut(String statut, Locale locale) {
        return traduireDepuisMessages("statut.facture." + statut, statut, locale);
    }

    public String translateAbonnementType(String type, Locale locale) {
        return traduireDepuisMessages("abonnement.type." + type, type, locale);
    }

    public String translateAbonnementStatut(String statut, Locale locale) {
        return traduireDepuisMessages("abonnement.statut." + statut, statut, locale);
    }

    public String translateNotificationType(String type, Locale locale) {
        return traduireDepuisMessages("notification.type." + type, type, locale);
    }

    public String translateVisibilite(String visibilite, Locale locale) {
        return traduireDepuisMessages("visibilite." + visibilite, visibilite, locale);
    }

    public String translateMois(String mois, Locale locale) {
        if (mois == null || mois.isEmpty()) return mois;
        String moisNormalise = mois.toUpperCase().replace("É", "E").replace("Û", "U");
        return traduireDepuisMessages("mois." + moisNormalise, mois, locale);
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
        return traduireDepuisMessages(key, key, locale);
    }

    public String translate(String key, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            return key;
        }
    }

    // ==================== MÉTHODE PRIVÉE COMMUNE ====================

    private String traduireDepuisMessages(String key, String valeurParDefaut, Locale locale) {
        if (key == null || key.isEmpty()) {
            return valeurParDefaut;
        }
        try {
            return messageSource.getMessage(key, null, valeurParDefaut, locale);
        } catch (Exception e) {
            return valeurParDefaut;
        }
    }
}
