package com.italo.conversormoedas.data.repository

import com.italo.conversormoedas.data.local.QuotationDao
import com.italo.conversormoedas.data.local.toFavoriteEntity
import com.italo.conversormoedas.data.local.toQuotation
import com.italo.conversormoedas.data.models.PriceHistory
import com.italo.conversormoedas.data.models.Quotation
import com.italo.conversormoedas.data.models.QuotationApiService
import com.italo.conversormoedas.domain.QuotationRepository
import com.italo.conversormoedas.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

// Implementação do QuotationRepository que lida com a lógica de buscar dados da API.
class QuotationRepositoryImpl(
    private val apiService: QuotationApiService,
    private val quotationDao: QuotationDao // Injeção correta do DAO
) : QuotationRepository {

    /**
     * Obtém cotações com lógica de busca inteligente para economizar memória e dados.
     */
    override fun getTrendingQuotations(query: String): Flow<Resource<List<Quotation>>> = flow {
        emit(Resource.Loading())

        try {
            if (query.isBlank()) {
                // Termo de busca vazio -> Carrega tendências (Top 20 do mercado)
                val response = apiService.getCryptoQuotations(perPage = 20)
                val trendingList = response.map { it.toDomain() }
                emit(Resource.Success(trendingList))
            } else {
                // Busca ativa -> Utiliza endpoints de pesquisa das APIs
                val searchResults = mutableListOf<Quotation>()

                // Busca Criptomoedas na CoinGecko
                val cryptoSearch = apiService.searchCrypto(query)
                val cryptoQuotations = cryptoSearch.coins.take(10).map { it.toQuotation() }
                searchResults.addAll(cryptoQuotations)

                // Busca Ações/Símbolos na AlphaVantage
                val stockSearch = apiService.searchSymbols(query)
                val stockQuotations = stockSearch.bestMatches.take(5).map { it.toQuotation() }
                searchResults.addAll(stockQuotations)

                emit(Resource.Success(searchResults))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Erro no servidor: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Sem conexão com a internet."))
        } catch (e: Exception) {
            emit(Resource.Error("Ocorreu um erro inesperado: ${e.localizedMessage}"))
        }
    }

    // PERSISTÊNCIA ROOM

    /**
     * Salva uma cotação no banco de dados local.
     */
    override suspend fun addQuotationToFavorites(quotation: Quotation) {
        quotationDao.insertFavorite(quotation.toFavoriteEntity())
    }

    /**
     * Remove uma cotação do banco de dados local.
     */
    override suspend fun removeQuotationFromFavorites(quotationId: String) {
        quotationDao.deleteFavorite(quotationId)
    }

    /**
     * Verifica em tempo real se um item é favorito.
     */
    override fun isQuotationFavorite(quotationId: String): Flow<Boolean> {
        return quotationDao.isFavorite(quotationId)
    }

    /**
     * Lista todos os favoritos persistidos no SQLite.
     */
    override fun getFavoriteQuotations(): Flow<List<Quotation>> {
        return quotationDao.getAllFavorites().map { entities ->
            entities.map { it.toQuotation() }
        }
    }

    // HISTÓRICO DE PREÇOS

    override suspend fun getPriceHistory(id: String): Resource<List<PriceHistory>> {
        return try {
            val response = apiService.getPriceHistory(id = id)
            Resource.Success(response.toDomain())
        } catch (e: Exception) {
            Resource.Error("Histórico indisponível no momento.")
        }
    }
}