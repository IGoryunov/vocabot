package co.kongostream.vocabularybot

import co.kongostream.vocabularybot.domain.VocabotService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication(
    exclude = [MongoAutoConfiguration::class, MongoReactiveAutoConfiguration::class]
)
class VocabularyBotApplication

fun main(args: Array<String>) {
    runApplication<VocabularyBotApplication>(*args)
}

@Component
class Runner(
    private val vocabotService: VocabotService,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        vocabotService.startPolling()
    }

}