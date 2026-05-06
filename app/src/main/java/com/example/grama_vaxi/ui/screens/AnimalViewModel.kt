package com.example.grama_vaxi.ui.screens

import android.app.Application
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.grama_vaxi.data.Animal
import com.example.grama_vaxi.data.AnimalRepository
import com.example.grama_vaxi.worker.VaccineReminderWorker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AnimalViewModel(
    private val repository: AnimalRepository,
    application: Application
) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    val allAnimals: StateFlow<List<Animal>> = repository.getAllAnimals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertAnimal(animal: Animal) {
        viewModelScope.launch {
            val nextShotDate = System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000) // 180 days approx
            val animalWithDates = animal.copy(
                lastVaccinationDate = System.currentTimeMillis(),
                nextVaccinationDate = nextShotDate
            )
            
            // Sync to Firebase
            syncAnimalToFirebase(animalWithDates)
            
            repository.insertAnimal(animalWithDates)
            scheduleVaccineReminder(animalWithDates)
        }
    }

    private fun syncAnimalToFirebase(animal: Animal) {
        val animalMap = hashMapOf(
            "name" to animal.name,
            "breed" to animal.breed,
            "age" to animal.age,
            "species" to animal.species,
            "lastVaccinationDate" to animal.lastVaccinationDate,
            "nextVaccinationDate" to animal.nextVaccinationDate,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("animals")
            .add(animalMap)
            .addOnSuccessListener { documentReference ->
                animal.photoUri?.let { uriString ->
                    try {
                        uploadPhotoToStorage(documentReference.id, Uri.parse(uriString))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    private fun uploadPhotoToStorage(animalId: String, fileUri: Uri) {
        val photoRef = storage.reference.child("animal_photos/$animalId.jpg")
        photoRef.putFile(fileUri)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    firestore.collection("animals").document(animalId)
                        .update("photoUrl", downloadUri.toString())
                }
            }
    }

    private fun scheduleVaccineReminder(animal: Animal) {
        val data = Data.Builder()
            .putString("ANIMAL_NAME", animal.name)
            .build()

        // Schedule for 10 seconds later for demo purposes
        val workRequest = OneTimeWorkRequestBuilder<VaccineReminderWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setInputData(data)
            .build()

        workManager.enqueue(workRequest)
    }

    fun deleteAnimal(animal: Animal) {
        viewModelScope.launch {
            repository.deleteAnimal(animal)
        }
    }
}

class AnimalViewModelFactory(
    private val repository: AnimalRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimalViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
