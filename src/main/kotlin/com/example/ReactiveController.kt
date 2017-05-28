package com.example

import com.example.persistence.ReactiveAuditRepository
import com.example.persistence.ReactiveMessageRepository
import com.example.persistence.ReactivePeopleRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ReactiveController(
        val peopleRepository: ReactivePeopleRepository,
        val messageRepository: ReactiveMessageRepository,
        val auditRepository: ReactiveAuditRepository) {

    @GetMapping("/reactive/{personId}")
    fun getMessagesFor(@PathVariable personId: String): Mono<String> {
        return peopleRepository.findById(personId)
            .switchIfEmpty(Mono.error(NoSuchElementException()))
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
}