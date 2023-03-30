package co.kongostream.vocabularybot.domain

import co.kongostream.vocabularybot.infra.client.telegram.TelegramBotClient
import co.kongostream.vocabularybot.config.TelegramProperties
import co.kongostream.vocabularybot.domain.dictionary.DictionaryResource
import co.kongostream.vocabularybot.domain.dictionary.WordDefinitionMessageBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class VocabotService(
    @Qualifier("skyengDictionaryResource")
    private val dictionary: DictionaryResource,
    private val messageBuilder: WordDefinitionMessageBuilder,
    properties: TelegramProperties,
) : TelegramBotClient(properties) {

    override suspend fun processAnyMessage(incomeMessage: String): List<String> {
        val wordDefinitions = dictionary.findWordDefinition(incomeMessage)
        return wordDefinitions.map { messageBuilder.buildMessage(it) }
    }

}

