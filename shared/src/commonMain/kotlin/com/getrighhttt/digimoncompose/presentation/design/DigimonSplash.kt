package com.getrighhttt.digimoncompose.presentation.design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.getrighhttt.digimoncompose.generated.resources.Res
import com.getrighhttt.digimoncompose.generated.resources.splash_agumon
import org.jetbrains.compose.resources.painterResource

/** Bundled artwork appears offline. Requests start behind this finite entrance animation. */
@Composable
internal fun DigimonSplash(onFinished: () -> Unit) {
    val colors = LocalFieldGuideColors.current
    val entrance = remember { Animatable(0f) }
    val opacity = remember { Animatable(1f) }
    val finish by rememberUpdatedState(onFinished)
    LaunchedEffect(Unit) {
        entrance.animateTo(1f, tween(850, easing = FastOutSlowInEasing))
        opacity.animateTo(0f, tween(250))
        finish()
    }
    Box(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding(), contentAlignment = Alignment.Center) {
        Column(Modifier.graphicsLayer { alpha = opacity.value },
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(28.dp)) {
            Box(Modifier.size(228.dp).graphicsLayer {
                val scale = .82f + .18f * entrance.value
                scaleX = scale; scaleY = scale; alpha = entrance.value
            }.background(colors.tint, CircleShape), contentAlignment = Alignment.Center) {
                Box(Modifier.size(196.dp).clip(CircleShape).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                    Image(painterResource(Res.drawable.splash_agumon), contentDescription = "Agumon",
                        modifier = Modifier.size(152.dp), contentScale = ContentScale.Fit)
                }
            }
            GuideText("Digimon", size = 30, weight = FontWeight.Bold)
        }
    }
}
