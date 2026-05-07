package com.example.grama_vaxi.ui.screens

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.grama_vaxi.data.Animal
import com.example.grama_vaxi.data.AnimalRepository
import com.example.grama_vaxi.worker.VaccineReminderWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    private val auth = FirebaseAuth.getInstance()

    val allAnimals: StateFlow<List<Animal>> = repository.getAllAnimals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val firebaseAnimals = mutableStateListOf<Animal>()

    fun fetchAnimalsForCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("animals")
                .whereEqualTo("ownerUid", currentUser.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null) {
                        val animals = snapshot.documents.mapNotNull { doc ->
                            Animal(
                                id = 0,
                                name = doc.getString("name") ?: "",
                                breed = doc.getString("breed") ?: "",
                                age = (doc.getLong("age") ?: 0).toInt(),
                                species = doc.getString("species") ?: "",
                                photoUri = doc.getString("photoUrl"),
                                lastVaccinationDate = doc.getLong("lastVaccinationDate"),
                                nextVaccinationDate = doc.getLong("nextVaccinationDate")
                            )
                        }
                        firebaseAnimals.clear()
                        firebaseAnimals.addAll(animals)
                    }
                }
        }
    }

    fun insertAnimal(animal: Animal) {
        viewModelScope.launch {
            val nextShotDate = System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000)
            val animalWithDates = animal.copy(
                lastVaccinationDate = System.currentTimeMillis(),
                nextVaccinationDate = nextShotDate
            )
            
            syncAnimalToFirebase(animalWithDates)
            repository.insertAnimal(animalWithDates)
            scheduleVaccineReminder(animalWithDates)
        }
    }

    private fun syncAnimalToFirebase(animal: Animal) {
        val currentUser = auth.currentUser
        val animalMap = hashMapOf(
            "name" to animal.name,
            "breed" to animal.breed,
            "age" to animal.age,
            "species" to animal.species,
            "lastVaccinationDate" to animal.lastVaccinationDate,
            "nextVaccinationDate" to animal.nextVaccinationDate,
            "ownerUid" to (currentUser?.uid ?: "anonymous"),
            "ownerEmail" to (currentUser?.email ?: "anonymous"),
            "timestamp" to System.currentTimeMillis()
        )

        Log.d("FirebaseSync", "Attempting to sync animal: ${animal.name}")

        firestore.collection("animals")
            .add(animalMap)
            .addOnSuccessListener { documentReference ->
                Log.d("FirebaseSync", "Successfully added animal to Firestore with ID: ${documentReference.id}")
                animal.photoUri?.let { uriString ->
                    try {
                        uploadPhotoToStorage(documentReference.id, Uri.parse(uriString))
                    } catch (e: Exception) {
                        Log.e("FirebaseSync", "Error parsing photo URI: ${e.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseSync", "Failed to sync animal: ${e.message}")
            }
    }

    private fun uploadPhotoToStorage(animalId: String, fileUri: Uri) {
        val photoRef = storage.reference.child("animal_photos/$animalId.jpg")
        photoRef.putFile(fileUri)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    firestore.collection("animals").document(animalId)
                        .update("photoUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            Log.d("FirebaseSync", "Successfully updated photo URL in Firestore")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseSync", "Failed to upload photo: ${e.message}")
            }
    }

    private fun scheduleVaccineReminder(animal: Animal) {
        val data = Data.Builder()
            .putString("ANIMAL_NAME", animal.name)
            .build()

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
