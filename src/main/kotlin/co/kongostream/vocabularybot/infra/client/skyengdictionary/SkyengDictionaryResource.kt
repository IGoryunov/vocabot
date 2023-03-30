package co.kongostream.vocabularybot.infra.client.skyengdictionary

import co.kongostream.vocabularybot.domain.dictionary.DictionaryResource
import co.kongostream.vocabularybot.domain.dictionary.model.Definition
import co.kongostream.vocabularybot.domain.dictionary.model.PartOfSpeech
import co.kongostream.vocabularybot.domain.dictionary.model.PartOfSpeech.*
import co.kongostream.vocabularybot.domain.dictionary.model.Phonetic
import co.kongostream.vocabularybot.domain.dictionary.model.Translation
import co.kongostream.vocabularybot.domain.dictionary.model.WordDefinition
import co.kongostream.vocabularybot.infra.client.skyengdictionary.dto.MeaningDetails
import co.kongostream.vocabularybot.infra.client.skyengdictionary.dto.MeaningDetailsList
import co.kongostream.vocabularybot.infra.client.skyengdictionary.dto.SearchMeaningResults
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class SkyengDictionaryResource : DictionaryResource {

    private val client = WebClient.builder()
        .baseUrl("https://dictionary.skyeng.ru/api/public/v1")
        .build()

    private suspend fun searchMeanings(phrase: String): SearchMeaningResults = client.get()
        .uri {
            it.path("/words/search")
                .queryParam("search", phrase)
                .queryParam("page", "0")
                .queryParam("pageSize", "1")
                .build()
        }
        .retrieve()
        .awaitBody()

    private suspend fun getMeaningDetails(ids: List<Long>): MeaningDetailsList = client.get()
        .uri {
            it.path("/meanings")
                .queryParam("ids", ids.joinToString())
                .queryParam("page", "0")
                .build()
        }
        .retrieve()
        .awaitBody()


    private suspend fun takeFirstMostPopularMeanings(phrase: String): List<MeaningDetails> {
        val meanings = searchMeanings(phrase)
        val firstMeaningId = meanings.firstOrNull()?.meanings?.firstOrNull()?.id ?: return emptyList()
        val firstMeaningDetails = getMeaningDetails(listOf(firstMeaningId)).firstOrNull() ?: return emptyList()
        val meaningIds = firstMeaningDetails.meaningsWithSimilarTranslation
            .sortedByDescending { it.frequencyPercent.toFloat() }
            .map { it.meaningId }
            .take(MAX_MEANINGS - 1)

        val meaningDetails = getMeaningDetails(meaningIds)

        if (meaningDetails.firstOrNull { it.id != firstMeaningDetails.id } != null) {
          return meaningDetails + firstMeaningDetails
        }

        return meaningDetails
    }

    override suspend fun findWordDefinition(word: String): List<WordDefinition> {
        val meanings = takeFirstMostPopularMeanings(word)

        return meanings.map { meaning ->
            WordDefinition(
                word = meaning.text,
                phonetic = Phonetic(meaning.transcription, null),
                definitions = listOf(
                    Definition(
                        partOfSpeech = meaning.partOfSpeechCode.toPartOfSpeech(),
                        meaning = meaning.definition.text,
                        examples = meaning.examples.map { it.text },
                        translation = meaning.translation.run {
                            Translation(
                                text = text,
                                note = note
                            )
                        },
                        image = meaning.images.firstOrNull()?.url?.replaceBefore("https://", "")
                    )
                )
            )
        }
    }

    companion object {
        const val MAX_MEANINGS: Int = 5

        private val PARTS_OF_SPEECH = mapOf(
            "n" to NOUN,
            "prn" to PRONOUN,
            "prp" to PREPOSITION,
            "r" to ADVERB,
            "j" to ADJECTIVE,
            "exc" to INTERJECTION,
            "ph" to PHRASE
        )

        private fun String.toPartOfSpeech(): PartOfSpeech = PARTS_OF_SPEECH[this] ?: throw Exception("Unknown part of speech: $this")

    }
}