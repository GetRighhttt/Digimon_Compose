package com.getrighhttt.digimoncompose.presentation.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.getrighhttt.digimoncompose.presentation.browse.model.DigimonCard
import com.getrighhttt.digimoncompose.presentation.design.ArtworkFallback
import com.getrighhttt.digimoncompose.presentation.design.GuideIcon
import com.getrighhttt.digimoncompose.presentation.design.GuideIconKind
import com.getrighhttt.digimoncompose.presentation.design.GuideText
import com.getrighhttt.digimoncompose.presentation.design.LocalFieldGuideColors

/**
 * Presents a catalog entry as an artwork card or a compact, accessible row.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal fun DigimonEntry(
    card: DigimonCard,
    compact: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    val shape = RoundedCornerShape(18.dp)
    val modifier =
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, colors.border, shape)
            .clickable(role = Role.Button, onClickLabel = "Open ${card.name}", onClick = onClick)

    if (compact) {
        Row(
            modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EntryArtwork(card, Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)))
            EntryName(card, Modifier.weight(1f))
            GuideIcon(GuideIconKind.Forward, Modifier.padding(end = 8.dp).size(18.dp))
        }
    } else {
        Column(modifier) {
            EntryArtwork(card, Modifier.fillMaxWidth().aspectRatio(1.12f))
            Row(
                Modifier.fillMaxWidth().background(colors.tint.copy(alpha = 0.45f)).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                EntryName(card, Modifier.weight(1f), reserveNameSpace = true)
                GuideIcon(GuideIconKind.Forward, Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun EntryArtwork(
    card: DigimonCard,
    modifier: Modifier,
) {
    // Source artwork can contain a white background, including in dark appearance.
    Box(modifier.background(Color.White)) {
        SubcomposeAsyncImage(
            card.image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().padding(12.dp),
            loading = { ArtworkFallback(card.name) },
            error = { ArtworkFallback(card.name) },
        )
    }
}

@Composable
private fun EntryName(
    card: DigimonCard,
    modifier: Modifier,
    reserveNameSpace: Boolean = false,
) {
    val colors = LocalFieldGuideColors.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        GuideText(
            "NO. ${card.id.toString().padStart(4, '0')}",
            size = 10,
            weight = FontWeight.Bold,
            color = colors.secondary,
        )
        GuideText(
            card.name,
            size = 15,
            weight = FontWeight.SemiBold,
            minLines = if (reserveNameSpace) 2 else 1,
            maxLines = 2,
        )
    }
}
