package com.example.conversormoedas.data.models

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

    @GET("coins/markets")
    suspend fun getCotacoesCrypto(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): List<QuotationResponse>

    @GET("coins/{id}/market_chart")
    suspend fun getPriceHistory(
        @retrofit2.http.Path("id") id: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: Int = 30
    ): HistoryResponse

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
