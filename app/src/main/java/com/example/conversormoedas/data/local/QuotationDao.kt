package com.example.conversormoedas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(quotation: FavoriteQuotationEntity)

    @Query("DELETE FROM favorite_quotations WHERE id = :quotationId")
    suspend fun deleteFavorite(quotationId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_quotations WHERE id = :quotationId)")
    fun isFavorite(quotationId: String): Flow<Boolean>

    @Query("SELECT * FROM favorite_quotations")
    fun getAllFavorites(): Flow<List<FavoriteQuotationEntity>>
}