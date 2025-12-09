package com.example.conversormoedas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.conversormoedas.data.models.Quotation

@Entity(tableName = "favorite_quotations")
data class FavoriteQuotationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String?
)

// Converte um modelo de Domínio (API) em uma Entidade Room (Local).
fun Quotation.toFavoriteEntity(): FavoriteQuotationEntity {
    return FavoriteQuotationEntity(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl
    )
}

// Converte uma Entidade Room (Local) em um modelo de Domínio (API).
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