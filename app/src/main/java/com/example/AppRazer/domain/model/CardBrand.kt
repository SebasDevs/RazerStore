package com.example.AppRazer.domain.model

enum class CardBrand(val label: String) {
    VISA("VISA"),
    MASTERCARD("MASTERCARD"),
    AMEX("AMEX"),
    UNKNOWN("")
}

fun detectCardBrand(number: String): CardBrand {
    return when {
        number.startsWith("4") -> CardBrand.VISA
        number.length >= 2 && number.substring(0, 2).toIntOrNull()
            ?.let { it in 51..55 } == true -> CardBrand.MASTERCARD

        number.length >= 4 && number.substring(0, 4).toIntOrNull()
            ?.let { it in 2221..2720 } == true -> CardBrand.MASTERCARD

        number.startsWith("34") || number.startsWith("37") -> CardBrand.AMEX
        else -> CardBrand.UNKNOWN
    }
}