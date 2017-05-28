package com.example.persistence

@org.springframework.stereotype.Repository
interface PeopleRepository: org.springframework.data.repository.CrudRepository<Person, String>

@org.springframework.stereotype.Repository
interface AuditRepository: org.springframework.data.repository.CrudRepository<Audit, String> {
    fun findByEmail(email: String): Audit
}

@org.springframework.stereotype.Repository
interface MessageRepository: org.springframework.data.repository.CrudRepository<Message, String> {
    fun countByMessageDateGreaterThanAndEmail(messageDate: java.time.LocalDateTime, email: String): Long
}

@org.springframework.stereotype.Repository
interface ReactivePeopleRepository: org.springframework.data.repository.reactive.ReactiveCrudRepository<Person, String>

@org.springframework.stereotype.Repository
interface ReactiveAuditRepository: org.springframework.data.repository.reactive.ReactiveCrudRepository<Audit, String> {
    fun findByEmail(email: String): reactor.core.publisher.Mono<Audit>
}

@org.springframework.stereotype.Repository
interface ReactiveMessageRepository: org.springframework.data.repository.reactive.ReactiveCrudRepository<Message, String> {
    fun countByMessageDateGreaterThanAndEmail(messageDate: java.time.LocalDateTime, email: String): reactor.core.publisher.Mono<Long>
}
