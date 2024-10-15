// HomeScreen.kt
package com.evashadidi.validator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.evashadidi.validator.ui.theme.ValidatorTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    var isRunningChecks by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Get a CoroutineScope tied to the composable

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Align content to the center
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.asset_1), // Replace with your image resource
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Logo and Text in the middle center
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()), // Add vertical scroll if needed
            verticalArrangement = Arrangement.Center, // Align content in the center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Align content in the center horizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_color),
                contentDescription = "Validator Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = "Hoopo Validator",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Ensuring your device integrity.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }

        // Button at the very end of the screen
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .align(Alignment.BottomCenter), // Align content to the bottom center
            horizontalAlignment = Alignment.CenterHorizontally // Align content in the center horizontally
        ) {
            Button(
                onClick = {
                    isRunningChecks = true
                    coroutineScope.launch {
                        delay(2000)
                        isRunningChecks = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = if (isRunningChecks) "Running all checks..." else "Run All Checks Now",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CheckCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ValidatorTheme {
        HomeScreen()
    }
}
