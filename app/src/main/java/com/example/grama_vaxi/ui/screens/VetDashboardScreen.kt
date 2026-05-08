package com.example.grama_vaxi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class DiseaseReport(
    val id: String = "",
    val description: String = "",
    val location: String = "",
    val farmerEmail: String = "",
    val doctorAdvice: String? = null,
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetDashboardScreen(onNavigateBack: () -> Unit, onProfileClick: () -> Unit) {
    var reports by remember { mutableStateOf<List<DiseaseReport>>(emptyList()) }
    var selectedReport by remember { mutableStateOf<DiseaseReport?>(null) }
    var adviceText by remember { mutableStateOf("") }
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        firestore.collection("reports")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    reports = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(DiseaseReport::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vet Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
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
                .padding(16.dp)
        ) {
            Text(
                "Emergency Reports (ತುರ್ತು ವರದಿಗಳು)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reports) { report ->
                    AlertItem(
                        report = report,
                        onClick = { 
                            selectedReport = report
                            adviceText = report.doctorAdvice ?: ""
                        }
                    )
                }
            }
        }

        if (selectedReport != null) {
            AlertDialog(
                onDismissRequest = { selectedReport = null },
                title = { Text("Emergency Details") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Location: ${selectedReport!!.location}", fontWeight = FontWeight.Bold)
                        Text("Symptoms: ${selectedReport!!.description}")
                        Text("Farmer: ${selectedReport!!.farmerEmail}", color = Color.Gray, fontSize = 12.sp)
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Text("Medical Suggestions:", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = adviceText,
                            onValueChange = { adviceText = it },
                            placeholder = { Text("Enter first-aid advice...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            firestore.collection("reports").document(selectedReport!!.id)
                                .update("doctorAdvice", adviceText)
                                .addOnSuccessListener {
                                    val notification = hashMapOf(
                                        "title" to "Doctor Replied!",
                                        "message" to "Medical advice received for your report.",
                                        "targetEmail" to selectedReport!!.farmerEmail,
                                        "timestamp" to System.currentTimeMillis()
                                    )
                                    firestore.collection("notifications").add(notification)
                                    selectedReport = null
                                }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeMain)
                    ) {
                        Text("SEND ADVICE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedReport = null }) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}

@Composable
fun AlertItem(report: DiseaseReport, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (report.doctorAdvice == null) Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null, 
                tint = if (report.doctorAdvice == null) Color.Red else Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = report.description, fontWeight = FontWeight.Medium, maxLines = 1)
                Text(text = "Location: ${report.location}", fontSize = 12.sp, color = Color.Gray)
                if (report.doctorAdvice != null) {
                    Text(text = "Replied", fontSize = 10.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}
