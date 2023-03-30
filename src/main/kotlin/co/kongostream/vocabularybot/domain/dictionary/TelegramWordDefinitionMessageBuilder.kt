package co.kongostream.vocabularybot.domain.dictionary

import co.kongostream.vocabularybot.domain.dictionary.model.PartOfSpeech
import co.kongostream.vocabularybot.domain.dictionary.model.PartOfSpeech.*
import co.kongostream.vocabularybot.domain.dictionary.model.WordDefinition
import org.springframework.stereotype.Component

@Component
class TelegramWordDefinitionMessageBuilder : WordDefinitionMessageBuilder {

    override fun buildMessage(wordDefinition: WordDefinition): String = buildString {
        appendLine("\uD83D\uDD0E *${wordDefinition.word.replaceFirstChar { it.titlecase() }.escapeSpecialChars()}*")
        wordDefinition.phonetic?.let { appendLine("\uD83D\uDC44 `/${it.text.escapeSpecialChars()}/`") }

        wordDefinition.definitions.groupBy { it.partOfSpeech }.forEach { (partOfSpeech, definitions) ->
            appendLine()
            appendLine(partOfSpeech.format())
            definitions.forEachIndexed { index, def ->
                append("\uD83D\uDCD6 ")
                appendLine(def.meaning.escapeSpecialChars())
                def.translation?.text?.let { appendLine("\uD83C\uDDF7\uD83C\uDDFA ||${it.escapeSpecialChars()}||") }
                if (def.translation?.note?.isNotBlank() == true) {
                    appendLine("\uD83D\uDCC3 ||${def.translation.note.escapeSpecialChars()}||")
                }
                def.examples?.map {
                    appendLine("\uD83D\uDCAC _\"${it.escapeSpecialChars(ignore = setOf('['.code, ']'.code)).boldPhrase()}\"_")
                    if (index != definitions.size - 1) {
                        appendLine()
                    }
                }
                def.image?.let { url -> appendLine("[image](${url.escapeSpecialChars()})") }
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
        PHRASE       -> "\uD83D\uDDE3️ *PHRASE*"
    }

    private val regex = Regex("""\[(.*?)]""")
    private fun String.boldPhrase(): String = replace(regex) { "*${it.groups[1]?.value ?: ""}*" }

    private val specialChars = setOf('_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!', '\\').map { it.code }

    private fun String.escapeSpecialChars(ignore: Set<Int> = emptySet()): String {
        return buildString {
            this@escapeSpecialChars.chars().forEach {
                if (it in specialChars && it !in ignore) {
                    append('\\')
                }
                append(it.toChar())
            }
        }
    }

}