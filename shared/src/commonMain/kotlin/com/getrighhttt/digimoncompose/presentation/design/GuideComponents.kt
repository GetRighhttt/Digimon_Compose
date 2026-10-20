package com.getrighhttt.digimoncompose.presentation.design

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Small shared controls keep typography, hit areas, and contrast consistent.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal fun GuideText(
    text: String,
    size: Int = 16,
    weight: FontWeight = FontWeight.Normal,
    color: Color = LocalFieldGuideColors.current.text,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    BasicText(
        text,
        modifier,
        style =
            TextStyle(
                color = color,
                fontSize = size.sp,
                fontWeight = weight,
                lineHeight = (size * 1.4f).sp,
            ),
        maxLines = maxLines,
        minLines = minLines,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
internal fun GuideIcon(
    kind: GuideIconKind,
    modifier: Modifier = Modifier,
    color: Color = LocalFieldGuideColors.current.accent,
) {
    Canvas(modifier.size(22.dp)) {
        val width = size.width
        val height = size.height
        val stroke = 1.8.dp.toPx()

        fun line(
            start: Offset,
            end: Offset,
        ) = drawLine(color, start, end, stroke, StrokeCap.Round)
        when (kind) {
            GuideIconKind.Search -> {
                drawCircle(
                    color,
                    width * 0.27f,
                    Offset(width * 0.43f, height * 0.43f),
                    style = Stroke(stroke),
                )
                line(Offset(width * 0.64f, height * 0.64f), Offset(width * 0.87f, height * 0.87f))
            }

            GuideIconKind.Refresh -> {
                drawArc(
                    color,
                    40f,
                    285f,
                    false,
                    Offset(width * 0.17f, height * 0.17f),
                    Size(width * 0.66f, height * 0.66f),
                    style = Stroke(stroke, cap = StrokeCap.Round),
                )
                line(Offset(width * 0.78f, height * 0.15f), Offset(width * 0.84f, height * 0.4f))
                line(Offset(width * 0.84f, height * 0.4f), Offset(width * 0.61f, height * 0.35f))
            }

            GuideIconKind.Back,
            GuideIconKind.Forward,
            -> {
                val reverse = kind == GuideIconKind.Back
                val x = if (reverse) 0.33f else 0.67f
                val other = if (reverse) 0.67f else 0.33f
                line(Offset(width * other, height * 0.23f), Offset(width * x, height * 0.5f))
                line(Offset(width * x, height * 0.5f), Offset(width * other, height * 0.77f))
            }

            GuideIconKind.Close -> {
                line(Offset(width * 0.25f, height * 0.25f), Offset(width * 0.75f, height * 0.75f))
                line(Offset(width * 0.75f, height * 0.25f), Offset(width * 0.25f, height * 0.75f))
            }

            GuideIconKind.List -> {
                for (row in 0..2) {
                    val y = height * (0.22f + row * 0.28f)
                    drawCircle(color, stroke, Offset(width * 0.17f, y))
                    line(Offset(width * 0.38f, y), Offset(width * 0.85f, y))
                }
            }

            GuideIconKind.Grid -> {
                for (x in 0..1) {
                    for (y in 0..1) {
                        drawRoundRect(
                            color,
                            Offset(width * (0.14f + x * 0.4f), height * (0.14f + y * 0.4f)),
                            Size(width * 0.28f, height * 0.28f),
                            cornerRadius = CornerRadius(2.dp.toPx()),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun GuideIconButton(
    kind: GuideIconKind,
    label: String,
    onClick: () -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    Box(
        Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        GuideIcon(kind)
    }
}

@Composable
internal fun GuideButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalFieldGuideColors.current
    Box(
        modifier
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.accent)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        GuideText(text, size = 14, weight = FontWeight.SemiBold, color = colors.onAccent)
    }
}

@Composable
internal fun GuideChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    Box(
        Modifier
            .defaultMinSize(minHeight = 48.dp)
            .selectable(selected, role = Role.RadioButton, onClick = onClick)
            .padding(vertical = 5.dp)
            .background(if (selected) colors.accent else colors.tint, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        GuideText(
            text,
            size = 14,
            weight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) colors.onAccent else colors.secondary,
        )
    }
}

@Composable
internal fun GuideTag(text: String) {
    val colors = LocalFieldGuideColors.current
    Box(
        Modifier
            .background(colors.tint, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        GuideText(text, size = 13, weight = FontWeight.Medium, color = colors.accent)
    }
}

@Composable
internal fun GuideLoading(label: String) {
    val colors = LocalFieldGuideColors.current
    val transition = rememberInfiniteTransition()
    val angle =
        transition.animateFloat(0f, 360f, infiniteRepeatable(tween(1000, easing = LinearEasing)))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Canvas(Modifier.size(20.dp)) {
            drawArc(
                colors.accent,
                angle.value,
                260f,
                false,
                style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
            )
        }
        GuideText(label, size = 14, color = colors.secondary)
    }
}
