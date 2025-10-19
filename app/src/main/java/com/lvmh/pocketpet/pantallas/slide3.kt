package com.lvmh.pocketpet.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Slide3() {
    val celeste = Color(0xFF4A90E2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(celeste)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚙️",
                fontSize = 100.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Personaliza tu experiencia",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cuida de tu mascota financiera.\nEstablece metas de ahorro y\nconfigura categorías personalizadas.",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}