package com.example.grama_vaxi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.grama_vaxi.data.Animal
import com.example.grama_vaxi.ui.theme.LightBg
import com.example.grama_vaxi.ui.theme.OrangeMain
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerScreen(
    viewModel: AnimalViewModel,
    onNavigateBack: () -> Unit,
    onAddAnimalClick: () -> Unit,
    onReportDiseaseClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val animals by viewModel.allAnimals.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onReportDiseaseClick) {
                        Icon(Icons.Default.NotificationImportant, contentDescription = "Report Disease", tint = Color.Red)
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAnimalClick,
                containerColor = OrangeMain,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Register Animal", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = LightBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Vaccine Calendar Section
            item {
                Text(
                    text = "Vaccine Calendar (ಲಸಿಕೆ ಕ್ಯಾಲೆಂಡರ್)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                VaccineCalendarRow(animals)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Animal Ledger Section
            item {
                Text(
                    text = "Animal Ledger (ಪ್ರಾಣಿ ಪುಸ್ತಕ)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (animals.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(animals) { animal ->
                    AnimalHealthCard(animal = animal)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun VaccineCalendarRow(animals: List<Animal>) {
    if (animals.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Register animals to see vaccination dates",
                modifier = Modifier.padding(24.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(animals) { animal ->
                Card(
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeMain)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(animal.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                        val dateStr = animal.nextVaccinationDate?.let {
                            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(it))
                        } ?: "TBD"
                        Text("Next: $dateStr", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimalHealthCard(animal: Animal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Large Animal Icon or Photo
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            color = when (animal.species) {
                                "Cow" -> Color(0xFFFFEBEE)
                                "Sheep" -> Color(0xFFE3F2FD)
                                else -> Color(0xFFF1F8E9)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (animal.photoUri != null) {
                        AsyncImage(
                            model = animal.photoUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = when (animal.species) {
                                "Cow" -> Icons.Default.Agriculture
                                "Sheep" -> Icons.Default.Pets
                                else -> Icons.Default.Pets
                            },
                            contentDescription = null,
                            tint = when (animal.species) {
                                "Cow" -> Color.Red
                                "Sheep" -> Color.Blue
                                else -> Color.Green
                            },
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = animal.name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text(text = "${animal.breed} • ${animal.age} Years", color = Color.Gray, fontSize = 14.sp)
                }
                
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "HEALTHY",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF2E7D32),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Last Vaccination", fontSize = 11.sp, color = Color.Gray)
                    val lastDateStr = animal.lastVaccinationDate?.let {
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "N/A"
                    Text(lastDateStr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Next Shot Due", fontSize = 11.sp, color = OrangeMain, fontWeight = FontWeight.Bold)
                    val nextDateStr = animal.nextVaccinationDate?.let {
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "Pending"
                    Text(nextDateStr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No animals in your ledger yet.",
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            "ಲಸಿಕೆ ವಿವರಗಳಿಗಾಗಿ ಪ್ರಾಣಿಗಳನ್ನು ಸೇರಿಸಿ",
            color = Color.Gray.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}
