package com.camavinga.okezino.profile_api.data.model

import java.time.Instant
import java.time.LocalDateTime

data class AnalyzedString(
    val id: String,
    val value: String,
    val properties: StringProperties,
    val created_at: String = Instant.now().toString()
)

data class StringProperties(
    val length: Int,
    val is_palindrome: Boolean,
    val unique_characters: Int,
    val word_count: Int,
    val sha256_hash: String,
    val character_frequency_map: Map<Char, Int>
)

//GET
data class PagedAnalyzedStringsResponse(
    val data: List<AnalyzedString>,
    val count: Int,
    val filters_applied: Map<String, Any?>
)