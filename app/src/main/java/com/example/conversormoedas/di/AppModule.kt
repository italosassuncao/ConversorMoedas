package com.example.conversormoedas.di

import androidx.room.Room
import com.example.conversormoedas.data.local.QuotationDB
import com.example.conversormoedas.data.local.QuotationDao
import com.example.conversormoedas.data.models.QuotationApiService
import com.example.conversormoedas.data.repository.QuotationRepository
import com.example.conversormoedas.data.repository.QuotationRepositoryImpl
import com.example.conversormoedas.presentation.detail.DetailViewModel
import com.example.conversormoedas.presentation.explore.ExploreViewModel
import com.example.conversormoedas.presentation.favorites.FavoritesViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Módulo Koin que define todas as dependências do aplicativo.
 */
val appModule = module {

    // --- SEÇÃO DE REDE (Networking) ---

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .client(get<OkHttpClient>()) // Tipo explícito adicionado
            .addConverterFactory(
                get<Json>().asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    single {
        get<Retrofit>().create(QuotationApiService::class.java)
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            QuotationDB::class.java,
            QuotationDB.DATABASE_NAME
        ).build()
    }

    // Singleton para o DAO
    single {
        get<QuotationDB>().quotationDao() // Tipo explícito adicionado
    }

    // Singleton para o Repositório
    single<QuotationRepository> {
        QuotationRepositoryImpl(
            apiService = get<QuotationApiService>(), // Tipo explícito adicionado
            quotationDao = get<QuotationDao>()       // Tipo explícito adicionado
        )
    }

    viewModel {
        ExploreViewModel(repository = get<QuotationRepository>())
    }

    viewModel {
        FavoritesViewModel(repository = get<QuotationRepository>())
    }

    viewModel { params ->
        DetailViewModel(
            repository = get<QuotationRepository>(),
            savedStateHandle = params.get()
        )
    }
}