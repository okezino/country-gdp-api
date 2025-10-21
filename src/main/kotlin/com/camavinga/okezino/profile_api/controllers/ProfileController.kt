package com.camavinga.okezino.profile_api.controllers

import com.camavinga.okezino.profile_api.data.model.AnalyzedString
import com.camavinga.okezino.profile_api.data.usecase.GetAnalyzedString
import com.camavinga.okezino.profile_api.data.usecase.GetFilterList
import com.camavinga.okezino.profile_api.model.CreateStringRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/strings")
class ProfileController {

    private val webClient = WebClient.create("https://catfact.ninja")
    private val storage = ConcurrentHashMap<String, AnalyzedString>()

    @PostMapping
    fun create(@RequestBody body: CreateStringRequest): ResponseEntity<Any> {

        if (body.value !is String ) {
            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(mapOf("422 Unprocessable Entity" to "Invalid data type for \"value\" (must be string)"))
        }
        if ( body.value.isBlank()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("400 Bad Request" to "Invalid request body or missing \"value\" field"))
        }
        if (storage.containsKey(body.value)) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapOf("409 Conflict" to "String already exists in the system"))
        }

        val entity = GetAnalyzedString.analyzeString(body.value)
        storage[body.value] = entity

        return ResponseEntity.ok(entity)
    }

    @GetMapping("/{string_value}")
    fun getOne(@PathVariable string_value: String): ResponseEntity<Any> {
        val a = storage.values.find { it.value.equals(string_value, ignoreCase = true) }
        return ResponseEntity.ok(a)
    }

    @GetMapping
    fun getMore(
        @RequestParam(required = false, name = "is_palindrome") isPalindrome: Boolean?,
        @RequestParam(required = false, name = "min_length") minLength: Int?,
        @RequestParam(required = false, name = "max_length") maxLength: Int?,
        @RequestParam(required = false, name = "word_count") wordCount: Int?,
        @RequestParam(required = false, name = "contains_character") containsCharacter: String?
    ): ResponseEntity<Any> {
        val a = GetFilterList.listWithFilters(isPalindrome = isPalindrome, minLength = minLength, maxLength = maxLength,
                wordCount = wordCount, containsCharacter = containsCharacter, storage = storage)
        return ResponseEntity.ok(a)
    }


    @DeleteMapping("/{string_value}")
    fun deleteOne(@PathVariable string_value: String): ResponseEntity<Any> {
        val a = storage.values.find { it.value.equals(string_value, ignoreCase = true) }
        if (!storage.containsKey(string_value)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("404 Not Found" to "String does not exist in the system"))
        }
        storage.remove(string_value)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT).body(null)
    }
}