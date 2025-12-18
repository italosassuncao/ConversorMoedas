package com.italo.conversormoedas.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite_quotations")
data class FavoriteQuotationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String?
)

@Dao
interface QuotationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(quotation: FavoriteQuotationEntity)

    @Query("DELETE FROM favorite_quotations WHERE id = :quotationId")
    suspend fun deleteFavorite(quotationId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_quotations WHERE id = :quotationId)")
    fun isFavorite(quotationId: String): Flow<Boolean>

    @Query("SELECT * FROM favorite_quotations")
    fun getAllFavorites(): Flow<List<FavoriteQuotationEntity>>
}