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
    private val quotationDao: QuotationDao
) : QuotationRepository {

    // Lista de ativos fixos
    private val fixedAssets = listOf(
        Pair("AAPL", "Apple Inc."),      // Ação
        Pair("MSFT", "Microsoft Corp."),  // Ação
        Pair("EUR", "USD"),              // Forex EUR/USD
        Pair("USD", "BRL")               // Forex USD/BRL
    )

    // --- MÉTODOS DE DADOS REMOTOS (API) ---

    override fun getTrendingQuotations(query: String): Flow<Resource<List<Quotation>>> = flow {
        emit(Resource.Loading())

        try {
            // Busca Crypto (CoinGecko)
            val cryptoResponse = apiService.getCryptoQuotations(currency = "usd")
            val cryptoList = cryptoResponse.map { it.toDomain() }

            // Busca Ações/Forex (AlphaVantage)
            val alphaList = mutableListOf<Quotation>()
            for (asset in fixedAssets) {
                val quote = if (asset.second.contains('/')) {
                    getAlphaForexQuote(asset.first, asset.second)
                } else {
                    getAlphaStockQuote(asset.first, asset.second)
                }
                if (quote is Resource.Success && quote.data != null) {
                    alphaList.add(quote.data)
                }
            }

            // Mescla as listas
            val mergedList = (cryptoList + alphaList).shuffled()

            // Aplica filtro de busca
            val filteredList = if (query.isNotBlank()) {
                mergedList.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.symbol.contains(query, ignoreCase = true)
                }
            } else {
                mergedList
            }

            emit(Resource.Success(filteredList))

        } catch (e: HttpException) {
            emit(Resource.Error("Erro HTTP ao buscar dados de mercado: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Erro de rede. Verifique sua conexão."))
        } catch (e: Exception) {
            emit(Resource.Error("Ocorreu um erro desconhecido: ${e.localizedMessage}"))
        }
    }

    /**
     * Função interna para buscar cotação de Ações.
     */
    private suspend fun getAlphaStockQuote(symbol: String, name: String): Resource<Quotation> {
        return try {
            val response = apiService.getStockQuote(symbol)
            val quotation = response.globalQuote?.toQuotation(symbol, name, symbol)
            if (quotation != null) {
                Resource.Success(quotation)
            } else {
                Resource.Error("Dados de ações incompletos para $symbol")
            }
        } catch (e: Exception) {
            Resource.Error("Falha ao buscar cotação de ações $symbol")
        }
    }

    /**
     * Função interna para buscar cotação de Moedas (Forex).
     */
    private suspend fun getAlphaForexQuote(fromSymbol: String, toSymbol: String): Resource<Quotation> {
        return try {
            val response = apiService.getForexQuote(fromSymbol, toSymbol)
            val id = "$fromSymbol/$toSymbol"
            val quotation = response.exchangeRate?.toQuotation(id, fromSymbol, toSymbol)
            if (quotation != null) {
                Resource.Success(quotation)
            } else {
                Resource.Error("Dados de Forex incompletos para $id")
            }
        } catch (e: Exception) {
            Resource.Error("Falha ao buscar cotação Forex $fromSymbol/$toSymbol")
        }
    }

    override suspend fun getPriceHistory(id: String): Resource<List<PriceHistory>> {

        return try {
            if (!id.contains('/')) {
                val response = apiService.getPriceHistory(id = id)
                Resource.Success(response.toDomain())
            } else {
                Resource.Error("Histórico de Ações/Forex ainda não implementado.")
            }
        } catch (e: Exception) {
            Resource.Error("Ocorreu um erro desconhecido ao buscar histórico: ${e.localizedMessage}")
        }
    }

    // --- MÉTODOS DE DADOS LOCAIS (ROOM) ---

    override suspend fun addQuotationToFavorites(quotation: Quotation) {
        quotationDao.insertFavorite(quotation.toFavoriteEntity())
    }

    override suspend fun removeQuotationFromFavorites(quotationId: String) {
        quotationDao.deleteFavorite(quotationId)
    }

    override fun isQuotationFavorite(quotationId: String): Flow<Boolean> {
        return quotationDao.isFavorite(quotationId)
    }

    override fun getFavoriteQuotations(): Flow<List<Quotation>> {
        return quotationDao.getAllFavorites().map { entities ->
            entities.map { it.toQuotation() }
        }
    }

}