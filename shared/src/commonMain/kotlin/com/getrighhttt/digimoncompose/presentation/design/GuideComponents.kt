package com.getrighhttt.digimoncompose.presentation.design

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/** Small shared controls keep typography, hit areas, and contrast consistent. */
@Composable
internal fun GuideText(text: String, size: Int = 16, weight: FontWeight = FontWeight.Normal,
    color: Color = LocalFieldGuideColors.current.text, modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE, minLines: Int = 1) {
    BasicText(text, modifier, style = TextStyle(color = color, fontSize = size.sp,
        fontWeight = weight, lineHeight = (size * 1.4f).sp),
        maxLines = maxLines, minLines = minLines, overflow = TextOverflow.Ellipsis)
}

@Composable
internal fun GuideIcon(kind: GuideIconKind, modifier: Modifier = Modifier,
    color: Color = LocalFieldGuideColors.current.accent) {
    Canvas(modifier.size(22.dp)) {
        val w = size.width; val h = size.height; val stroke = 1.8.dp.toPx()
        fun line(a: Offset, b: Offset) = drawLine(color, a, b, stroke, StrokeCap.Round)
        when (kind) {
            GuideIconKind.Search -> {
                drawCircle(color, w * .27f, Offset(w * .43f, h * .43f), style = Stroke(stroke))
                line(Offset(w * .64f, h * .64f), Offset(w * .87f, h * .87f))
            }
            GuideIconKind.Refresh -> {
                drawArc(color, 40f, 285f, false, Offset(w * .17f, h * .17f), Size(w * .66f, h * .66f), style = Stroke(stroke, cap = StrokeCap.Round))
                line(Offset(w * .78f, h * .15f), Offset(w * .84f, h * .4f))
                line(Offset(w * .84f, h * .4f), Offset(w * .61f, h * .35f))
            }
            GuideIconKind.Back, GuideIconKind.Forward -> {
                val reverse = kind == GuideIconKind.Back
                val x = if (reverse) .33f else .67f
                val other = if (reverse) .67f else .33f
                line(Offset(w * other, h * .23f), Offset(w * x, h * .5f))
                line(Offset(w * x, h * .5f), Offset(w * other, h * .77f))
            }
            GuideIconKind.Close -> {
                line(Offset(w * .25f, h * .25f), Offset(w * .75f, h * .75f))
                line(Offset(w * .75f, h * .25f), Offset(w * .25f, h * .75f))
            }
            GuideIconKind.Grid -> for (x in 0..1) for (y in 0..1) {
                drawRoundRect(color, Offset(w * (.14f + x * .4f), h * (.14f + y * .4f)), Size(w * .28f, h * .28f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
            }
        }
    }
}

@Composable
internal fun GuideIconButton(kind: GuideIconKind, label: String, onClick: () -> Unit) {
    val colors = LocalFieldGuideColors.current
    Box(Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(colors.surface)
        .border(1.dp, colors.border, RoundedCornerShape(16.dp))
        .clickable(role = Role.Button, onClick = onClick).semantics { contentDescription = label },
        contentAlignment = Alignment.Center) { GuideIcon(kind) }
}

@Composable
internal fun GuideButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalFieldGuideColors.current
    Box(modifier.defaultMinSize(minHeight = 48.dp).clip(RoundedCornerShape(16.dp))
        .background(colors.accent).clickable(role = Role.Button, onClick = onClick)
        .padding(horizontal = 20.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
        GuideText(text, size = 14, weight = FontWeight.SemiBold, color = colors.onAccent)
    }
}

@Composable
internal fun GuideChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalFieldGuideColors.current
    Box(Modifier.defaultMinSize(minHeight = 48.dp).clip(RoundedCornerShape(16.dp))
        .background(if (selected) colors.accent else colors.surface)
        .border(1.dp, if (selected) colors.accent else colors.border, RoundedCornerShape(16.dp))
        .selectable(selected, role = Role.RadioButton, onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
        GuideText(text, size = 13, weight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) colors.onAccent else colors.secondary)
    }
}

@Composable
internal fun GuideTag(text: String) {
    val colors = LocalFieldGuideColors.current
    Box(Modifier.background(colors.tint, RoundedCornerShape(10.dp)).padding(horizontal = 12.dp, vertical = 7.dp)) {
        GuideText(text, size = 12, weight = FontWeight.Medium, color = colors.accent)
    }
}

@Composable
internal fun GuideLoading(label: String) {
    val colors = LocalFieldGuideColors.current
    val transition = rememberInfiniteTransition()
    val angle = transition.animateFloat(0f, 360f, infiniteRepeatable(tween(1000, easing = LinearEasing)))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Canvas(Modifier.size(20.dp)) {
            drawArc(colors.accent, angle.value, 260f, false, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        }
        GuideText(label, size = 14, color = colors.secondary)
    }
}
