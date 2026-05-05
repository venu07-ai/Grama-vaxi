package com.example.grama_vaxi

import android.app.Application
import com.example.grama_vaxi.data.AnimalRepository
import com.example.grama_vaxi.data.GramaVaxiDatabase

class GramaVaxiApplication : Application() {
    private val database: GramaVaxiDatabase by lazy { GramaVaxiDatabase.getDatabase(this) }
    val repository: AnimalRepository by lazy { AnimalRepository(database.animalDao()) }
}
