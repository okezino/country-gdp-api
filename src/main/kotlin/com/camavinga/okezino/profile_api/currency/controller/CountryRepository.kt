package com.camavinga.okezino.profile_api.currency.controller

import com.camavinga.okezino.profile_api.currency.data.CountryOutput
import com.camavinga.okezino.profile_api.currency.data.Currency
import com.camavinga.okezino.profile_api.currency.data.StatusResponse
import org.springframework.stereotype.Repository
import javax.swing.plaf.synth.Region

@Repository
class CountryRepository {

    var listCountries = mutableListOf<CountryOutput>()

    fun saveAllCountries(countries: List<CountryOutput>) {
        listCountries = countries.toMutableList()
    }


    fun getStatus(): StatusResponse {

        return  StatusResponse(
            total_countries = listCountries.size,
            last_refreshed_at = listCountries[0].last_refreshed_at
        )
    }

    fun save(country: CountryOutput): CountryOutput {
        listCountries.add(country)
        return country
    }

    fun delete(country: String) {
        listCountries = listCountries.filter {
            !it.name.equals(country, ignoreCase = true)
        }.toMutableList()
    }

    fun filterByName(name: String): List<CountryOutput> {
        return listCountries.filter { it.name.equals(name, ignoreCase = true) }
    }

    fun filterAndSort(sort: String?, region: String?, currency: String?): List<CountryOutput> {
        var filteredList = listCountries.toList()

        if (!region.isNullOrBlank()) {
            filteredList = filteredList.filter { it.region?.equals(region, ignoreCase = true) == true }
        }
        if (!currency.isNullOrBlank()) {
            filteredList = filteredList.filter { it.currency_code?.equals(currency, ignoreCase = true) == true }
        }
        if (!sort.isNullOrBlank()) {
            filteredList = when (sort.lowercase()) {
                "gdp_asc" -> filteredList.sortedBy { it.estimated_gdp }
                "gdp_desc" -> filteredList.sortedByDescending { it.estimated_gdp }
                else -> filteredList
            }
        }

        return filteredList
    }

}