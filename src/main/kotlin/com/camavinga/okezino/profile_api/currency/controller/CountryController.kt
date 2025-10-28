package com.camavinga.okezino.profile_api.currency.controller

import CountryOutput
import com.camavinga.okezino.profile_api.currency.data.CodeResult
import com.camavinga.okezino.profile_api.currency.data.CountryItem
import com.camavinga.okezino.profile_api.currency.data.Rates
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Instant
import kotlin.random.Random

@RestController
@RequestMapping("/countries")
class CountryController {

    private val countriesClient = WebClient.create("https://restcountries.com")
    private val ratesClient = WebClient.create("https://open.er-api.com")

    fun getFuck() {

        val countriesMono = countriesClient.get()
            .uri("/v2/all?fields=name,capital,region,population,flag,currencies")
            .retrieve()
            .bodyToFlux(CountryItem::class.java)
            .collectList()

        val ratesMono = ratesClient.get()
            .uri("/v6/latest/USD")
            .retrieve()
            .bodyToMono(CodeResult::class.java)

         Mono.zip(countriesMono, ratesMono)
            .map { tuple ->
                val countries = tuple.t1
                val exchangeRates = tuple.t2.rates

                countries.mapIndexed { index, country ->
                    val currencyCode = country.currencies.firstOrNull()?.code ?: "USD"

                    // reflectively get the rate value from Rates class
                    val exchangeRate = try {
                        val rateField = Rates::class.java.getDeclaredField(currencyCode)
                        rateField.getDouble(exchangeRates)
                    } catch (e: Exception) {
                        1.0
                    }

                    val randomFactor = Random.nextDouble(1000.0, 2000.0)
                    val estimatedGdp = country.population * randomFactor / exchangeRate

                    CountryOutput(
                        id = index + 1,
                        name = country.name,
                        capital = country.capital,
                        region = country.region,
                        population = country.population.toLong(),
                        currency_code = currencyCode,
                        exchange_rate = String.format("%.2f", exchangeRate).toDouble(),
                        estimated_gdp = String.format("%.2f", estimatedGdp).toDouble(),
                        flag_url = country.flag,
                        last_refreshed_at = Instant.now().toString()
                    )
                }
            }.subscribe { countryOutputs ->
                // Here you can save countryOutputs to your database
                countryOutputs.forEach { println(it) }
            }
    }

    init {
        getFuck()
    }


    @PostMapping("/refresh")
    fun refreshCountries(): ResponseEntity<Any> {
        return ResponseEntity.ok().build()

    }

    @GetMapping()
    fun getCountries(
        @RequestParam(required = false, value = "region") region: String?,
        @RequestParam(required = false, value = "currency") currency: String?,
        @RequestParam(required = false, value = "sort") name: String?
    ): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @GetMapping("/:{string_value}")
    fun getCountry(
        @PathVariable string_value: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok().build()

    }

    @GetMapping("/image")
    fun getCountryImage(
        @PathVariable string_value: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok().build()

    }

    @DeleteMapping(":{string_value}")
    fun deleteCountries(@PathVariable string_value: String): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }
}