package co.kongostream.vocabularybot.infra.client.freedictionary.dto

data class WordItem(
    val word: String,
    val meanings: List<Meaning>,
    val phonetic: String?,
    val phonetics: List<Phonetic>?,
    val sourceUrls: List<String>?,
)