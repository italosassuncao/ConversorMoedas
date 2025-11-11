package com.example.conversormoedas.data.models

import androidx.room.Query
import kotlinx.serialization.Serializable
import retrofit2.http.GET

@Serializable
data class Quotation(
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val priceChange24h: Double,
    val imageUrl: String? = null // URL Opcional
)

// Histórico de dados
@Serializable
data class PriceHistory(
    val timestamp: Long,
    val price: Double
)

// Interface Retrofit
interface QuotationApiService {
    @GET("coins/markets")
    suspend fun getCryptoQuotations(
        @Query("vs_currency") currency: String = "brl",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): List<QuotationResponse>

    @GET("coins/{id}/market_chart")
    suspend fun getPriceHistory(
        @retrofit2.http.Path("id") id: String,
        @Query("vs_currency") currency: String = "brl",
        @Query("days") days: Int = 7
    ): HistoryResponse
}

@Serializable
data class QuotationResponse(
    val id: String,
    val name: String,
    val symbol: String,
    @Serializable(with = DoubleAsStringSerializer::class)
    val current_price: Double,
    val price_change_percentage_24h: Double,
    val image: String? = null
) {
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
data class HistoryResponse(
    val prices: List<List<Double>>
) {
    fun toDomain(): List<PriceHistory> {
        return prices.map {
            PriceHistory(it[0].toLong(), it[1])
        }
    }
}

// Serializer de exemplo caso o preço venha como String na API (não necessário para CoinGecko, mas útil para AlphaVantage)
// import kotlinx.serialization.KSerializer
// import kotlinx.serialization.descriptors.PrimitiveKind
// import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
// import kotlinx.serialization.descriptors.SerialDescriptor
// import kotlinx.serialization.encoding.Decoder
// import kotlinx.serialization.encoding.Encoder
// object DoubleAsStringSerializer : KSerializer<Double> { ... }