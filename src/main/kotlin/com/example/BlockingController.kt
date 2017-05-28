package com.example

import com.example.persistence.AuditRepository
import com.example.persistence.MessageRepository
import com.example.persistence.PeopleRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BlockingController(
        val peopleRepository: PeopleRepository,
        val auditRepository: AuditRepository,
        val messageRepository: MessageRepository) {

    @GetMapping("/blocking/{personId}")
    fun getMessagesFor(@PathVariable personId: String): String {
        val person = peopleRepository
            .findById(personId)
            .orElseThrow { NoSuchElementException("Not found") }

        val lastLogin = auditRepository
            .findByEmail(person.email).eventDate

        val numberOfMessages = messageRepository
            .countByMessageDateGreaterThanAndEmail(lastLogin, person.email)

        return "Hello ${person.name}, you have $numberOfMessages messages since $lastLogin"
    }

}