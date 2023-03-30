package co.kongostream.vocabularybot.domain.dictionary.model

data class Definition(
    val partOfSpeech: PartOfSpeech,
    val meaning: String,
    val examples: List<String>?,
    val translation: Translation? = null,
    val image: String? = null,
)