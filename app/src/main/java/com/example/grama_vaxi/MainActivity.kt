package com.example.grama_vaxi

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grama_vaxi.ui.Screen
import com.example.grama_vaxi.ui.screens.AnimalViewModel
import com.example.grama_vaxi.ui.screens.AnimalViewModelFactory
import com.example.grama_vaxi.ui.screens.FarmerScreen
import com.example.grama_vaxi.ui.screens.RegisterAnimalScreen
import com.example.grama_vaxi.ui.screens.ReportDiseaseScreen
import com.example.grama_vaxi.ui.screens.VetDashboardScreen
import com.example.grama_vaxi.ui.screens.AdminDashboardScreen
import com.example.grama_vaxi.ui.theme.AdminGrey
import com.example.grama_vaxi.ui.theme.GramaVaxiTheme
import com.example.grama_vaxi.ui.theme.LightBg
import com.example.grama_vaxi.ui.theme.OrangeMain

import com.example.grama_vaxi.ui.screens.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.example.grama_vaxi.ui.screens.ProfileScreen
import com.example.grama_vaxi.ui.screens.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            GramaVaxiTheme(darkTheme = themeViewModel.isDarkTheme) {
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ -> }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val navController = rememberNavController()
                val context = LocalContext.current
                val application = context.applicationContext as GramaVaxiApplication
                val viewModel: AnimalViewModel = viewModel(
                    factory = AnimalViewModelFactory(application.repository, application)
                )
                
                val auth = FirebaseAuth.getInstance()
                val firestore = FirebaseFirestore.getInstance()
                var userRole by remember { mutableStateOf<String?>(null) }
                
                val currentUser = auth.currentUser
                val startDestination = if (currentUser != null) {
                    // We'll determine the actual destination after fetching role
                    "loading"
                } else {
                    Screen.Login.route
                }

                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        firestore.collection("users").document(currentUser.uid).get()
                            .addOnSuccessListener { doc ->
                                userRole = doc.getString("role") ?: "Farmer"
                                // Navigate based on role if we were on loading
                                if (navController.currentDestination?.route == "loading") {
                                    val route = when (userRole) {
                                        "Vet" -> Screen.VetDashboard.route
                                        "Admin" -> Screen.AdminDashboard.route
                                        else -> Screen.Farmer.route
                                    }
                                    navController.navigate(route) {
                                        popUpTo("loading") { inclusive = true }
                                    }
                                }
                            }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LightBg
                ) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("loading") {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = OrangeMain)
                            }
                        }
                        composable(Screen.Login.route) {
                            LoginScreen(onLoginSuccess = { role ->
                                val route = when (role) {
                                    "Vet" -> Screen.VetDashboard.route
                                    "Admin" -> Screen.AdminDashboard.route
                                    else -> Screen.Farmer.route
                                }
                                navController.navigate(route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            })
                        }
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onFarmerClick = { navController.navigate(Screen.Farmer.route) },
                                onVetClick = { navController.navigate(Screen.VetDashboard.route) },
                                onAdminClick = { navController.navigate(Screen.AdminDashboard.route) }
                            )
                        }
                        composable(Screen.Farmer.route) {
                            FarmerScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onAddAnimalClick = { navController.navigate(Screen.RegisterAnimal.route) },
                                onReportDiseaseClick = { navController.navigate(Screen.ReportDisease.route) },
                                onProfileClick = { navController.navigate(Screen.Profile.route) }
                            )
                        }
                        composable(Screen.RegisterAnimal.route) {
                            RegisterAnimalScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ReportDisease.route) {
                            ReportDiseaseScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.VetDashboard.route) {
                            VetDashboardScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onProfileClick = { navController.navigate(Screen.Profile.route) }
                            )
                        }
                        composable(Screen.AdminDashboard.route) {
                            AdminDashboardScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onProfileClick = { navController.navigate(Screen.Profile.route) }
                            )
                        }
                        composable(Screen.Profile.route) {
                            ProfileScreen(
                                themeViewModel = themeViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onFarmerClick: () -> Unit = {},
    onVetClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo
        Surface(
            modifier = Modifier.size(110.dp),
            shape = RoundedCornerShape(32.dp),
            color = OrangeMain,
            shadowElevation = 20.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "G",
                    color = Color.White,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title in Kannada
        Text(
            text = "ಗ್ರಾಮ-ವ್ಯಾಕ್ಸಿ",
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF212121),
            letterSpacing = 1.sp
        )

        // Subtitle
        Text(
            text = "Village Livestock Health Manager",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Role Cards
        RoleCard(
            title = "FARMER",
            subtitle = "Manage your livestock",
            icon = Icons.Default.Agriculture,
            iconBgColor = OrangeMain,
            onClick = onFarmerClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        RoleCard(
            title = "VETERINARY OFFICER",
            subtitle = "Monitor village health",
            icon = Icons.Default.MedicalServices,
            iconBgColor = Color(0xFF121212),
            onClick = onVetClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        RoleCard(
            title = "ADMINISTRATOR",
            subtitle = "Schedule village camps",
            icon = Icons.Default.Apartment,
            iconBgColor = AdminGrey,
            onClick = onAdminClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp),
        shape = RoundedCornerShape(35.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .background(iconBgColor, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                    color = Color.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GramaVaxiTheme {
        HomeScreen()
    }
}
