// HomeScreen.kt
package com.evashadidi.validator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evashadidi.validator.ui.theme.ValidatorTheme
import com.evashadidi.validator.R

@Composable
fun HomeScreen() {
    // Apply a vertical gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    ) {
        // Content Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo or Icon (Replace with your actual logo)
            Image(
                painter = painterResource(id = R.drawable.ic_validator_logo_foreground), // Ensure you have this drawable
                contentDescription = "Validator Logo",
                modifier = Modifier
                    .size(120.dp)
                    .shadow(8.dp, RoundedCornerShape(60.dp)) // Circular shadow
            )

            Spacer(modifier = Modifier.height(24.dp)) // Space between logo and title

            // Title Text
            Text(
                text = "HOOPO VALIDATOR BY HADIDIZ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 16.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp)) // Space between title and subtitle

            // Subtitle Text within a Card for emphasis
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Checker Services Running...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Additional space if needed

            // Optional: Add an indicator or animation to show services are running
            // For simplicity, we'll add a CircularProgressIndicator
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
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
