package com.camavinga.okezino.profile_api.data.usecase

import com.camavinga.okezino.profile_api.data.model.AnalyzedString
import com.camavinga.okezino.profile_api.data.model.PagedAnalyzedStringsResponse
import java.util.concurrent.ConcurrentHashMap

object GetFilterList {
    fun listWithFilters(
        isPalindrome: Boolean?,
        minLength: Int?,
        maxLength: Int?,
        wordCount: Int?,
        containsCharacter: String?,
        storage: ConcurrentHashMap<String, AnalyzedString>
    ): PagedAnalyzedStringsResponse {
        val all =  storage.values.filter { analyzed ->
            val props = analyzed.properties

            val matchesPalindrome = isPalindrome?.let { props.is_palindrome == it } ?: true
            val matchesMinLength = minLength?.let { props.length >= it } ?: true
            val matchesMaxLength = maxLength?.let { props.length <= it } ?: true
            val matchesWordCount = wordCount?.let { props.word_count == it } ?: true
            val matchesContainsCharacter = containsCharacter?.let { ch ->
                analyzed.value.contains(ch, ignoreCase = true)
            } ?: true

            matchesPalindrome &&
                    matchesMinLength &&
                    matchesMaxLength &&
                    matchesWordCount &&
                    matchesContainsCharacter
        }

        val filtersApplied = buildMap<String, Any> {
            isPalindrome?.let { put("is_palindrome", it) }
            minLength?.let { put("min_length", it) }
            maxLength?.let { put("max_length", it) }
            wordCount?.let { put("word_count", it) }
            containsCharacter?.let { put("contains_character", it) }
        }

        return PagedAnalyzedStringsResponse(
            data = all,
            count = all.size,
            filters_applied = filtersApplied
        )
    }

}