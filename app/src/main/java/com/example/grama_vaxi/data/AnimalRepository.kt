package com.example.grama_vaxi.data

import kotlinx.coroutines.flow.Flow

class AnimalRepository(private val animalDao: AnimalDao) {
    fun getAllAnimals(): Flow<List<Animal>> = animalDao.getAllAnimals()

    suspend fun insertAnimal(animal: Animal) = animalDao.insertAnimal(animal)

    suspend fun deleteAnimal(animal: Animal) = animalDao.deleteAnimal(animal)

    suspend fun getAnimalById(id: Int) = animalDao.getAnimalById(id)
}
