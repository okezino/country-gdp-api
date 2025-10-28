package com.camavinga.okezino.profile_api.currency.data

data class CountryOutput(
    val id: Int,
    val name: String?,
    val capital: String?,
    val region: String?,
    val population: Long,
    val currency_code: String?,
    val exchange_rate: Double?,
    val estimated_gdp: Double?,
    val flag_url: String?,
    val last_refreshed_at: String
)