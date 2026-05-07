package com.example.grama_vaxi

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import kotlinx.coroutines.launch

import com.example.grama_vaxi.ui.screens.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.example.grama_vaxi.ui.screens.ProfileScreen
import com.example.grama_vaxi.ui.screens.ThemeViewModel
import com.example.grama_vaxi.ui.screens.SplashScreen

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
                var currentUser by remember { mutableStateOf(auth.currentUser) }
                var userRole by remember { mutableStateOf<String?>(null) }
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    firestore.collection("notifications")
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(1)
                        .addSnapshotListener { snapshot, error ->
                            if (error == null && snapshot != null && !snapshot.isEmpty) {
                                val doc = snapshot.documents[0]
                                val timestamp = doc.getLong("timestamp") ?: 0L
                                // Only show if it's very recent (e.g. within last 30 seconds)
                                if (System.currentTimeMillis() - timestamp < 30000) {
                                    val title = doc.getString("title") ?: "New Update"
                                    scope.launch {
                                        snackbarHostState.showSnackbar(title)
                                    }
                                }
                            }
                        }
                }

                DisposableEffect(auth) {
                    val listener = FirebaseAuth.AuthStateListener { 
                        currentUser = it.currentUser
                    }
                    auth.addAuthStateListener(listener)
                    onDispose { auth.removeAuthStateListener(listener) }
                }

                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        firestore.collection("users").document(currentUser!!.uid).get()
                            .addOnSuccessListener { doc ->
                                userRole = doc.getString("role") ?: "Farmer"
                            }
                    } else {
                        userRole = null
                    }
                }

                // Monitor navigation to "loading" to auto-redirect once role is known
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                LaunchedEffect(currentRoute, userRole) {
                    if (currentRoute == "loading" && userRole != null) {
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

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    content = { padding ->
                        Surface(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            color = LightBg
                        ) {
                            NavHost(navController = navController, startDestination = Screen.Splash.route) {
                                composable(Screen.Splash.route) {
                                    SplashScreen(onNextScreen = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    })
                                }
                                composable("loading") {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = OrangeMain)
                                    }
                                }
                                composable(Screen.Login.route) {
                                    LoginScreen(onLoginSuccess = { role ->
                                        userRole = role // Update local state immediately
                                        val route = when (role) {
                                            "Vet" -> Screen.VetDashboard.route
                                            "Admin" -> Screen.AdminDashboard.route
                                            else -> Screen.Farmer.route
                                        }
                                        navController.navigate(route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                            popUpTo(Screen.Home.route) { inclusive = true }
                                        }
                                    })
                                }
                                composable(Screen.Home.route) {
                                    HomeScreen(
                                        onGetStartedClick = {
                                            if (auth.currentUser != null) {
                                                navController.navigate("loading")
                                            } else {
                                                navController.navigate(Screen.Login.route)
                                            }
                                        }
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
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background Image with stylized overlay
        Image(
            painter = painterResource(id = R.drawable.gemini_generated_image_8g4rho8g4rho8g4r),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.15f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo in a high-quality card
            Surface(
                modifier = Modifier
                    .size(160.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(40.dp),
                color = Color.White,
                shadowElevation = 20.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gemini_generated_image_58ld1w58ld1w58ld),
                        contentDescription = "GramaVaxi Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Title and Branding
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ಗ್ರಾಮ-ವ್ಯಾಕ್ಸಿ",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1B5E20), // Darker green for a nature/livestock feel
                    letterSpacing = 1.sp
                )
                
                Text(
                    text = "Grama-Vaxi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeMain,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Revolutionizing Village Livestock Care",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Feature Highlights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureIcon(Icons.Default.MedicalServices, "Health")
                FeatureIcon(Icons.Default.Agriculture, "Livestock")
                FeatureIcon(Icons.Default.Apartment, "Village")
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Premium Get Started Button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "GET STARTED",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun FeatureIcon(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(OrangeMain.copy(alpha = 0.1f), RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = OrangeMain, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GramaVaxiTheme {
        HomeScreen()
    }
}
