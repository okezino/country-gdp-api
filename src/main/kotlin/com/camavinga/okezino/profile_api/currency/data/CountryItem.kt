package com.camavinga.okezino.profile_api.currency.data

data class CountryItem(
    val capital: String,
    val currencies: List<Currency>,
    val flag: String,
    val independent: Boolean,
    val name: String,
    val population: Int,
    val region: String
)