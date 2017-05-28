package com.example.persistence

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class Person(@Id val id: String, val name: String, val email: String)
data class Audit(val eventDate: LocalDateTime, val email: String)
data class Message(val text: String, val messageDate: LocalDateTime, val email: String)

