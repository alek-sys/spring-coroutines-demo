package com.example

import com.example.persistence.*
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.time.Clock
import java.time.LocalDateTime

@SpringBootApplication
class SpringCoroutinesApplication(
        val peopleRepository: PeopleRepository,
        val auditRepository: AuditRepository,
        val messageRepository: MessageRepository,
        val clock: Clock) : CommandLineRunner {

    override fun run(vararg args: String?) {
        peopleRepository.save(Person("P1", "Alex", "alex@example.com"))

        auditRepository.save(Audit(LocalDateTime.now(clock).minusDays(2), "alex@example.com"))

        messageRepository.save(Message("Hello", LocalDateTime.now(clock).minusDays(10), "alex@example.com"))
        messageRepository.save(Message("How are you", LocalDateTime.now(clock), "alex@example.com"))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SpringCoroutinesApplication::class.java, *args)
}
