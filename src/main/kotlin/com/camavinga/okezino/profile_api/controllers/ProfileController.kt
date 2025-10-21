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

    @GetMapping("/filter-by-natural-language")
    fun filterByNaturalLanguage(@RequestParam(name = "query") query: String?): ResponseEntity<Any> {
        if (query.isNullOrBlank()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("400 Bad Request" to "Missing or empty query parameter"))
        }

        val parsed = try {
            parseQuery(query)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("400 Bad Request" to "Unable to parse natural language query"))
        }

        if (parsed.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("400 Bad Request" to "Unable to parse natural language query"))
        }

        // Basic conflict detection: if min_length > max_length (not commonly parsed here) or invalid values
        val minLength = parsed["min_length"] as Int?
        val maxLength = parsed["max_length"] as Int?
        if (minLength != null && maxLength != null && minLength > maxLength) {
            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(mapOf("422 Unprocessable Entity" to "Query parsed but resulted in conflicting filters"))
        }

        // Build a storage map from repository contents so we can reuse GetFilterList

        val result = GetFilterList.listWithFilters(
            isPalindrome = parsed["is_palindrome"] as Boolean?,
            minLength = parsed["min_length"] as Int?,
            maxLength = parsed["max_length"] as Int?,
            wordCount = parsed["word_count"] as Int?,
            containsCharacter = parsed["contains_character"] as String?,
            storage = storage
        )

        val interpreted = mapOf(
            "original" to query,
            "parsed_filters" to parsed
        )

        val response = mapOf(
            "data" to result.data,
            "count" to result.count,
            "interpreted_query" to interpreted
        )

        return ResponseEntity.ok(response)
    }

    private fun parseQuery(q: String): MutableMap<String, Any> {
        val out = mutableMapOf<String, Any>()
        val s = q.lowercase()

        // is_palindrome
        if (s.contains("palindr")) {
            out["is_palindrome"] = true
        }

        // word_count: "single word" or "one word" or "1 word"
        if (s.contains("single word") || s.contains("one word") || Regex("\\b1\\s+word\\b").containsMatchIn(s)) {
            out["word_count"] = 1
        } else {
            // try to match explicit digit like "2 word" or "2 words"
            val wordCountMatch = Regex("\\b(\\d+)\\s+words?\\b").find(s)
            if (wordCountMatch != null) {
                out["word_count"] = wordCountMatch.groupValues[1].toInt()
            }
        }

        // min_length: "longer than X" -> min_length = X+1 ; "longer than or equal to X" or "at least X" -> min_length = X
        val longerThanMatch = Regex("longer than or equal to (\\d+)").find(s)
        if (longerThanMatch != null) {
            out["min_length"] = longerThanMatch.groupValues[1].toInt()
        } else {
            val longerThanMatch2 = Regex("longer than (\\d+)").find(s)
            if (longerThanMatch2 != null) {
                out["min_length"] = longerThanMatch2.groupValues[1].toInt() + 1
            } else {
                val atLeastMatch = Regex("at least (\\d+)").find(s)
                if (atLeastMatch != null) {
                    out["min_length"] = atLeastMatch.groupValues[1].toInt()
                } else {
                    // direct "strings longer than 10 characters" style
                    val longerThanChars = Regex("longer than (\\d+) characters").find(s)
                    if (longerThanChars != null) {
                        out["min_length"] = longerThanChars.groupValues[1].toInt() + 1
                    }
                }
            }
        }

        // contains_character: "containing the letter z", "contains the letter 'a'", "contain the first vowel"
        val containsLetterMatch = Regex("letter\\s+'?([a-zA-Z])'?").find(s)
        if (containsLetterMatch != null) {
            out["contains_character"] = containsLetterMatch.groupValues[1].lowercase()
        } else {
            val containsCharMatch = Regex("containing the letter ([a-zA-Z])").find(s)
            if (containsCharMatch != null) {
                out["contains_character"] = containsCharMatch.groupValues[1].lowercase()
            } else if (s.contains("first vowel")) {
                // heuristic: first vowel as 'a'
                out["contains_character"] = "a"
            } else {
                // "containing the letter z" general pattern
                val containsSimple = Regex("contains (?:the )?letter ([a-zA-Z])").find(s)
                if (containsSimple != null) {
                    out["contains_character"] = containsSimple.groupValues[1].lowercase()
                }
            }
        }

        return out
    }
}