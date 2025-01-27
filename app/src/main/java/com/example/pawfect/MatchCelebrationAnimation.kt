package com.example.pawfect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon // Replace with material.icons if you're using Material 2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.appinterface.R
import androidx.compose.material3.Text
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer


@Composable
fun MatchCelebrationAnimation(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() }, // Dismiss on click
        contentAlignment = Alignment.Center
    ) {
        val scale = remember { androidx.compose.animation.core.Animatable(0f) }

        LaunchedEffect(Unit) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated Heart
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_heart_match),
                contentDescription = "Match Found!",
                tint = Color.Red,
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "It's a Match!",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
