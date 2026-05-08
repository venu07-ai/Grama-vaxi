package com.example.grama_vaxi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_vaxi.ui.theme.LightBg
import com.example.grama_vaxi.ui.theme.OrangeMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDiseaseScreen(
    languageViewModel: LanguageViewModel,
    onNavigateBack: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val t = languageViewModel::t
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("Report Sick Animal", "ಅನಾರೋಗ್ಯ ವರದಿ"), fontWeight = FontWeight.Bold) },
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
        if (submitted) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(t("Report Sent Successfully!", "ವರದಿ ಯಶಸ್ವಿಯಾಗಿ ಕಳುಹಿಸಲಾಗಿದೆ!"), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(t("The vet will be notified.", "ಪಶುವೈದ್ಯರಿಗೆ ತಿಳಿಸಲಾಗಿದೆ."), color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = OrangeMain)) {
                        Text(t("BACK TO HOME", "ಮುಖಪುಟಕ್ಕೆ ಹಿಂತಿರುಗಿ"))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Text(
                    t("Describe the symptoms", "ಲಕ್ಷಣಗಳನ್ನು ವಿವರಿಸಿ"),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text(t("Example: My sheep is not eating...", "ಉದಾಹರಣೆ: ನನ್ನ ಕುರಿ ಆಹಾರ ಸೇವಿಸುತ್ತಿಲ್ಲ...")) },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    t("Your Location", "ನಿಮ್ಮ ಸ್ಥಳ"),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(t("Temple Square, Village A", "ದೇವಸ್ಥಾನದ ಆವರಣ, ಗ್ರಾಮ ಎ")) },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (description.isNotBlank() && location.isNotBlank()) {
                            isLoading = true
                            val report = hashMapOf(
                                "description" to description,
                                "location" to location,
                                "farmerEmail" to (auth.currentUser?.email ?: "Anonymous"),
                                "timestamp" to System.currentTimeMillis()
                            )
                            firestore.collection("reports")
                                .add(report)
                                .addOnSuccessListener {
                                    isLoading = false
                                    submitted = true
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t("REPORT EMERGENCY", "ತುರ್ತು ವರದಿ ಮಾಡಿ"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
