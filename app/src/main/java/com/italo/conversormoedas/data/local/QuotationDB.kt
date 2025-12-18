package com.italo.conversormoedas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteQuotationEntity::class, AlertEntity::class],
    version = 2,
    exportSchema = false
)
abstract class QuotationDatabase : RoomDatabase() {
    abstract fun quotationDao(): QuotationDao
    abstract fun alertDao(): AlertDao

    companion object {
        const val DATABASE_NAME = "quotation_db"
    }
}