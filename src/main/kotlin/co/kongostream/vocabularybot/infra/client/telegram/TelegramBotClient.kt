package co.kongostream.vocabularybot.infra.client.telegram

import co.kongostream.vocabularybot.config.TelegramProperties
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

abstract class TelegramBotClient(
    properties: TelegramProperties,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val bot: Bot = bot {
        token = properties.token
        dispatch {
            handleAnyMessage()
        }
    }

    private fun Dispatcher.handleAnyMessage() {
        message {
            message.text?.let { text ->
                logger.info("Received message(chatId='${message.chat.id}'): $text")
                ioScope.launch {
                    runCatching { processAnyMessage(text) }
                        .onSuccess { responseMessages -> responseMessages.forEach { text -> sendMessage(ChatId.fromId(message.chat.id), text) } }
                        .onFailure { exception -> logger.error("Can't process received message: \"$text\". Reason: ${exception.message}") }
                }
            }
        }
    }

    private fun sendMessage(chatId: ChatId, text: String) {
        bot.sendMessage(
            chatId = chatId,
            text = text,
            parseMode = ParseMode.MARKDOWN_V2
        ).fold(
            ifSuccess = { message -> logger.info("Message sent successfully to chat '${message.chat.id}'. Message: ${message.text}") },
            ifError = { error -> logger.error("Error sending tg message to chat '$chatId'. Reason: $error") }
        )
    }

    /**
     * @return List of response messages
     */
    abstract suspend fun processAnyMessage(incomeMessage: String): List<String>

    fun startPolling() {
        runCatching {
            logger.info("Bot starts polling...")
            bot.startPolling()
        }.onFailure { exception ->
            logger.error("Polling was finished with error:", exception)
        }
    }
}
