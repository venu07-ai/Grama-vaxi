package com.example.grama_vaxi.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_vaxi.R
import com.example.grama_vaxi.ui.theme.OrangeMain
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNextScreen: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = springSpec()
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000)
        )
        delay(2000)
        onNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
                    .alpha(alpha.value),
                shape = RoundedCornerShape(40.dp),
                color = Color.White,
                shadowElevation = 30.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gemini_generated_image_8g4rho8g4rho8g4r),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Grama-Vaxi",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = OrangeMain,
                modifier = Modifier.alpha(alpha.value)
            )

            Text(
                text = "Village Livestock Companion",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier.alpha(alpha.value)
            )
        }
        
        // Footer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "Smart Health for Every Village",
                fontSize = 12.sp,
                color = Color.LightGray,
                letterSpacing = 2.sp
            )
        }
    }
}

private fun springSpec() = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)
