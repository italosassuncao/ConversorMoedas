package com.italo.conversormoedas.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Resposta raiz do AlphaVantage para cotações instantâneas (TIME_SERIES_INTRADAY, CURRENCY_EXCHANGE_RATE, etc.)
@Serializable
data class AlphaVantageQuoteResponse(
    // Global Quote é o nome da chave para ações instantâneas
    @SerialName("Global Quote")
    val globalQuote: GlobalQuote? = null,
    // Realtime Currency Exchange Rate é o nome da chave para Forex instantâneo
    @SerialName("Realtime Currency Exchange Rate")
    val exchangeRate: ExchangeRate? = null
)

// Modelo para ações
@Serializable
data class GlobalQuote(
    @SerialName("05. price")
    val price: String? = null,

    @SerialName("09. change")
    val change: String? = null,

    @SerialName("10. change percent")
    val changePercent: String? = null,
) {
    fun toQuotation(id: String, name: String, symbol: String): Quotation? {
        val currentPrice = price?.toDoubleOrNull() ?: return null
        val changePercentValue = changePercent?.replace("%", "")?.toDoubleOrNull() ?: 0.0

        return Quotation(
            id = id,
            name = name,
            symbol = symbol,
            currentPrice = currentPrice,
            priceChange24h = changePercentValue,
            imageUrl = null
        )
    }
}

// Modelo para moedas
@Serializable
data class ExchangeRate(
    @SerialName("5. Exchange Rate")
    val exchangeRate: String? = null,
) {
    fun toQuotation(id: String, fromSymbol: String, toSymbol: String): Quotation? {
        val currentPrice = exchangeRate?.toDoubleOrNull() ?: return null

        return Quotation(
            id = id,
            name = "$fromSymbol/$toSymbol",
            symbol = "$fromSymbol/$toSymbol",
            currentPrice = currentPrice,
            priceChange24h = 0.0,
            imageUrl = null
        )
    }
}