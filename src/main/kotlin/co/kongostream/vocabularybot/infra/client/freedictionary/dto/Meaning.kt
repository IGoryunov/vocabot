package co.kongostream.vocabularybot.infra.client.freedictionary.dto

data class Meaning(
    val antonyms: List<String>?,
    val definitions: List<Definition>?,
    val partOfSpeech: String,
    val synonyms: List<String>
)