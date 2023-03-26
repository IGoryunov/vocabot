package co.kongostream.vocabularybot.client.freedictionary

import co.kongostream.vocabularybot.infra.client.freedictionary.FreeDictionaryResource
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FreeDictionaryResourceTest {

    private lateinit var client: FreeDictionaryResource

    @BeforeEach
    fun setUp() {
        client = FreeDictionaryResource()
    }

    @Test
    fun `find word request`(): Unit = runBlocking {
        val word = client.findWordDefinition("continue")
        println(word)
    }
}