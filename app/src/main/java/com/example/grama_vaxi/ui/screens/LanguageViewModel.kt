package com.example.grama_vaxi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LanguageViewModel : ViewModel() {
    var isKannada by mutableStateOf(false)
        private set

    fun toggleLanguage() {
        isKannada = !isKannada
    }
    
    fun setLanguage(kannada: Boolean) {
        isKannada = kannada
    }

    // Helper function to get text based on current language
    fun t(en: String, kn: String): String {
        return if (isKannada) kn else en
    }
}
