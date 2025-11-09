import translate from "translate";

translate.engine = "google";
translate.key = "";

export async function translateText(text, targetLang) {
  try {
    if (!text) return "";
    if (targetLang === "fr") return text;
    return await translate(text, { to: targetLang });
  } catch (error) {
    console.error("Erreur traduction :", error);
    return text;
  }
}
