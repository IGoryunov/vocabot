package co.kongostream.vocabularybot.domain.dictionary

import co.kongostream.vocabularybot.domain.dictionary.model.PartOfSpeech
import co.kongostream.vocabularybot.domain.dictionary.model.PartOfSpeech.*
import co.kongostream.vocabularybot.domain.dictionary.model.WordDefinition
import org.springframework.stereotype.Component

@Component
class TelegramWordDefinitionMessageBuilder : WordDefinitionMessageBuilder {

    override fun buildMessage(wordDefinition: WordDefinition): String = buildString {
        append("*${wordDefinition.word.replaceFirstChar { it.titlecase() }}*")
        wordDefinition.phonetic?.let { append(" ${it.text}".escapeSpecialChars()) }
        appendLine()

        wordDefinition.definitions.groupBy { it.partOfSpeech }.forEach { (partOfSpeech, definitions) ->
            appendLine()
            appendLine(partOfSpeech.format())
            definitions.forEachIndexed { index, def ->
                append("\uD83D\uDCD6 ")
                appendLine(def.meaning.escapeSpecialChars())
                def.example?.let {
                    appendLine("\uD83D\uDCAC _\"${it.escapeSpecialChars().boldWord(wordDefinition.word)}\"_")
                    if (index != definitions.size - 1) {
                        appendLine()
                    }
                }
            }
        }
    }

    private fun PartOfSpeech.format(): String = when (this) {
        NOUN         -> "\uD83C\uDF4F *NOUN*"
        VERB         -> "\uD83C\uDFC3 *VERB*"
        ADVERB       -> "\uD83C\uDFC3 *ADVERB*"
        ADJECTIVE    -> "\uD83C\uDFA8 *ADJECTIVE*"
        PREPOSITION  -> "\uD83D\uDC47 *PREPOSITION*"
        INTERJECTION -> "\uD83D\uDDE3 *INTERJECTION*"
        PRONOUN      -> "\uD83D\uDC68\u200D\uD83C\uDFA4 *PRONOUN*"
    }

    private fun String.boldWord(word: String): String =
        replace(Regex("""\b$word\w*""", RegexOption.IGNORE_CASE)) { "*${it.groups.first()?.value ?: ""}*" }

    private val specialChars = setOf('_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!', '\\').map { it.code }

    private fun String.escapeSpecialChars(): String {
        return buildString {
            this@escapeSpecialChars.chars().forEach {
                if (it in specialChars) {
                    append('\\')
                }
                append(it.toChar())
            }
        }
    }

}