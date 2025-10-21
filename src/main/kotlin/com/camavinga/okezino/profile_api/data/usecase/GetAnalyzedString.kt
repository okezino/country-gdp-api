package com.camavinga.okezino.profile_api.data.usecase

import com.camavinga.okezino.profile_api.data.model.AnalyzedString
import com.camavinga.okezino.profile_api.data.model.StringProperties
import java.security.MessageDigest

object GetAnalyzedString {

    fun analyzeString(input: String): AnalyzedString {
        val trimmed = input.trim()
        val hashValue = sha256(trimmed)

        val freqMap = trimmed.groupingBy { it }
            .eachCount()
            .filterKeys { !it.isWhitespace() }

        val properties = StringProperties(
            length = trimmed.length,
            is_palindrome = trimmed.equals(trimmed.reversed(), ignoreCase = true),
            unique_characters = freqMap.keys.size,
            word_count = trimmed.split("\\s+".toRegex()).size,
            sha256_hash = hashValue,
            character_frequency_map = freqMap
        )

        return AnalyzedString(
            id = hashValue,
            value = trimmed,
            properties = properties
        )
    }

    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}