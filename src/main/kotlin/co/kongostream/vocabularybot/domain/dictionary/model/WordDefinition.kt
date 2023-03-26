package co.kongostream.vocabularybot.domain.dictionary.model

data class WordDefinition(
    val word: String,
    val phonetic: Phonetic?,
    val definitions: List<Definition>,
)