package com.italo.conversormoedas.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Entidade para armazenar alertas de preço criados pelo utilizador.
 */
@Entity(tableName = "price_alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quotationId: String,
    val symbol: String,
    val targetPrice: Double,
    val isAbove: Boolean,
    val isActive: Boolean = true
)

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Query("DELETE FROM price_alerts WHERE id = :alertId")
    suspend fun deleteAlert(alertId: Int)

    @Query("SELECT * FROM price_alerts")
    fun getAllAlertsFlow(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM price_alerts WHERE isActive = 1")
    suspend fun getActiveAlerts(): List<AlertEntity>

    @Update
    suspend fun updateAlert(alert: AlertEntity)
}