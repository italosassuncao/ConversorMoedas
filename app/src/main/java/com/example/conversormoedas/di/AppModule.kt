package com.example.conversormoedas.di

import com.example.conversormoedas.data.repository.QuotationRepository
import com.example.conversormoedas.data.repository.QuotationRepositoryImpl
import com.example.conversormoedas.presentation.explore.ExploreViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import com.example.conversormoedas.data.models.QuotationApiService

// URL Base da CoinGecko
private const val BASE_URL_CRYPTO = "https://api.coingecko.com/api/v3/"

/**
 * Módulo Koin que define todas as dependências do aplicativo.
 */
val appModule = module {

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // OkHttpClient com Interceptor de Log
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    // JSON Serializer do Kotlinx
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    // Instância do Retrofit
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CRYPTO)
            .client(get())
            .addConverterFactory(get<Json>()
                .asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // Interface da API (implementação Retrofit)
    single {
        get<Retrofit>().create(QuotationApiService::class.java)
    }

    // Repositório de Cotações (Interface e Implementação)
    single<QuotationRepository> {
        QuotationRepositoryImpl(apiService = get())
    }

    // ViewModel para a tela de Exploração
    viewModel {
        ExploreViewModel(repository = get())
    }
}