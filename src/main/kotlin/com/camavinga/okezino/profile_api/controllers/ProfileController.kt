package com.camavinga.okezino.profile_api.controllers

import com.camavinga.okezino.profile_api.model.Profile
import com.camavinga.okezino.profile_api.model.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/me")
class ProfileController {


    @GetMapping
    fun getProfile() : Profile {
        return Profile(
            status = "Success",
            user = User(
                email = "okezi003@gmail.com",
                name = "Joseph Okeh Simon",
                stack = "Kotlin"
            ),
            fact = "pending"
        )
    }
}