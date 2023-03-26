package co.kongostream.vocabularybot.domain.dictionary.model

data class Definition(
    val partOfSpeech: PartOfSpeech,
    val meaning: String,
    val example: String?,
)