package com.camavinga.okezino.profile_api.controllers

import com.camavinga.okezino.profile_api.data.usecase.GetAnalyzedString
import com.camavinga.okezino.profile_api.model.CatFactResponse
import com.camavinga.okezino.profile_api.model.CreateStringRequest
import com.camavinga.okezino.profile_api.model.Profile
import com.camavinga.okezino.profile_api.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration

@RestController
@RequestMapping("/strings")
class ProfileController {

    private val webClient = WebClient.create("https://catfact.ninja")
    val listOfString = mutableListOf<String>()
    private fun encode(v: String) = java.net.URLEncoder.encode(v, "UTF-8")
    private fun decode(v: String) = java.net.URLDecoder.decode(v, "UTF-8")


    @PostMapping
    fun create(@RequestBody body: CreateStringRequest): ResponseEntity<Any> {
        if (listOfString.contains(body.value)) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapOf("error" to "String already exists in the system"))
        }

        listOfString.add(body.value)

        val location = URI.create("/strings/${encode(body.value)}")
        return ResponseEntity.created(location).body(GetAnalyzedString.analyzeString(body.value))
    }

    @GetMapping
    fun getProfile(): Profile {
        val factResponse: CatFactResponse? = try {
            webClient.get()
                .uri("/fact")
                .retrieve()
                .bodyToMono(CatFactResponse::class.java)
                // timeout after 4 seconds
                .timeout(Duration.ofSeconds(14))
                // handle HTTP or network errors gracefully
                .onErrorResume { ex ->
                    when (ex) {
                        is WebClientResponseException -> {
                            println("HTTP error: ${ex.statusCode}")
                            Mono.empty()
                        }

                        else -> {
                            println("Error calling external API: ${ex.message}")
                            Mono.empty()
                        }
                    }
                }
                .block()
        } catch (ex: Exception) {
            println("Unexpected error: ${ex.message}")
            null
        }

        val factMessage = when {
            factResponse == null -> "There was an error fetching the cat fact or the request timed out."
            else -> factResponse.fact
        }

        return Profile(
            status = "success",
            user = User(
                email = "okezi003@gmail.com",
                name = "Joseph Okeh Simon",
                stack = "Kotlin/Spring"
            ),
            fact = factMessage
        )
    }


}