package com.example.conversormoedas.data.repository

import com.example.conversormoedas.data.models.PriceHistory
import com.example.conversormoedas.data.models.Quotation
import com.example.conversormoedas.util.Resource
import kotlinx.coroutines.flow.Flow

interface QuotationRepository {

    /**
     * Obtém uma lista de cotações (Crypto/Moedas/Ações) e as emite via Flow.
     * O Flow pode emitir status de Carregando, Sucesso ou Erro.
     */
    fun getTrendingQuotations(
        query: String = "" // Para implementar a funcionalidade de busca
    ): Flow<Resource<List<Quotation>>>

    /**
     * Obtém o histórico de preço para uma cotação específica.
     */
    suspend fun getPriceHistory(id: String): Resource<List<PriceHistory>>

    // Operações futuras para Favoritos (Room)
    // suspend fun addToFavorites(quotation: Quotation)
    // fun getFavorites(): Flow<List<Quotation>>
}