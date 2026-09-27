package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CropDiagnosisRecord::class], version = 1, exportSchema = false)
abstract class CropDatabase : RoomDatabase() {
    abstract fun cropDao(): CropDao

    companion object {
        @Volatile
        private var INSTANCE: CropDatabase? = null

        fun getDatabase(context: Context): CropDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CropDatabase::class.java,
                    "cropshield_database"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
