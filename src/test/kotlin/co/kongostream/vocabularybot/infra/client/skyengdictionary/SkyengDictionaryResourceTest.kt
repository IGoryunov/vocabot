package co.kongostream.vocabularybot.infra.client.skyengdictionary

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class SkyengDictionaryResourceTest {

    @Test
    @Disabled
    fun `hot request`(): Unit = runBlocking {
        val resource = SkyengDictionaryResource()

        val result = resource.findWordDefinition("hello")

        assertThat(result).isNotEmpty()
    }
}