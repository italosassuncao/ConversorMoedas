package com.italo.conversormoedas.data.models

import com.italo.conversormoedas.util.ApiConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

// --- Modelos de Dados ---
@Serializable
data class Quotation (
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val priceChange24h: Double,
    val imageUrl: String? = null
)

// Modelo de resposta para busca no CoinGecko

@Serializable
data class CoinGeckoSearchResponse(
    @SerialName("coins")
    val coins: List<CoinGeckoSearchCoin> = emptyList()
)

@Serializable
data class CoinGeckoSearchCoin(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("large")
    val imageUrl: String? = null
) {
    fun toQuotation(): Quotation {
        return Quotation(
            id = id,
            name = name,
            symbol = symbol,
            imageUrl = imageUrl,
            currentPrice = 0.0,
            priceChange24h = 0.0
        )
    }
}

// Modelo de resposta para busca no Alpha Vantage
@Serializable
data class AlphaVantageSearchResponse(
    @SerialName("bestMatches")
    val bestMatches: List<AlphaVantageSymbolMatch> = emptyList()
)

@Serializable
data class AlphaVantageSymbolMatch(
    @SerialName("1. symbol")
    val symbol: String,
    @SerialName("2. name")
    val name: String,
    @SerialName("3. type")
    val type: String,
    @SerialName("4. region")
    val region: String
) {
    fun toQuotation(): Quotation {
        return Quotation(
            id = symbol,
            name = name,
            symbol = symbol,
            imageUrl = null,
            currentPrice = 0.0,
            priceChange24h = 0.0
        )
    }
}

/**
 * Modelo de dados para o Histórico de preços (usado para gráficos).
 */
@Serializable
data class PriceHistory (
    val timestamp: Long,
    val price: Double
)

/**
 * Interface Retrofit para interagir com a API de Cotações.
 */
interface QuotationApiService {

    // --- CoinGecko Endpoints (Crypto) ---
    @GET("coins/markets")
    suspend fun getCryptoQuotations(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): List<QuotationResponse>

    @GET("search")
    suspend fun searchCrypto(
        @Query("query") query: String
    ): CoinGeckoSearchResponse

    @GET("coins/{id}/market_chart")
    suspend fun getPriceHistory(
        @retrofit2.http.Path("id") id: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: Int = 7
    ): HistoryResponse

    // --- AlphaVantage Endpoints (Ações e Forex) ---
    @GET("query?function=SYMBOL_SEARCH")
    suspend fun searchSymbols(
        @Query("keywords") keywords: String,
        @Query("apikey") apiKey: String = ApiConstants.ALPHA_VANTAGE_API_KEY
    ): AlphaVantageSearchResponse

    @GET("query?function=GLOBAL_QUOTE")
    suspend fun getStockQuote(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = ApiConstants.ALPHA_VANTAGE_API_KEY
    ): AlphaVantageQuoteResponse

    @GET("query?function=CURRENCY_EXCHANGE_RATE")
    suspend fun getForexQuote(
        @Query("from_symbol") fromSymbol: String,
        @Query("to_symbol") toSymbol: String,
        @Query("apikey") apiKey: String = ApiConstants.ALPHA_VANTAGE_API_KEY
    ): AlphaVantageQuoteResponse

}

// Modelos específicos para CoinGecko
@Serializable
data class QuotationResponse (
    val id: String,
    val symbol: String,
    val name: String,
    @Serializable(with = DoubleAsStringSerializer::class)
    val current_price: Double,
    val price_change_percentage_24h: Double,
    val image: String? = null
) {
    // Função auxiliar para mapear o modelo de domínio interno
    fun toDomain(): Quotation {
        return Quotation(
            id = id,
            name = name,
            symbol = symbol.uppercase(),
            currentPrice = current_price,
            priceChange24h = price_change_percentage_24h,
            imageUrl = image
        )
    }
}

@Serializable
data class HistoryResponse (
    // A API CoinGecko retorna preços como uma lista de [timestamp, price]
    val prices: List<List<Double>>
) {
    fun toDomain(): List<PriceHistory> {
        return prices.map {
            PriceHistory(
                timestamp = it[0].toLong(),
                price = it[1]
            )
        }
    }
}