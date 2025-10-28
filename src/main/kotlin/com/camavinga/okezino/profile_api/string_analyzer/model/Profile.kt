package com.camavinga.okezino.profile_api.string_analyzer.model

import java.time.LocalDateTime

data class Profile(
    val status: String,
    val user: User,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val fact: String
)

data class User(
    val email: String,
    val name: String,
    val stack: String
)


data class CatFactResponse(
    val fact: String,
    val length: Int
)

//POST
data class AnalyzedStringResponse(
    val id: String,
    val value: String,
    val properties: AnalyzedStringPropertiesResponse,
    val created_at: LocalDateTime
)
data class AnalyzedStringPropertiesResponse(
    val length: Int,
    val is_palindrome: Boolean,
    val unique_characters: Int,
    val word_count: Int,
    val sha256_hash: String,
    val character_frequency_map: Map<String, Int>
)

//GET
data class PagedAnalyzedStringsResponse(
    val data: List<AnalyzedStringResponse>,
    val count: Int,
    val filters_applied: Map<String, Any?>
)

//NLF
data class NLFilterInterpretation(
    val original: String,
    val parsed_filters: Map<String, Any>
)

data class NLFilterResponse(
    val data: List<AnalyzedStringResponse>,
    val count: Int,
    val interpreted_query: NLFilterInterpretation
)

data class CreateStringRequest(val value: Any)
