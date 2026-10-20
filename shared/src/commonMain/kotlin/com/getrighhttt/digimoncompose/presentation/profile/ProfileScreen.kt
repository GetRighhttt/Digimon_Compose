package com.getrighhttt.digimoncompose.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.getrighhttt.digimoncompose.presentation.browse.ArtworkFallback
import com.getrighhttt.digimoncompose.presentation.design.*
import com.getrighhttt.digimoncompose.presentation.profile.model.*

/**
 * Profile sections render only metadata supplied by the source.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal fun ProfileScreen(state: ProfileState, name: String, image: String?, onBack: () -> Unit, onRetry: () -> Unit) {
    val colors = LocalFieldGuideColors.current
    Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GuideIconButton(GuideIconKind.Back, "Back to directory", onBack)
            GuideText("FIELD NOTES", size = 11, weight = FontWeight.Bold, color = colors.secondary, modifier = Modifier.weight(1f))
            state.id?.let { GuideTag("#${it.toString().padStart(4, '0')}") }
        }
        val content = state.content
        LazyColumn(Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Box(Modifier.fillMaxWidth().heightIn(min = 220.dp, max = 320.dp).aspectRatio(1.3f)
                    .clip(RoundedCornerShape(28.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                    SubcomposeAsyncImage(content?.image ?: image, contentDescription = "Artwork of ${content?.name ?: name}",
                        contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(24.dp),
                        loading = { ArtworkFallback(name) }, error = { ArtworkFallback(name) })
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GuideText(content?.name ?: name, size = 32, weight = FontWeight.Bold)
                    if (content != null) FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (content.levels + content.attributes + content.types).distinct().forEach { GuideTag(it) }
                    }
                }
            }
            if (state.loading) item { GuideLoading("Opening field notes…") }
            state.error?.let { error -> item {
                ProfileSection("Couldn't open these notes") {
                    GuideText(when (error) {
                        ProfileError.NotFound -> "This Digimon's profile is no longer available."
                        ProfileError.InvalidResponse -> "The profile returned incomplete data. Please try again."
                        ProfileError.RequestRejected -> "This profile couldn't be requested. Please try again."
                        ProfileError.Unavailable -> "Check your connection and try again."
                    }, size = 14, color = colors.secondary)
                    GuideButton("Try again", onRetry)
                }
            } }
            if (content != null) {
                item { ProfileSection("About ${content.name}") {
                    GuideText(content.description ?: "An English description isn't available for this Digimon.", size = 15, color = colors.secondary)
                } }
                item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatPanel("DEBUT", content.debut ?: "Unknown", Modifier.weight(1f))
                    StatPanel("X-ANTIBODY", when (content.xAntibody) { true -> "Yes"; false -> "No"; null -> "Unknown" }, Modifier.weight(1f))
                } }
                if (content.skills.isNotEmpty()) item {
                    var expanded by rememberSaveable(content.id) { mutableStateOf(false) }
                    ProfileSection("Skills · ${content.skills.size}") {
                        (if (expanded) content.skills else content.skills.take(3)).forEach { skill ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                GuideText(skill.name, size = 15, weight = FontWeight.SemiBold)
                                if (skill.description.isNotBlank()) GuideText(skill.description, size = 14, color = colors.secondary)
                            }
                        }
                        if (content.skills.size > 3) GuideButton(if (expanded) "Show fewer skills" else "See all skills", { expanded = !expanded })
                    }
                }
                if (content.priorCount != null || content.nextCount != null) item { ProfileSection("Evolution relationships") {
                    GuideText("Evolution can branch into different forms and depend on conditions.", size = 14, color = colors.secondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatPanel("PRIOR LINKS", content.priorCount?.toString() ?: "Unknown", Modifier.weight(1f))
                        StatPanel("NEXT LINKS", content.nextCount?.toString() ?: "Unknown", Modifier.weight(1f))
                    }
                } }
            }
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalFieldGuideColors.current
    Column(Modifier.fillMaxWidth().background(colors.surface, RoundedCornerShape(24.dp)).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GuideText(title, size = 18, weight = FontWeight.SemiBold)
        content()
    }
}

@Composable
private fun StatPanel(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = LocalFieldGuideColors.current
    Column(modifier.background(colors.tint, RoundedCornerShape(20.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        GuideText(label, size = 10, weight = FontWeight.Bold, color = colors.secondary)
        GuideText(value, size = 20, weight = FontWeight.SemiBold, color = colors.accent)
    }
}
