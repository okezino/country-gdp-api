package com.camavinga.okezino.profile_api.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpServerErrorException
import kotlin.to

open class ApiException(message: String) : RuntimeException(message)
class ResourceAlreadyExistsException(message: String): ApiException(message)
class ResourceNotFoundException(message: String): ApiException(message)
class BadRequestException(message: String): ApiException(message)
class UnprocessableEntityException(message: String): ApiException(message)


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException::class)
    fun handleConflict(ex: ResourceAlreadyExistsException): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to ex.message))

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to ex.message))

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to ex.message))

    @ExceptionHandler(HttpServerErrorException.InternalServerError::class)
    fun handleUnprocessable(ex: UnprocessableEntityException): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(mapOf("error" to ex.message))

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception): ResponseEntity<Any> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            mapOf(
                "error" to (ex.message ?: "unexpected error")
            )
        )
}

