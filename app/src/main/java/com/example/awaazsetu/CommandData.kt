package com.example.awaazsetu

import java.util.Locale

data class Command(
    val intent: String,
    val category: String,
    val en: String,
    val hi: String,
    val actionType: ActionType = ActionType.NONE,
    val actionData: String = "",
    val actionLabel: String = ""
)

enum class ActionType {
    NONE,
    DIAL,
    OPEN_URL
}

data class QuickPrompt(
    val query: String,
    val labelEn: String,
    val labelHi: String,
    val iconResName: String = "ic_mic"
)

object CommandRepository {

    val quickPrompts = listOf(
        QuickPrompt("pm kisan", "PM Kisan", "पीएम किसान"),
        QuickPrompt("pm awas", "PM Housing", "पीएम आवास"),
        QuickPrompt("police", "Police", "पुलिस"),
        QuickPrompt("ambulance", "Ambulance", "एम्बुलेंस"),
        QuickPrompt("fever", "Fever / Health", "बुखार / सेहत"),
        QuickPrompt("ration card", "Ration Card", "राशन कार्ड"),
        QuickPrompt("aadhaar", "Aadhaar Card", "आधार कार्ड"),
        QuickPrompt("women help", "Women Help", "महिला हेल्पलाइन"),
        QuickPrompt("child help", "Child Help", "चाइल्ड हेल्पलाइन"),
        QuickPrompt("cyber crime", "Cyber Crime", "साइबर अपराध")
    )

    val commands = listOf(
        Command("greet_hello", "General", "Namaste! I am Sarthi, your digital assistant.", "नमस्ते! मैं सारथी हूँ, आपका डिजिटल सहायक।"),
        Command("greet_how_are_you", "General", "I am functioning at 100% efficiency!", "मैं 100% दक्षता पर काम कर रहा हूँ!"),
        Command("bot_identity", "General", "I am Sarthi, developed by Yuvraj.", "मैं सारथी हूँ, जिसे युवराज द्वारा विकसित किया गया है।"),
        Command("agri_pm_kisan", "Agriculture", "PM Kisan gives Rs 6000/year to farmers.", "पीएम किसान सम्मान निधि किसानों को 6000 रुपये प्रति वर्ष देती है।", ActionType.OPEN_URL, "pmkisan.gov.in", "PM Kisan Portal"),
        Command("health_fever", "Health", "For fever: Rest and Paracetamol.", "बुखार के लिए: आराम और पेरासिटामोल।"),
        Command("emg_police", "Emergency", "Police: 100 or 112.", "पुलिस: 100 या 112।", ActionType.DIAL, "112", "Police"),
        Command("emg_ambulance", "Emergency", "Ambulance: 108 or 102.", "एम्बुलेंस: 108 या 102।", ActionType.DIAL, "108", "Ambulance"),
        Command("doc_aadhaar", "Documents", "Aadhaar is your 12-digit ID.", "आधार आपकी 12-अंकीय आईडी है।", ActionType.OPEN_URL, "myaadhaar.uidai.gov.in", "MyAadhaar Portal"),
        Command("pm_awas", "Housing", "PMAY provides affordable housing.", "प्रधानमंत्री आवास योजना किफायती आवास प्रदान करती है।", ActionType.OPEN_URL, "pmaymis.gov.in", "PMAY MIS"),
        Command("wcd_women_helpline", "Women/Child", "Women Helpline: 181.", "महिला हेल्पलाइन: 181।", ActionType.DIAL, "181", "Women Helpline"),
        Command("wcd_childline", "Women/Child", "Childline: 1098.", "चाइल्डलाइन: 1098।", ActionType.DIAL, "1098", "Childline"),
        Command("emg_cyber", "Emergency", "Cyber Crime: 1930.", "साइबर अपराध: 1930।", ActionType.DIAL, "1930", "Cyber Crime"),
        Command("doc_ration_card", "Documents", "Ration Card provides subsidized food grains. Apply via your state food portal.", "राशन कार्ड से सस्ता अनाज मिलता है। राज्य पोर्टल से आवेदन करें।")
        // ... (You can keep the rest of your original Command list here)
    )

    // Expanded Keyword Map with Devanagari Support
    val keywords = mapOf(
        // General & Greetings
        "hello" to "greet_hello", "hi" to "greet_hello", "namaste" to "greet_hello",
        "नमस्ते" to "greet_hello", "हेलो" to "greet_hello", "प्रणाम" to "greet_hello",

        "how are you" to "greet_how_are_you", "kaise ho" to "greet_how_are_you",
        "कैसे हो" to "greet_how_are_you", "क्या हाल है" to "greet_how_are_you",

        "who are you" to "bot_identity", "author" to "bot_identity", "yuvraj" to "bot_identity",
        "तुम कौन हो" to "bot_identity", "कौन हो" to "bot_identity", "युवराज" to "bot_identity",

        // Agriculture
        "pm kisan" to "agri_pm_kisan", "पीएम किसान" to "agri_pm_kisan", "किसान" to "agri_pm_kisan",

        // Health
        "fever" to "health_fever", "bukhar" to "health_fever", "बुखार" to "health_fever",

        // Emergency
        "police" to "emg_police", "100" to "emg_police", "112" to "emg_police", "पुलिस" to "emg_police",
        "ambulance" to "emg_ambulance", "108" to "emg_ambulance", "एम्बुलेंस" to "emg_ambulance",
        "cyber crime" to "emg_cyber", "fraud" to "emg_cyber", "साइबर" to "emg_cyber",
        "women help" to "wcd_women_helpline", "woman" to "wcd_women_helpline", "महिला" to "wcd_women_helpline",
        "child help" to "wcd_childline", "child" to "wcd_childline", "बच्चे" to "wcd_childline",

        // Documents
        "aadhaar" to "doc_aadhaar", "आधार" to "doc_aadhaar",
        "ration card" to "doc_ration_card", "राशन कार्ड" to "doc_ration_card",

        // Housing
        "pm awas" to "pm_awas", "awas" to "pm_awas", "आवास" to "pm_awas", "घर" to "pm_awas"
    )

    /**
     * Finds the best command based on user query.
     */
    fun findCommand(query: String): Command? {
        // Step 1: Normalize query - remove Hindi Purna Viram (।) and special chars
        val normalizedQuery = query.lowercase(Locale.getDefault())
            .replace("।", "")
            .replace("?", "")
            .replace(".", "")
            .trim()

        var bestMatchIntent: String? = null
        var maxKeywordLength = 0

        // Step 2: Loop through keywords to find the longest match (Devanagari or English)
        for ((keyword, intent) in keywords) {
            if (normalizedQuery.contains(keyword)) {
                if (keyword.length > maxKeywordLength) {
                    maxKeywordLength = keyword.length
                    bestMatchIntent = intent
                }
            }
        }

        return if (bestMatchIntent != null) {
            commands.find { it.intent == bestMatchIntent }
        } else {
            null
        }
    }
}
