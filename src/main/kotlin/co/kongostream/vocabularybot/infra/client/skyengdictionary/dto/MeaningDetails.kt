package co.kongostream.vocabularybot.infra.client.skyengdictionary.dto

data class MeaningDetails(
    val id: String,
    val text: String,
    val transcription: String,
    val partOfSpeechCode: String,
    val translation: Translation,
    val definition: Definition,
    val images: List<Image>,
    val examples: List<Example>,
    val difficultyLevel: Int,
    val meaningsWithSimilarTranslation: List<MeaningsWithSimilarTranslation>,
)