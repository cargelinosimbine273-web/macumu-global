package com.example.util

object CurrencyUtils {
    // Current typical exchange rates relative to Metical Moçambicano (MZN)
    const val RATE_MZN = 1.0
    const val RATE_ZAR = 0.285 // ~ 3.5 MZN per ZAR
    const val RATE_USD = 0.0156 // ~ 64 MZN per USD
    const val RATE_EUR = 0.0147 // ~ 68 MZN per EUR

    fun formatPrice(amountMzn: Double, currency: String): String {
        return when (currency) {
            "ZAR" -> {
                val converted = amountMzn * RATE_ZAR
                "R %.2f".format(converted)
            }
            "USD" -> {
                val converted = amountMzn * RATE_USD
                "$ %.2f".format(converted)
            }
            "EUR" -> {
                val converted = amountMzn * RATE_EUR
                "€ %.2f".format(converted)
            }
            else -> {
                "%,.0f MZN".format(amountMzn).replace(',', ' ')
            }
        }
    }
}
