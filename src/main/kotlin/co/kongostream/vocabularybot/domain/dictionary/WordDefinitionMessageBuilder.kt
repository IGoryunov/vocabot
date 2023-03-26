package co.kongostream.vocabularybot.domain.dictionary

import co.kongostream.vocabularybot.domain.dictionary.model.WordDefinition

interface WordDefinitionMessageBuilder {

    fun buildMessage(wordDefinition: WordDefinition): String

}