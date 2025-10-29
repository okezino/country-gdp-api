package com.camavinga.okezino.profile_api.currency.controller

import com.camavinga.okezino.profile_api.currency.data.CountryOutput
import com.camavinga.okezino.profile_api.currency.util.SummaryImageGenerator
import org.springframework.stereotype.Service

@Service
class CountryService(
    private val countryRepository: CountryRepository
) {

    fun getCountryByName(name: String) = countryRepository.filterByName(name)
    fun deleteCountry(name: String) = countryRepository.delete(name)


    fun getCountriesFilteredAndSorted(sort: String?, region: String?, currency: String?) =
        countryRepository.filterAndSort(sort, region, currency)


    fun addAllCountries(countries: List<CountryOutput>) {
        countryRepository.saveAllCountries(countries)
        try {
            SummaryImageGenerator.generateSummaryImage(countries)
        } catch (e: Exception) {
            // don't fail the flow if image generation fails; log the error
            e.printStackTrace()
        }
    }

    fun getStatus() = countryRepository.getStatus()
}