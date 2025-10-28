package com.camavinga.okezino.profile_api.currency.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/status")
class StatusController(private val service: CountryService) {

    @GetMapping
    fun getStatus(): ResponseEntity<Any> {
        return ResponseEntity.ok(service.getStatus())

    }
}