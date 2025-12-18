package com.italo.conversormoedas.data.local

import com.italo.conversormoedas.data.models.AlphaVantageSymbolMatch
import com.italo.conversormoedas.data.models.CoinGeckoSearchCoin
import com.italo.conversormoedas.data.models.HistoryResponse
import com.italo.conversormoedas.data.models.PriceHistory
import com.italo.conversormoedas.data.models.Quotation
import com.italo.conversormoedas.data.models.QuotationResponse

/**
 * Funções de extensão para mapear modelos de dados (DTOs e Entidades)
 * para modelos de domínio e vice-versa.
 */

// --- MAPEAMENTO DE API (COINGECKO) ---

/**
 * Converte a resposta padrão de mercado da CoinGecko para o modelo de domínio.
 */
fun QuotationResponse.toDomain(): Quotation {
    return Quotation(
        id = id,
        name = name,
        symbol = symbol.uppercase(),
        imageUrl = image,
        currentPrice = current_price,
        priceChange24h = price_change_percentage_24h
    )
}

/**
 * Converte o resultado de busca da CoinGecko para o modelo de domínio.
 */
fun CoinGeckoSearchCoin.toQuotation(): Quotation {
    return Quotation(
        id = id,
        name = name,
        symbol = symbol.uppercase(),
        imageUrl = imageUrl,
        currentPrice = 0.0, // Preço será atualizado na tela de detalhes
        priceChange24h = 0.0
    )
}

/**
 * Converte a resposta de histórico da CoinGecko para uma lista de PriceHistory.
 */
fun HistoryResponse.toDomain(): List<PriceHistory> {
    return prices.map { dataPoint ->
        PriceHistory(
            timestamp = dataPoint[0].toLong(),
            price = dataPoint[1]
        )
    }
}

// --- MAPEAMENTO DE API (ALPHA VANTAGE) ---

/**
 * Converte o resultado de busca de símbolos da Alpha Vantage para o modelo de domínio.
 */
fun AlphaVantageSymbolMatch.toQuotation(): Quotation {
    return Quotation(
        id = symbol,
        name = name,
        symbol = symbol,
        imageUrl = null, // Alpha Vantage não fornece ícones
        currentPrice = 0.0,
        priceChange24h = 0.0
    )
}

// --- MAPEAMENTO DE PERSISTÊNCIA (ROOM) ---

/**
 * Converte um modelo de domínio Quotation para uma entidade de banco de dados Room.
 * Usado ao salvar um favorito.
 */
fun Quotation.toFavoriteEntity(): FavoriteQuotationEntity {
    return FavoriteQuotationEntity(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl
    )
}

/**
 * Converte uma entidade Room para o modelo de domínio Quotation.
 * Usado ao listar os favoritos salvos no dispositivo.
 */
fun FavoriteQuotationEntity.toQuotation(): Quotation {
    return Quotation(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl,
        currentPrice = 0.0, // Dados voláteis (preço) devem ser actualizados via API
        priceChange24h = 0.0
    )
}