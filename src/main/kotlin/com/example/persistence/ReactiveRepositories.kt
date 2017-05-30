package com.example.persistence

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReactivePeopleRepository: ReactiveCrudRepository<Person, String>

@Repository
interface ReactiveAuditRepository: ReactiveCrudRepository<Audit, String> {
    fun findByEmail(email: String): reactor.core.publisher.Mono<Audit>
}

@Repository
interface ReactiveMessageRepository: ReactiveCrudRepository<Message, String> {
    fun countByMessageDateGreaterThanAndEmail(messageDate: java.time.LocalDateTime, email: String): reactor.core.publisher.Mono<Long>
}