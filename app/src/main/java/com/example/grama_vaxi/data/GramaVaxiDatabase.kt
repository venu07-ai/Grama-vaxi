package com.example.grama_vaxi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Animal::class], version = 1, exportSchema = false)
abstract class GramaVaxiDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao

    companion object {
        @Volatile
        private var Instance: GramaVaxiDatabase? = null

        fun getDatabase(context: Context): GramaVaxiDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GramaVaxiDatabase::class.java, "grama_vaxi_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
