package com.example.conversormoedas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_quotations")
data class FavoriteQuotationEntity {
    @PrimaryKey
    val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String?
}