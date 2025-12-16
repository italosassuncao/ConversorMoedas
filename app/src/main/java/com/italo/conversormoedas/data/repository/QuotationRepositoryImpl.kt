package com.italo.conversormoedas.data.repository

import com.italo.conversormoedas.data.models.PriceHistory
import com.italo.conversormoedas.data.models.Quotation
import com.italo.conversormoedas.data.models.QuotationApiService
import com.italo.conversormoedas.domain.QuotationRepository
import com.italo.conversormoedas.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

// Implementação do QuotationRepository que lida com a lógica de buscar dados da API.
class QuotationRepositoryImpl(
    private val apiService: QuotationApiService,
    quotationDao: Any
) : QuotationRepository {

    /**
     * Implementação para obter as cotações em destaque (trending).
     * Envia o estado da operação (Loading, Success, Error) através de um Flow.
     */
    override fun getTrendingQuotations(query: String): Flow<Resource<List<Quotation>>> = flow {
        emit(Resource.Loading())

        try {
            // Simulação: Chamada da API CoinGecko
            val response = apiService.getCryptoQuotations(currency = "usd")
            val domainList = response.map { it.toDomain() }

            val filteredList = if (query.isNotBlank()) {
                domainList.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.symbol.contains(query, ignoreCase = true)
                }
            } else {
                domainList
            }

            emit(Resource.Success(filteredList))

        } catch (e: HttpException) {
            // Erro HTTP
            emit(Resource.Error("Erro HTTP: ${e.message()}"))
        } catch (e: IOException) {
            // Erro de rede
            emit(Resource.Error("Erro de rede. Verifique sua conexão."))
        } catch (e: Exception) {
            // Outros erros
            emit(Resource.Error("Erro desconhecido: ${e.localizedMessage}"))
        }
    }

    override suspend fun getPriceHistory(id: String) : Resource<List<PriceHistory>> {
        return try {
            val response = apiService.getPriceHistory(id = id)
            Resource.Success(response.toDomain())
        } catch (e: HttpException) {
            Resource.Error("Erro ao buscar histórico: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Erro de rede ao buscar histórico.")
        } catch (e: Exception) {
            Resource.Error("Erro desconhecido ao buscar histórico.")
        }
    }

    override suspend fun addQuotationToFavorites(quotation: Quotation) {
        TODO("Not yet implemented")
    }

    override suspend fun removeQuotationFromFavorites(quotationId: String) {
        TODO("Not yet implemented")
    }

    override fun isQuotationFavorite(quotationId: String): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getFavoriteQuotations(): Flow<List<Quotation>> {
        TODO("Not yet implemented")
    }

}