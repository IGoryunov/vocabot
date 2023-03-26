package co.kongostream.vocabularybot.infra.client.freedictionary.dto

data class Definition(
    val antonyms: List<String>?,
    val definition: String?,
    val example: String?,
    val synonyms: List<String>?
)