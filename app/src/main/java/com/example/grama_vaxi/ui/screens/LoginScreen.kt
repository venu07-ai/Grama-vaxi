package com.example.grama_vaxi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_vaxi.ui.theme.OrangeMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    languageViewModel: LanguageViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Farmer") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val t = languageViewModel::t

    Box(modifier = Modifier.fillMaxSize()) {
        // Language Toggle at Top Right
        TextButton(
            onClick = { languageViewModel.toggleLanguage() },
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 16.dp)
        ) {
            Text(if (languageViewModel.isKannada) "English" else "ಕನ್ನಡ", fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isRegistering) t("Create Account", "ಖಾತೆ ರಚಿಸಿ") else t("Grama-Vaxi Login", "ಗ್ರಾಮ-ವ್ಯಾಕ್ಸಿ ಲಾಗಿನ್"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OrangeMain
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = t("Secure access for Village Health", "ಗ್ರಾಮದ ಆರೋಗ್ಯಕ್ಕಾಗಿ ಸುರಕ್ಷಿತ ಪ್ರವೇಶ"), color = Color.Gray)
            
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(t("Email", "ಇಮೇಲ್")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(t("Password", "ಪಾಸ್‌ವರ್ಡ್")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (isRegistering) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(t("Select Role:", "ಪಾತ್ರವನ್ನು ಆಯ್ಕೆ ಮಾಡಿ:"), fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Farmer", "Veterinary Officer", "Admin").forEach { role ->
                        val roleDisplay = when(role) {
                            "Farmer" -> t("Farmer", "ರೈತ")
                            "Veterinary Officer" -> t("Veterinary", "ಪಶುವೈದ್ಯ")
                            "Admin" -> t("Admin", "ನಿರ್ವಾಹಕ")
                            else -> role
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedRole == role, onClick = { selectedRole = role })
                            Text(roleDisplay)
                        }
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        if (isRegistering) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { result ->
                                    val user = result.user
                                    if (user != null) {
                                        val userData = hashMapOf("email" to email, "role" to selectedRole)
                                        firestore.collection("users").document(user.uid).set(userData)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                onLoginSuccess(selectedRole)
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    errorMessage = it.localizedMessage ?: t("Registration failed", "ನೋಂದಣಿ ವಿಫಲವಾಗಿದೆ")
                                }
                        } else {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener { result ->
                                    firestore.collection("users").document(result.user!!.uid).get()
                                        .addOnSuccessListener { document ->
                                            val role = document.getString("role") ?: "Farmer"
                                            isLoading = false
                                            onLoginSuccess(role)
                                        }
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    errorMessage = it.localizedMessage ?: t("Login failed", "ಲಾಗಿನ್ ವಿಫಲವಾಗಿದೆ")
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        if (isRegistering) t("REGISTER", "ನೋಂದಾಯಿಸಿ") else t("LOGIN", "ಲಾಗಿನ್"),
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(
                    if (isRegistering) t("Already have an account? Login", "ಈಗಾಗಲೇ ಖಾತೆ ಇದೆಯೇ? ಲಾಗಿನ್ ಮಾಡಿ") 
                    else t("Don't have an account? Register", "ಖಾತೆ ಇಲ್ಲವೇ? ನೋಂದಾಯಿಸಿ")
                )
            }
        }
    }
}
