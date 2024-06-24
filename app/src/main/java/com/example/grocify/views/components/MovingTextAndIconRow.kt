package com.example.grocify.views.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.views.theme.BlueLight

@Composable
fun MovingTextAndIconRow(
    orderId: String,
    onTrackOrderClick: (orderId: String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val animationDurationMillis = (5000 * (screenWidth.value / 500)).toLong()

    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -screenWidth.value,
        targetValue = screenWidth.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .background(BlueLight)
            .clickable(onClick = { onTrackOrderClick(orderId)})
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset(x = offsetX.dp)
        ) {
            Text(
                text = "Controlla lo stato del tuo ordine",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                Icons.Filled.LocalShipping,
                contentDescription = "Localized description",
                tint = Color.White
            )
        }
    }
}