package com.camavinga.okezino.profile_api.model

data class Profile(
    val status: String,
    val user: User,
    val timestamp: String,
    val fact: String
)

data class User(
    val email: String,
    val name: String,
    val stack: String

)
