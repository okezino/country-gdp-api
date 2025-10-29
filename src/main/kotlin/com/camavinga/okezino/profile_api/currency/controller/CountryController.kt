package com.camavinga.okezino.profile_api.currency.controller

import com.camavinga.okezino.profile_api.currency.data.CodeResult
import com.camavinga.okezino.profile_api.currency.data.CountryItem
import com.camavinga.okezino.profile_api.currency.data.CountryOutput
import com.camavinga.okezino.profile_api.currency.data.Rates
import org.springframework.http.MediaType
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
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import kotlin.random.Random

@RestController
@RequestMapping("/countries")
class CountryController(
    private val service: CountryService
) {

    private val countriesClient = WebClient.create("https://restcountries.com")
    private val ratesClient = WebClient.create("https://open.er-api.com")


    fun getRateByCode(rates: Rates, code: String?): Double? {
        return try {
            val property = Rates::class.members
                .firstOrNull { it.name.equals(code, ignoreCase = true) }
            property?.call(rates) as? Double
        } catch (e: Exception) {
            null
        }
    }


    fun getData() {

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
                    val currencyCode = country.currencies?.firstOrNull()?.code
                    val exchangedRate = getRateByCode(exchangeRates, currencyCode)

                    val randomFactor = Random.nextDouble(1000.0, 2000.0)
                    val estimatedGdp: Double = exchangedRate?.let {
                        country.population * randomFactor / it
                    } ?: 0.0
                    CountryOutput(
                        id = index + 1,
                        name = country.name,
                        capital = country.capital,
                        region = country.region,
                        population = country.population.toLong(),
                        currency_code = currencyCode,
                        exchange_rate = getRateByCode(exchangeRates, currencyCode),
                        estimated_gdp = estimatedGdp,
                        flag_url = country.flag,
                        last_refreshed_at = Instant.now().toString()
                    )


                }
            }.subscribe { countryOutputs ->
                // Here you can save countryOutputs to your database
//                countryOutputs.forEach { println(it) }

                service.addAllCountries(countryOutputs)

            }
    }


    @PostMapping("/refresh")
    fun refreshCountries(): ResponseEntity<Any> {
        getData()
        return ResponseEntity.ok("Country refresh successfully")
    }

    @GetMapping()
    fun getCountries(
        @RequestParam(required = false, value = "region") region: String?,
        @RequestParam(required = false, value = "currency") currency: String?,
        @RequestParam(required = false, value = "sort") sort: String?
    ): ResponseEntity<Any> {
        return service.getCountriesFilteredAndSorted(
            sort = sort,
            region = region,
            currency = currency
        )
    }

    @GetMapping("/{string_value}")
    fun getCountry(
        @PathVariable string_value: String
    ): ResponseEntity<Any> {
        return service.getCountryByName(name = string_value)

    }

    @GetMapping("/image")
    fun getCountryImage(): ResponseEntity<Any> {
        val imagePath = Paths.get("cache", "summary.png")
        if (!Files.exists(imagePath)) {
            return ResponseEntity.status(404).body(mapOf("error" to "Summary image not found"))
        }

        val bytes = Files.readAllBytes(imagePath)
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(bytes)

    }

    @DeleteMapping("/{string_value}")
    fun deleteCountries(@PathVariable string_value: String): ResponseEntity<Any> {

        return service.deleteCountry(name = string_value)
    }
}