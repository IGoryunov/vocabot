package co.kongostream.vocabularybot.config

import com.mongodb.ReadPreference
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableConfigurationProperties(
    MongoProperties::class
)
@EnableReactiveMongoRepositories
class MongoConfig(
    private val mongoProps: MongoProperties,
) {

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create(mongoProps.uri)
    }

    @Bean
    fun reactiveMongoTemplate(mongoClient: MongoClient): ReactiveMongoTemplate {
        val reactiveMongoTemplate = ReactiveMongoTemplate(mongoClient, mongoProps.dbname)
        reactiveMongoTemplate.setReadPreference(ReadPreference.secondaryPreferred())

        prepareTemplate(reactiveMongoTemplate)

        return reactiveMongoTemplate
    }

}


@ConfigurationProperties(prefix = "vocabot.mongodb")
data class MongoProperties @ConstructorBinding constructor(
    val dbname: String,
    val host: String,
    val port: Int,
    val user: String,
    val pass: String,
    val uri: String,
)

fun prepareTemplate(reactiveMongoTemplate: ReactiveMongoTemplate) {
    val converter = reactiveMongoTemplate.converter as MappingMongoConverter
    converter.setTypeMapper(DefaultMongoTypeMapper(null))
}

