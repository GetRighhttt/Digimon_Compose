package com.getrighhttt.digimoncompose.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.getrighhttt.digimoncompose.presentation.design.GuideButton
import com.getrighhttt.digimoncompose.presentation.design.GuideIconButton
import com.getrighhttt.digimoncompose.presentation.design.GuideIconKind
import com.getrighhttt.digimoncompose.presentation.design.GuideLoading
import com.getrighhttt.digimoncompose.presentation.design.GuideTag
import com.getrighhttt.digimoncompose.presentation.design.GuideText
import com.getrighhttt.digimoncompose.presentation.design.LocalFieldGuideColors
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileContent
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileError
import com.getrighhttt.digimoncompose.presentation.profile.model.ProfileState

/**
 * Profile sections render only metadata supplied by the source.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal fun ProfileScreen(
    state: ProfileState,
    name: String,
    image: String?,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    val content = state.content

    Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding()) {
        ProfileHeader(state.id, onBack)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                ProfileCover(
                    name = content?.name ?: name,
                    image = content?.image ?: image,
                    levels = content?.levels.orEmpty(),
                )
            }
            val classifications =
                (content?.attributes.orEmpty() + content?.types.orEmpty())
                    .distinct()
            if (classifications.isNotEmpty()) {
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        classifications.forEach { GuideTag(it) }
                    }
                }
            }

            if (state.loading) {
                item { GuideLoading("Opening field notes…") }
            }
            state.error?.let { error ->
                item {
                    ProfileSection("Couldn't open these notes") {
                        GuideText(errorText(error), size = 14, color = colors.secondary)
                        GuideButton("Try again", onRetry)
                    }
                }
            }

            if (content != null) {
                item { ProfileFacts(content) }
                item {
                    ProfileSection("Overview") {
                        GuideText(
                            content.description ?: MISSING_DESCRIPTION,
                            size = 17,
                            color = colors.secondary,
                        )
                    }
                }
                if (content.skills.isNotEmpty()) {
                    item { ProfileSkills(content) }
                }
                if (content.priorCount != null || content.nextCount != null) {
                    item { ProfileEvolutions(content) }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    id: Int?,
    onBack: () -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GuideIconButton(GuideIconKind.Back, "Back to directory", onBack)
        GuideText(
            "Field notes",
            size = 18,
            weight = FontWeight.Bold,
            color = colors.text,
            modifier = Modifier.weight(1f),
        )
        id?.let { GuideTag("#${it.toString().padStart(4, '0')}") }
    }
}

@Composable
private fun ProfileFacts(content: ProfileContent) {
    val antibodyStatus =
        when (content.xAntibody) {
            true -> "Yes"
            false -> "No"
            null -> "Unknown"
        }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatPanel("DEBUT", content.debut ?: "Unknown", Modifier.weight(1f))
        StatPanel("X-ANTIBODY", antibodyStatus, Modifier.weight(1f))
    }
}

@Composable
private fun ProfileSkills(content: ProfileContent) {
    val colors = LocalFieldGuideColors.current
    var expanded by rememberSaveable(content.id) { mutableStateOf(false) }
    val visibleSkills = if (expanded) content.skills else content.skills.take(3)

    ProfileSection("Skills · ${content.skills.size}") {
        visibleSkills.forEach { skill ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                GuideText(skill.name, size = 17, weight = FontWeight.SemiBold)
                if (skill.description.isNotBlank()) {
                    GuideText(skill.description, size = 16, color = colors.secondary)
                }
            }
        }
        if (content.skills.size > 3) {
            GuideButton(
                text = if (expanded) "Show fewer skills" else "See all skills",
                onClick = { expanded = !expanded },
            )
        }
    }
}

@Composable
private fun ProfileEvolutions(content: ProfileContent) {
    val colors = LocalFieldGuideColors.current
    ProfileSection("Evolution relationships") {
        GuideText(
            "Evolution can branch into different forms and depend on conditions.",
            size = 14,
            color = colors.secondary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatPanel(
                "PRIOR LINKS",
                content.priorCount?.toString() ?: "Unknown",
                Modifier.weight(1f),
            )
            StatPanel("NEXT LINKS", content.nextCount?.toString() ?: "Unknown", Modifier.weight(1f))
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GuideText(title, size = 18, weight = FontWeight.Bold)
        Box(
            Modifier.fillMaxWidth().background(colors.border).padding(top = 1.dp),
        )
        content()
    }
}

@Composable
private fun StatPanel(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalFieldGuideColors.current
    Column(
        modifier = modifier.background(colors.surface, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        GuideText(label, size = 11, weight = FontWeight.Bold, color = colors.secondary)
        GuideText(value, size = 20, weight = FontWeight.SemiBold, color = colors.accent)
    }
}

private fun errorText(error: ProfileError): String =
    when (error) {
        ProfileError.NotFound -> "This Digimon's profile is no longer available."
        ProfileError.InvalidResponse -> "The profile returned incomplete data. Please try again."
        ProfileError.RequestRejected -> "This profile couldn't be requested. Please try again."
        ProfileError.Unavailable -> "Check your connection and try again."
    }

private const val MISSING_DESCRIPTION =
    "An English description isn't available for this Digimon."
