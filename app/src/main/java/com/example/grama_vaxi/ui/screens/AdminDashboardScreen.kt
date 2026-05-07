package com.example.grama_vaxi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_vaxi.ui.theme.LightBg
import com.example.grama_vaxi.ui.theme.OrangeMain

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class Camp(
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val description: String = "",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(onNavigateBack: () -> Unit, onProfileClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var camps by remember { mutableStateOf<List<Camp>>(emptyList()) }
    val firestore = FirebaseFirestore.getInstance()

    // Form State
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        firestore.collection("camps")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    camps = snapshot.toObjects(Camp::class.java)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard (ನಿರ್ವಾಹಕರ ಡ್ಯಾಶ್‌ಬೋರ್ಡ್‌)", fontWeight = FontWeight.Bold) },
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = OrangeMain,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("SCHEDULE CAMP") }
            )
        },
        containerColor = LightBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                "Active Camps (ಸಕ್ರಿಯ ಶಿಬಿರಗಳು)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(camps) { camp ->
                    CampCard(camp.title, "${camp.date} at ${camp.time}", "Location: ${camp.location}")
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Schedule New Camp", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (e.g. Oct 25)") })
                        OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 10:00 AM)") })
                        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
                        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && date.isNotBlank()) {
                                val campData = hashMapOf(
                                    "title" to title,
                                    "date" to date,
                                    "time" to time,
                                    "location" to location,
                                    "description" to description,
                                    "timestamp" to System.currentTimeMillis()
                                )
                                firestore.collection("camps").add(campData)
                                    .addOnSuccessListener {
                                        showDialog = false
                                        // Send notification (simulated by adding to a notifications collection)
                                        val notification = hashMapOf(
                                            "title" to "New Camp: $title",
                                            "message" to "Join us at $location on $date at $time",
                                            "timestamp" to System.currentTimeMillis()
                                        )
                                        firestore.collection("notifications").add(notification)
                                        
                                        // Reset fields
                                        title = ""; date = ""; time = ""; location = ""; description = ""
                                    }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeMain)
                    ) {
                        Text("SCHEDULE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}

@Composable
fun CampCard(title: String, date: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF2196F3))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = date, fontSize = 14.sp, color = Color.Gray)
                Text(text = status, fontSize = 12.sp, color = OrangeMain, fontWeight = FontWeight.Bold)
            }
        }
    }
}
