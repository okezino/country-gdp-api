package com.camavinga.okezino.profile_api.model

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
