package com.example.grama_vaxi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_vaxi.ui.theme.OrangeMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    languageViewModel: LanguageViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    
    var userRole by remember { mutableStateOf("Farmer") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val t = languageViewModel::t

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    userRole = doc.getString("role") ?: "Farmer"
                    name = doc.getString("name") ?: ""
                    phone = doc.getString("phone") ?: ""
                    village = doc.getString("village") ?: ""
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("Profile", "ಪ್ರೊಫೈಲ್"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Profile Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(OrangeMain.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = OrangeMain
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentUser?.email ?: t("No Email", "ಇಮೇಲ್ ಇಲ್ಲ"),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${t("Role", "ಪಾತ್ರ")}: $userRole",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Editable Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(t("Personal Details", "ವೈಯಕ್ತಿಕ ವಿವರಗಳು"), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangeMain)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(t("Full Name", "ಪೂರ್ಣ ಹೆಸರು")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(t("Phone Number", "ದೂರವಾಣಿ ಸಂಖ್ಯೆ")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = village,
                        onValueChange = { village = it },
                        label = { Text(t("Village/Address", "ಗ್ರಾಮ/ವಿಳಾಸ")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (currentUser != null) {
                                isSaving = true
                                val updates = hashMapOf(
                                    "name" to name,
                                    "phone" to phone,
                                    "village" to village
                                )
                                firestore.collection("users").document(currentUser.uid).update(updates as Map<String, Any>)
                                    .addOnSuccessListener { isSaving = false }
                                    .addOnFailureListener { isSaving = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(t("SAVE PROFILE", "ಪ್ರೊಫೈಲ್ ಉಳಿಸಿ"), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(t("Settings", "ಸೆಟ್ಟಿಂಗ್‌ಗಳು"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Dark Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (themeViewModel.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(t("Dark Theme", "ಡಾರ್ಕ್ ಥೀಮ್"))
                        }
                        Switch(
                            checked = themeViewModel.isDarkTheme,
                            onCheckedChange = { themeViewModel.toggleTheme() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Language Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(t("Language (ಕನ್ನಡ)", "ಭಾಷೆ (English)"))
                        }
                        Switch(
                            checked = languageViewModel.isKannada,
                            onCheckedChange = { languageViewModel.toggleLanguage() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = {
                    auth.signOut()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(t("LOGOUT", "ಲಾಗ್ ಔಟ್"), fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
