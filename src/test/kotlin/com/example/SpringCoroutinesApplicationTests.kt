package com.example

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureWebTestClient
class SpringCoroutinesApplicationTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @TestConfiguration
    class ClockConfiguration {
        @Bean
        fun clock() = Clock.fixed(Instant.parse("2017-05-05T11:00:00.00Z"), ZoneId.of("Europe/London"))
    }

    @Test
    fun `blocking controller returns a message`() {
        val message = webTestClient
                .get().uri("/blocking/P1").exchange()
                .expectBody(String::class.java)
                .returnResult().responseBody

        assertThat(message).isEqualTo("Hello Alex, you have 1 messages since 2017-05-03T12:00")
    }

    @Test
    fun `blocking controller returns 404 for non-existing person`() {
        `assert 404 status for url`("/blocking/some-id")
    }

    @Test
    fun `reactive controller returns a message`() {
        val messagePublisher = webTestClient
                .get().uri("/reactive/P1").exchange()
                .returnResult(String::class.java)
                .responseBody

        StepVerifier.create(messagePublisher)
                .expectNext("Hello Alex, you have 1 messages since 2017-05-03T12:00")
                .expectComplete()
                .verify()
    }

    @Test
    fun `reactive controller returns 404 for non-existing person`() {
        `assert 404 status for url`("/reactive/some-id")
    }

    @Test
    fun `coroutine controller returns a message`() {
        val messagePublisher = webTestClient
                .get().uri("/coroutine/P1").exchange()
                .returnResult(String::class.java)
                .responseBody

        StepVerifier.create(messagePublisher)
                .expectNext("Hello Alex, you have 1 messages since 2017-05-03T12:00")
                .expectComplete()
                .verify()
    }

    @Test
    fun `coroutine controller returns 404 for non-existing person`() {
        `assert 404 status for url`("/coroutine/some-id")
    }

    private fun `assert 404 status for url`(url: String) {
        val status = webTestClient
                .get().uri(url).exchange()
                .expectBody(String::class.java)
                .returnResult().status

        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
