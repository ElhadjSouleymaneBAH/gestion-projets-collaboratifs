package be.iccbxl.gestionprojets.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Générateur PDF pour les factures avec support multilingue.
 */
@Service
public class PdfGenerator {

    // ========== Traductions FR/EN ==========
    private static final Map<String, Map<String, String>> TRANSLATIONS = new HashMap<>();

    static {
        // Français
        Map<String, String> fr = new HashMap<>();
        fr.put("invoice", "FACTURE");
        fr.put("issueDate", "Date d'émission : ");
        fr.put("dueDate", "Date d'échéance : ");
        fr.put("period", "Période : ");
        fr.put("billedTo", "Facturé à :");
        fr.put("description", "Description");
        fr.put("amount", "Montant");
        fr.put("vat", "TVA (21 %)");
        fr.put("totalIncVat", "Total TTC");
        fr.put("paymentInfo", "Informations de paiement :");
        fr.put("iban", "IBAN : BE99 9999 9999 9999");
        fr.put("bic", "BIC : GEBABEBB");
        fr.put("legalMention", "En effectuant le règlement de cette facture, vous confirmez automatiquement votre accord avec les conditions générales de vente.");
        fr.put("generatedOn", "Générée le ");
        fr.put("defaultDescription", "Abonnement Premium Mensuel – Plateforme CollabPro");
        fr.put("exclVat", "(HT)");

        // Anglais
        Map<String, String> en = new HashMap<>();
        en.put("invoice", "INVOICE");
        en.put("issueDate", "Issue date: ");
        en.put("dueDate", "Due date: ");
        en.put("period", "Period: ");
        en.put("billedTo", "Billed to:");
        en.put("description", "Description");
        en.put("amount", "Amount");
        en.put("vat", "VAT (21%)");
        en.put("totalIncVat", "Total incl. VAT");
        en.put("paymentInfo", "Payment information:");
        en.put("iban", "IBAN: BE99 9999 9999 9999");
        en.put("bic", "BIC: GEBABEBB");
        en.put("legalMention", "By making payment of this invoice, you automatically confirm your agreement with the general terms of sale.");
        en.put("generatedOn", "Generated on ");
        en.put("defaultDescription", "Monthly Premium Subscription – CollabPro Platform");
        en.put("exclVat", "(excl. VAT)");

        TRANSLATIONS.put("fr", fr);
        TRANSLATIONS.put("en", en);
    }

    // ========== Public API : génération avec langue ==========
    public byte[] generate(Map<String, Object> data, String langue) {
        String lang = normalizeLang(langue);                 // fr/en (fallback fr)
        Map<String, String> t = TRANSLATIONS.get(lang);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 36, 36, 48, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // Polices
            Font boldFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
            Font titleFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normalFont   = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font smallFont    = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font clientLabel  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font boldCell     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            // Logo (via classpath)
            addLogoIfPresent(doc, "static/logo-collabpro.png");

            // En-tête Entreprise
            String entrepriseNom     = getString(data, "entrepriseNom", "CollabPro Solutions");
            String entrepriseAdresse = getString(data, "entrepriseAdresse", "");
            String entrepriseVille   = getString(data, "entrepriseVille", "");
            String entrepriseEmail   = getString(data, "entrepriseEmail", "");
            String entrepriseTva     = getString(data, "entrepriseTva", "");

            doc.add(new Paragraph(entrepriseNom, boldFont));
            if (!entrepriseAdresse.isEmpty()) doc.add(new Paragraph(entrepriseAdresse, normalFont));
            if (!entrepriseVille.isEmpty())   doc.add(new Paragraph(entrepriseVille, normalFont));
            if (!entrepriseTva.isEmpty())     doc.add(new Paragraph("TVA: " + entrepriseTva, normalFont));
            if (!entrepriseEmail.isEmpty())   doc.add(new Paragraph("Email: " + entrepriseEmail, normalFont));
            doc.add(Chunk.NEWLINE);

            // Séparateur
            doc.add(new Paragraph("_________________________________________________________"));
            doc.add(Chunk.NEWLINE);

            // Titre & numéro
            String numero = getString(data, "numeroFacture", "N/A");
            doc.add(new Paragraph(t.get("invoice"), titleFont));
            doc.add(new Paragraph(numero, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            doc.add(Chunk.NEWLINE);

            // Dates & période (déjà formatées dans data)
            doc.add(new Paragraph(t.get("issueDate") + getString(data, "dateEmission", "—"), normalFont));
            doc.add(new Paragraph(t.get("dueDate")   + getString(data, "dateEcheance", "—"), normalFont));
            doc.add(new Paragraph(t.get("period")    + getString(data, "periode", "—"), normalFont));
            doc.add(Chunk.NEWLINE);

            // Client
            doc.add(new Paragraph(t.get("billedTo"), clientLabel));
            String clientNom     = getString(data, "clientNom", "Client");
            String clientEmail   = getString(data, "clientEmail", "");
            String clientAdresse = getString(data, "clientAdresse", "");
            doc.add(new Paragraph(clientNom, normalFont));
            if (!clientAdresse.isEmpty()) doc.add(new Paragraph(clientAdresse, normalFont));
            if (!clientEmail.isEmpty())   doc.add(new Paragraph(clientEmail, normalFont));
            doc.add(Chunk.NEWLINE);

            // Montants
            double ht  = asDouble(data.get("montantHT"));
            double tva = asDouble(data.get("tva"));
            double ttc = asDouble(data.get("montantTTC"));

            // Description (avec traduction par défaut)
            String desc = getString(data, "description", t.get("defaultDescription"));

            // Tableau
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            table.addCell(t.get("description"));
            table.addCell(t.get("amount"));
            table.addCell(desc + " " + t.get("exclVat"));
            table.addCell(formatAmount(ht, lang));
            table.addCell(t.get("vat"));
            table.addCell(formatAmount(tva, lang));

            Phrase totalLabel = new Phrase(t.get("totalIncVat"), boldCell);
            Phrase totalValue = new Phrase(formatAmount(ttc, lang), boldCell);
            table.addCell(totalLabel);
            table.addCell(totalValue);

            doc.add(table);
            doc.add(Chunk.NEWLINE);

            // Informations de paiement
            Font paymentFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            doc.add(new Paragraph(t.get("paymentInfo"), paymentFont));
            doc.add(new Paragraph(t.get("iban"), normalFont));
            doc.add(new Paragraph(t.get("bic"), normalFont));
            doc.add(Chunk.NEWLINE);

            // Mentions légales
            doc.add(new Paragraph(t.get("legalMention"), smallFont));
            doc.add(Chunk.NEWLINE);

            // Date de génération
            String dateGeneration = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            doc.add(new Paragraph(t.get("generatedOn") + dateGeneration, smallFont));

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF", e);
        }
    }

    public byte[] generate(Map<String, Object> data) {
        return generate(data, "fr");
    }

    // ========== Helpers ==========

    private static String normalizeLang(String langue) {
        if (langue == null) return "fr";
        String l = langue.trim().toLowerCase(Locale.ROOT);
        return (l.startsWith("en")) ? "en" : "fr";
    }

    private static String getString(Map<String, Object> data, String key, String defaultVal) {
        Object v = (data == null) ? null : data.get(key);
        if (v == null) return defaultVal;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? defaultVal : s;
    }

    private static double asDouble(Object v) {
        if (v == null) return 0d;
        if (v instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(v.toString().replace(",", "."));
        } catch (Exception ignored) {
            return 0d;
        }
    }

    private static String formatAmount(double value, String lang) {
        Locale locale = "fr".equals(lang) ? Locale.FRANCE : Locale.UK;
        NumberFormat cf = NumberFormat.getCurrencyInstance(locale);
        cf.setCurrency(java.util.Currency.getInstance("EUR"));
        return cf.format(value);
    }

    private static void addLogoIfPresent(Document doc, String classpathResource) {
        try (InputStream is = PdfGenerator.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                Image logo = Image.getInstance(bytes);
                logo.scaleToFit(150, 150);
                doc.add(logo);
                doc.add(Chunk.NEWLINE);
            } else {
                System.err.println("Logo non trouvé dans le classpath : " + classpathResource);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement logo : " + e.getMessage());
        }
    }
}
