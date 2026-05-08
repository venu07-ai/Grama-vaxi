package com.example.grama_vaxi.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.grama_vaxi.data.Animal
import com.example.grama_vaxi.ui.theme.LightBg
import com.example.grama_vaxi.ui.theme.OrangeMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAnimalScreen(
    viewModel: AnimalViewModel,
    languageViewModel: LanguageViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf("Sheep") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val t = languageViewModel::t

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val speciesList = listOf("Sheep", "Goat", "Cow", "Buffalo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("Register Animal", "ಪ್ರಾಣಿ ನೋಂದಣಿ"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { languageViewModel.toggleLanguage() }) {
                        Text(if (languageViewModel.isKannada) "EN" else "ಕನ್ನಡ", color = OrangeMain, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = LightBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo Selector
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .clickable { photoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = OrangeMain, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(t("Add Photo", "ಫೋಟೋ ಸೇರಿಸಿ"), fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Visual Species Selector
            Text(
                t("Select Animal Type", "ಪ್ರಾಣಿ ಪ್ರಕಾರವನ್ನು ಆಯ್ಕೆ ಮಾಡಿ"),
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                speciesList.take(2).forEach { species ->
                    val speciesKn = when(species) {
                        "Sheep" -> "ಕುರಿ"
                        "Goat" -> "ಮೇಕೆ"
                        else -> species
                    }
                    val isSelected = selectedSpecies == species
                    SpeciesCard(
                        name = t(species, speciesKn),
                        isSelected = isSelected,
                        onClick = { selectedSpecies = species },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                speciesList.drop(2).forEach { species ->
                    val speciesKn = when(species) {
                        "Cow" -> "ಹಸು"
                        "Buffalo" -> "ಎಮ್ಮೆ"
                        else -> species
                    }
                    val isSelected = selectedSpecies == species
                    SpeciesCard(
                        name = t(species, speciesKn),
                        isSelected = isSelected,
                        onClick = { selectedSpecies = species },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(t("Animal Name", "ಪ್ರಾಣಿಯ ಹೆಸರು")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text(t("Breed", "ತಳಿ")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text(t("Age in Years", "ವಯಸ್ಸು (ವರ್ಷಗಳಲ್ಲಿ)")) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && age.isNotBlank()) {
                        viewModel.insertAnimal(
                            Animal(
                                name = name,
                                breed = breed,
                                age = age.toIntOrNull() ?: 0,
                                species = selectedSpecies,
                                photoUri = selectedImageUri?.toString()
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(t("REGISTER", "ನೋಂದಾಯಿಸಿ"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SpeciesCard(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) OrangeMain else Color.White,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        border = if (isSelected) null else CardDefaults.outlinedCardBorder()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}
