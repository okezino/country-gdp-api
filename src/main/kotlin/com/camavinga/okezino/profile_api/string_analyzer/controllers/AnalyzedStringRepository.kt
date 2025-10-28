package com.camavinga.okezino.profile_api.string_analyzer.controllers

import com.camavinga.okezino.profile_api.string_analyzer.data.model.AnalyzedString
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class AnalyzedStringRepository {

    private val storage = ConcurrentHashMap<String, AnalyzedString>()

    fun save(entity: AnalyzedString): AnalyzedString {
        storage[entity.id] = entity
        return entity
    }

    fun findAll(): List<AnalyzedString> = storage.values.toList()

    fun findByValueIgnoreCase(value: String): AnalyzedString? =
        storage.values.find { it.value.equals(value, ignoreCase = true) }

    fun existsByValueIgnoreCase(value: String): Boolean =
        storage.values.any { it.value.equals(value, ignoreCase = true) }

    fun existsById(id: String): Boolean = storage.containsKey(id)

    fun delete(entity: AnalyzedString) {
        storage.remove(entity.id)
    }

    fun clear() {
        storage.clear()
    }
}
