package com.example.grama_vaxi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_vaxi.R
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
    var adminKey by remember { mutableStateOf("") }
    
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val t = languageViewModel::t
    val secretAdminKey = "GRAMA-ADMIN-777"

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image with Gradient Overlay
        Image(
            painter = painterResource(id = R.drawable.gemini_generated_image_8g4rho8g4rho8g4r),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.8f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Branding Logo
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gemini_generated_image_58ld1w58ld1w58ld),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isRegistering) t("Create Account", "ಖಾತೆ ರಚಿಸಿ") else t("Grama-Vaxi Login", "ಗ್ರಾಮ-ವ್ಯಾಕ್ಸಿ ಲಾಗಿನ್"),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1B5E20) // Deep Green
            )
            Text(
                text = t("Village Health Management", "ಗ್ರಾಮದ ಆರೋಗ್ಯ ನಿರ್ವಹಣೆ"),
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Login/Register Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(t("Email", "ಇಮೇಲ್")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = OrangeMain) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(t("Password", "ಪಾಸ್‌ವರ್ಡ್")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = OrangeMain) }
                    )

                    if (isRegistering) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            t("Select Your Role", "ನಿಮ್ಮ ಪಾತ್ರವನ್ನು ಆಯ್ಕೆ ಮಾಡಿ"),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RoleSelectorItem(
                                title = t("Farmer", "ರೈತ"),
                                icon = Icons.Default.Agriculture,
                                isSelected = selectedRole == "Farmer",
                                onClick = { selectedRole = "Farmer" },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            RoleSelectorItem(
                                title = t("Vet", "ವೈದ್ಯ"),
                                icon = Icons.Default.MedicalServices,
                                isSelected = selectedRole == "Veterinary Officer",
                                onClick = { selectedRole = "Veterinary Officer" },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            RoleSelectorItem(
                                title = t("Admin", "ಅಡ್ಮಿನ್"),
                                icon = Icons.Default.AdminPanelSettings,
                                isSelected = selectedRole == "Admin",
                                onClick = { selectedRole = "Admin" },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        if (selectedRole == "Admin") {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = adminKey,
                                onValueChange = { adminKey = it },
                                label = { Text("Secret Admin Key") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.Red) }
                            )
                        }
                    }

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = it, color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                isLoading = true
                                if (isRegistering) {
                                    if (selectedRole == "Admin") {
                                        if (adminKey != secretAdminKey) {
                                            errorMessage = t("Invalid Admin Key!", "ತಪ್ಪು ಅಡ್ಮಿನ್ ಕೀ!")
                                            isLoading = false
                                            return@Button
                                        }
                                        
                                        firestore.collection("users").whereEqualTo("role", "Admin").get()
                                            .addOnSuccessListener { snapshot ->
                                                if (!snapshot.isEmpty) {
                                                    errorMessage = t("An Admin account already exists!", "ಅಡ್ಮಿನ್ ಖಾತೆ ಈಗಾಗಲೇ ಅಸ್ತಿತ್ವದಲ್ಲಿದೆ!")
                                                    isLoading = false
                                                } else {
                                                    performRegistration(auth, firestore, email, password, selectedRole, 
                                                        onSuccess = { role ->
                                                            isLoading = false
                                                            onLoginSuccess(role)
                                                        },
                                                        onFailure = { error ->
                                                            isLoading = false
                                                            errorMessage = error
                                                        }
                                                    )
                                                }
                                            }
                                    } else {
                                        performRegistration(auth, firestore, email, password, selectedRole,
                                            onSuccess = { role ->
                                                isLoading = false
                                                onLoginSuccess(role)
                                            },
                                            onFailure = { error ->
                                                isLoading = false
                                                errorMessage = error
                                            }
                                        )
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
                        enabled = !isLoading,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                if (isRegistering) t("CREATE ACCOUNT", "ಖಾತೆ ರಚಿಸಿ") else t("LOGIN", "ಲಾಗಿನ್"),
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { isRegistering = !isRegistering },
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    if (isRegistering) t("Already have an account? Login", "ಈಗಾಗಲೇ ಖಾತೆ ಇದೆಯೇ? ಲಾಗಿನ್ ಮಾಡಿ") 
                    else t("Don't have an account? Register", "ಖಾತೆ ಇಲ್ಲವೇ? ನೋಂದಾಯಿಸಿ"),
                    color = Color(0xFF1B5E20),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Language Toggle at Top Right (Moved to end to be on top layer)
        TextButton(
            onClick = { languageViewModel.toggleLanguage() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 16.dp, end = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (languageViewModel.isKannada) "English" else "ಕನ್ನಡ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RoleSelectorItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) OrangeMain.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) OrangeMain else Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) OrangeMain else Color.Gray
        )
    }
}

private fun performRegistration(
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    email: String,
    password: String,
    role: String,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val user = result.user
            if (user != null) {
                val userData = hashMapOf("email" to email, "role" to role)
                firestore.collection("users").document(user.uid).set(userData)
                    .addOnSuccessListener {
                        onSuccess(role)
                    }
                    .addOnFailureListener {
                        onFailure(it.localizedMessage ?: "Firestore error")
                    }
            }
        }
        .addOnFailureListener {
            onFailure(it.localizedMessage ?: "Registration failed")
        }
}
