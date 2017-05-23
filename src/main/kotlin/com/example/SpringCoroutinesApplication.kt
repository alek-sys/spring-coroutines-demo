package com.example

import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.reactive.awaitFirst
import kotlinx.coroutines.experimental.reactive.awaitSingle
import kotlinx.coroutines.experimental.reactive.publish
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.LocalDateTime

data class Person(@Id val id: String, val name: String, val email: String)
data class Audit(val eventDate: LocalDateTime, val email: String)
data class Message(val text: String, val messageDate: LocalDateTime, val email: String)

@Repository
interface PeopleRepository: CrudRepository<Person, String>

@Repository
interface AuditRepository: CrudRepository<Audit, String> {
    fun findByEmail(email: String): Audit
}

@Repository
interface MessageRepository: CrudRepository<Message, String> {
    fun countByMessageDateGreaterThanAndEmail(messageDate: LocalDateTime, email: String): Long
}

@Repository
interface ReactivePeopleRepository: ReactiveCrudRepository<Person, String>

@Repository
interface ReactiveAuditRepository: ReactiveCrudRepository<Audit, String> {
    fun findByEmail(email: String): Mono<Audit>
}

@Repository
interface ReactiveMessageRepository: ReactiveCrudRepository<Message, String> {
    fun countByMessageDateGreaterThanAndEmail(messageDate: LocalDateTime, email: String): Mono<Long>
}

@RestController
class MyController(
        val peopleRepository: PeopleRepository,
        val auditRepository: AuditRepository,
        val messageRepository: MessageRepository) {

    @GetMapping("/blocking/{personId}")
    fun getMessagesFor(@PathVariable personId: String): String {
        val person = peopleRepository
            .findById(personId)
            .orElseThrow { IllegalArgumentException("Not found") }

        val lastLogin = auditRepository
            .findByEmail(person.email).eventDate

        val numberOfMessages = messageRepository
            .countByMessageDateGreaterThanAndEmail(lastLogin, person.email)

        return "Hello ${person.name}, you have $numberOfMessages messages since $lastLogin"
    }

}

@RestController
class MyReactiveController(
        val peopleRepository: ReactivePeopleRepository,
        val messageRepository: ReactiveMessageRepository,
        val auditRepository: ReactiveAuditRepository) {

    @GetMapping("/reactive/{personId}")
    fun getMessagesFor(@PathVariable personId: String): Mono<String>? {
        return peopleRepository.findById(personId)
            .flatMap { person ->
                auditRepository.findByEmail(person.email)
                    .flatMap { lastLogin ->
                        messageRepository.countByMessageDateGreaterThanAndEmail(lastLogin.eventDate, person.email)
                            .map { numberOfMessages ->
                                "Hello ${person.name}, you have $numberOfMessages messages since ${lastLogin.eventDate}"
                            }
                    }
            }
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun return404() = ResponseEntity.notFound().build<String>()
}

@RestController
class MyCoroutineController(
        val peopleRepository: ReactivePeopleRepository,
        val messageRepository: ReactiveMessageRepository,
        val auditRepository: ReactiveAuditRepository) {

    @GetMapping("/coroutine/{personId}")
    fun getMessages(@PathVariable personId: String) = publish(Unconfined) {
        val person = peopleRepository.findById(personId).awaitSingle()
        val lastLogin = auditRepository.findByEmail(person.email).awaitFirst().eventDate
        val numberOfMessages = messageRepository.countByMessageDateGreaterThanAndEmail(lastLogin, person.email).awaitFirst()

        val message = "Hello ${person.name}, you have $numberOfMessages messages since $lastLogin"

        send(message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun return404() = ResponseEntity.notFound().build<String>()
}

@SpringBootApplication
class SpringCoroutinesApplication(
        val peopleRepository: PeopleRepository,
        val auditRepository: AuditRepository,
        val messageRepository: MessageRepository) : CommandLineRunner {
    override fun run(vararg args: String?) {
        peopleRepository.save(Person("P1", "Alex", "alex@example.com"))

        auditRepository.save(Audit(LocalDateTime.now().minusDays(2), "alex@example.com"))

        messageRepository.save(Message("Hello", LocalDateTime.now().minusDays(10), "alex@example.com"))
        messageRepository.save(Message("How are you", LocalDateTime.now(), "alex@example.com"))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SpringCoroutinesApplication::class.java, *args)
}
