package com.getrighhttt.digimoncompose.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.getrighhttt.digimoncompose.presentation.design.ArtworkFallback
import com.getrighhttt.digimoncompose.presentation.design.GuideText
import com.getrighhttt.digimoncompose.presentation.design.LocalFieldGuideColors

/**
 * Connects profile artwork, name, and source stages in one field-guide cover.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal fun ProfileCover(
    name: String,
    image: String?,
    levels: List<String>,
) {
    val colors = LocalFieldGuideColors.current
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))) {
        Box(
            Modifier.fillMaxWidth().height(260.dp).background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            SubcomposeAsyncImage(
                model = image,
                contentDescription = "Artwork of $name",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(24.dp),
                loading = { ArtworkFallback(name) },
                error = { ArtworkFallback(name) },
            )
        }
        Column(
            Modifier.fillMaxWidth().background(colors.cover).padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (levels.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.size(6.dp).background(colors.onCover, CircleShape))
                    GuideText(
                        levels.joinToString(" · "),
                        size = 12,
                        weight = FontWeight.Medium,
                        color = colors.onCover,
                    )
                }
            }
            GuideText(name, size = 28, weight = FontWeight.Bold, color = colors.onCover)
        }
    }
}
