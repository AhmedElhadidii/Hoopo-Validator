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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evashadidi.validator.ui.theme.ValidatorTheme
import com.evashadidi.validator.R

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD700), // Gold
                        Color(0xFFFFA500)  // Darker Gold
                    )
                )
            ),
        contentAlignment = Alignment.Center // Center all content within the Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_validator_logo_foreground),
                contentDescription = "Validator Logo",
                modifier = Modifier
                    .size(100.dp)
                    .shadow(6.dp, RoundedCornerShape(50.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HADIDIZ HOOPO Validator",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp).wrapContentWidth(Alignment.CenterHorizontally),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(86.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Adjust width to fit content better
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "Checker Services Running...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(14.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    maxLines = 1
                )
            }

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
