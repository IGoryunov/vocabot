package co.kongostream.vocabularybot.infra.client.freedictionary

import co.kongostream.vocabularybot.infra.client.freedictionary.dto.Word
import co.kongostream.vocabularybot.domain.dictionary.DictionaryResource
import co.kongostream.vocabularybot.domain.dictionary.model.Definition
import co.kongostream.vocabularybot.domain.dictionary.model.PartOfSpeech
import co.kongostream.vocabularybot.domain.dictionary.model.Phonetic
import co.kongostream.vocabularybot.domain.dictionary.model.WordDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class FreeDictionaryResource : DictionaryResource {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val client = WebClient.builder()
        .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/en")
        .build()

    private suspend fun findWord(word: String): Word = client.get()
        .uri("/$word")
        .exchangeToMono { it.bodyToMono(Word::class.java) }
        .awaitFirstOrNull()
        ?: throw Exception("Word '$word' not found")

    override suspend fun findWordDefinition(word: String): List<WordDefinition> = withContext(ioScope.coroutineContext) {
        val wordItems = findWord(word)

        wordItems.map { item ->
            WordDefinition(
                word = item.word,
                phonetic = item.phonetics
                    ?.firstOrNull { it.text != null }
                    ?.let {
                        Phonetic(
                            text = it.text!!,
                            audio = it.audio
                        )
                    },
                definitions = item.meanings
                    .filter { it.definitions != null }
                    .flatMap { meaning ->
                        meaning.definitions!!
                            .filter { it.definition != null }
                            .map { meaning.partOfSpeech to it }
                            .toList()
                    }
                    .map { (partOfSpeech, meaning) ->
                        Definition(
                            partOfSpeech = partOfSpeech.recognizePartOfSpeech(),
                            meaning = meaning.definition!!,
                            example = meaning.example
                        )
                    }
            )
        }
    }

    companion object {
        private val speechMap = PartOfSpeech.values().associateBy { it.name.lowercase() }
        private fun String.recognizePartOfSpeech() = speechMap[this] ?: throw Exception("Unknown part of speech: $this")
    }


}