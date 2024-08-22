package com.sublime.lingo.presentation.ui

import android.content.Context
import com.sublime.lingo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// List of supported languages with their IDs
fun getSupportedLanguages(): List<Pair<String, String>> =
    listOf(
        "Afrikaans" to "af",
        "Amharic" to "am",
        "Arabic" to "ar",
        "Asturian" to "ast",
        "Azerbaijani" to "az",
        "Bashkir" to "ba",
        "Belarusian" to "be",
        "Bulgarian" to "bg",
        "Bengali" to "bn",
        "Breton" to "br",
        "Bosnian" to "bs",
        "Catalan, Valencian" to "ca",
        "Cebuano" to "ceb",
        "Czech" to "cs",
        "Welsh" to "cy",
        "Danish" to "da",
        "German" to "de",
        "Greek" to "el",
        "English" to "en",
        "Spanish" to "es",
        "Estonian" to "et",
        "Persian" to "fa",
        "Fulah" to "ff",
        "Finnish" to "fi",
        "French" to "fr",
        "Western Frisian" to "fy",
        "Irish" to "ga",
        "Gaelic; Scottish Gaelic" to "gd",
        "Galician" to "gl",
        "Gujarati" to "gu",
        "Hausa" to "ha",
        "Hebrew" to "he",
        "Hindi" to "hi",
        "Croatian" to "hr",
        "Haitian; Haitian Creole" to "ht",
        "Hungarian" to "hu",
        "Armenian" to "hy",
        "Indonesian" to "id",
        "Igbo" to "ig",
        "Iloko" to "ilo",
        "Icelandic" to "is",
        "Italian" to "it",
        "Japanese" to "ja",
        "Javanese" to "jv",
        "Georgian" to "ka",
        "Kazakh" to "kk",
        "Central Khmer" to "km",
        "Kannada" to "kn",
        "Korean" to "ko",
        "Luxembourgish; Letzeburgesch" to "lb",
        "Ganda" to "lg",
        "Lingala" to "ln",
        "Lao" to "lo",
        "Lithuanian" to "lt",
        "Latvian" to "lv",
        "Malagasy" to "mg",
        "Macedonian" to "mk",
        "Malayalam" to "ml",
        "Mongolian" to "mn",
        "Marathi" to "mr",
        "Malay" to "ms",
        "Burmese" to "my",
        "Nepali" to "ne",
        "Dutch; Flemish" to "nl",
        "Norwegian" to "no",
        "Northern Sotho" to "ns",
        "Occitan (post 1500)" to "oc",
        "Oriya" to "or",
        "Panjabi; Punjabi" to "pa",
        "Polish" to "pl",
        "Pushto; Pashto" to "ps",
        "Portuguese" to "pt",
        "Romanian; Moldavian; Moldovan" to "ro",
        "Russian" to "ru",
        "Sindhi" to "sd",
        "Sinhala; Sinhalese" to "si",
        "Slovak" to "sk",
        "Slovenian" to "sl",
        "Somali" to "so",
        "Albanian" to "sq",
        "Serbian" to "sr",
        "Swati" to "ss",
        "Sundanese" to "su",
        "Swedish" to "sv",
        "Swahili" to "sw",
        "Tamil" to "ta",
        "Thai" to "th",
        "Tagalog" to "tl",
        "Tswana" to "tn",
        "Turkish" to "tr",
        "Ukrainian" to "uk",
        "Urdu" to "ur",
        "Uzbek" to "uz",
        "Vietnamese" to "vi",
        "Wolof" to "wo",
        "Xhosa" to "xh",
        "Yiddish" to "yi",
        "Yoruba" to "yo",
        "Chinese" to "zh",
        "Zulu" to "zu",
    )

// Return the appropriate flag resource based on the language code
fun getFlagResource(
    context: Context,
    languageCode: String,
): Int {
    val resourceId = context.resources.getIdentifier(languageCode, "drawable", context.packageName)
    return if (resourceId != 0) resourceId else R.drawable.ic_launcher_foreground // Default placeholder
}

// Get the language name from the language code
fun getLanguageName(languageCode: String): String =
    getSupportedLanguages()
        .find {
            it.second == languageCode
        }?.first ?: "Unknown"

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
