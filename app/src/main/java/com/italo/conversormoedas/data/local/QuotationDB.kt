package com.italo.conversormoedas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteQuotationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuotationDB : RoomDatabase() {
    abstract fun quotationDao(): QuotationDao

    companion object {
        const val DATABASE_NAME = "quotation_db"
    }
}