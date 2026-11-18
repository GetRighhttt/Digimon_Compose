package com.getrighhttt.digimoncompose.presentation.mvilab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.getrighhttt.digimoncompose.presentation.design.GuideButton
import com.getrighhttt.digimoncompose.presentation.design.GuideIconButton
import com.getrighhttt.digimoncompose.presentation.design.GuideIconKind
import com.getrighhttt.digimoncompose.presentation.design.GuideLoading
import com.getrighhttt.digimoncompose.presentation.design.GuideText
import com.getrighhttt.digimoncompose.presentation.design.LocalFieldGuideColors

/**
 * Renders the same sample report controls and states for both frameworks.
 *
 * @author Stefan Bayne
 * @since 2026-11-18
 */
@Composable
internal fun FieldReportScreen(
    framework: String,
    state: ReportState,
    onBack: () -> Unit,
    onLoad: () -> Unit,
    onFail: () -> Unit,
    onRetry: () -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    Column(
        Modifier.fillMaxSize().background(colors.background).safeDrawingPadding(),
    ) {
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GuideIconButton(GuideIconKind.Back, "Back to catalog", onBack)
            GuideText(framework, size = 22, weight = FontWeight.Bold)
        }

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GuideText("Field report", size = 28, weight = FontWeight.Bold)
            GuideText(
                "Load a sample Digimon sighting, or simulate a failed request to try the retry flow.",
                color = colors.secondary,
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (state) {
                    ReportState.Ready -> {
                        GuideText("No report loaded.")
                    }

                    ReportState.Loading -> {
                        GuideLoading("Loading report…")
                    }

                    is ReportState.Loaded -> {
                        GuideText(state.summary)
                    }

                    ReportState.Failed -> {
                        GuideText("The sample request failed.", color = colors.error)
                        GuideButton("Retry", onRetry)
                    }
                }
            }

            if (state != ReportState.Loading) {
                GuideButton("Load report", onLoad, Modifier.fillMaxWidth())
                GuideButton("Simulate failure", onFail, Modifier.fillMaxWidth())
            }
        }
    }
}
