package com.example.conversormoedas.data.local

import androidx.room.PrimaryKey
import com.example.conversormoedas.data.models.Quotation

@Entity(tableName = "favorite_quotations")
data class FavoriteQuotationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String
)

// Funções de extensão para mapear entre o modelo de domínio e entidade de banco de dados

// Converte um modelo de domínio em uma entidade Room
fun Quotation.toFavoriteEntity(): FavoriteQuotationEntity {
    return FavoriteQuotationEntity(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl
    )
}

// Conversão de uma entidade Room para um modelo de domínio
fun FavoriteQuotationEntity.toQuotation(): Quotation {
    return Quotation(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl,
        currentPrice = 0.0,
        priceChange24h = 0.0
    )
}