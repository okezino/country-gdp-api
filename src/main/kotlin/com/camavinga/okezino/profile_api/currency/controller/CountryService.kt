package com.camavinga.okezino.profile_api.currency.controller

import com.camavinga.okezino.profile_api.currency.data.CountryOutput
import org.springframework.stereotype.Service

@Service
class CountryService(
    private val countryRepository: CountryRepository
) {

    fun getCountryByName(name: String) = countryRepository.filterByName(name)

    fun getCountriesFilteredAndSorted(sort: String?, region: String?, currency: String?) =
        countryRepository.filterAndSort(sort, region, currency)

    fun addCountry(country: CountryOutput) = countryRepository.save(country)

    fun deleteCountry(name: String) = countryRepository.delete(name)

    fun addAllCountries(countries: List<CountryOutput>) = countryRepository.saveAllCountries(countries)

    fun getStatus() = countryRepository.getStatus()
}