package com.example.conversormoedas.data.models

import androidx.compose.ui.graphics.Path
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotationApiService {

    @GET("api/v3/coins/markets")
    suspend fun getCryptoQuotations(
        @Query("vs_currency") currency: String,
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<QuotationApi> // Assuming the response is a list of QuotationApi objects

    @GET("api/v3/coins/{id}/market_chart")
    suspend fun getPriceHistory(
        @Path("id") id: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: String = "7"
    ): PriceHistoryApi
}