package com.example.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PeopleRepository: CrudRepository<Person, String>

@Repository
interface AuditRepository: CrudRepository<Audit, String> {
    fun findByEmail(email: String): Audit
}

@Repository
interface MessageRepository: CrudRepository<Message, String> {
    fun countByMessageDateGreaterThanAndEmail(messageDate: java.time.LocalDateTime, email: String): Long
}