package co.kongostream.vocabularybot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(TelegramProperties::class)
class TelegramBotConfig

@ConfigurationProperties(prefix = "vocabot.telegram")
data class TelegramProperties @ConstructorBinding constructor(
    val token: String,
)
