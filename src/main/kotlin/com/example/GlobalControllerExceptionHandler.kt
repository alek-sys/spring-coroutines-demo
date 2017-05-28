package com.example

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun return404() = ResponseEntity.notFound().build<String>()
}