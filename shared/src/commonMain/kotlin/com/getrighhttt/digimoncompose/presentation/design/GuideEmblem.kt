package com.getrighhttt.digimoncompose.presentation.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

/**
 * Draws a decorative pixel emblem for the field guide's compact identity bar.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal fun GuideEmblem() {
    val colors = LocalFieldGuideColors.current
    Canvas(
        Modifier.size(34.dp).background(colors.accent, RoundedCornerShape(10.dp)).padding(8.dp),
    ) {
        val cell = size.width / 5
        for (row in 0..4) {
            for (column in 0..4) {
                val outerDiamond = kotlin.math.abs(row - 2) + kotlin.math.abs(column - 2) == 2
                if (outerDiamond || (row == 2 && column == 2)) {
                    drawRect(
                        colors.onAccent,
                        Offset(column * cell, row * cell),
                        Size(cell * 0.8f, cell * 0.8f),
                    )
                }
            }
        }
    }
}
