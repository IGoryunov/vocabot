package co.kongostream.vocabularybot.domain.dictionary

import co.kongostream.vocabularybot.domain.dictionary.model.WordDefinition

interface DictionaryResource {

    suspend fun findWordDefinition(word: String): List<WordDefinition>

}


