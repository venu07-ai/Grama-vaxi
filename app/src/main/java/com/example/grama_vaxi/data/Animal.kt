package com.example.grama_vaxi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val breed: String,
    val age: Int,
    val species: String,
    val photoUri: String? = null,
    val lastVaccinationDate: Long? = null,
    val nextVaccinationDate: Long? = null
)
