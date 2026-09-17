package com.masroufi.pro.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

data class DonutSlice(val label: String, val value: Float, val color: Color)

@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    totalAmount: Double,
    centerText: String,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animateSweep by animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "sweep_anim"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            val total = slices.sumOf { it.value.toDouble() }.toFloat().takeIf { it > 0 } ?: 1f
            
            slices.forEach { slice ->
                val sweepAngle = (slice.value / total) * animateSweep
                if (sweepAngle > 0) {
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 2f, // Slight gap
                        useCenter = false,
                        style = Stroke(width = 40f, cap = StrokeCap.Round)
                    )
                }
                startAngle += sweepAngle
            }
        }
        
        Text(
            text = centerText,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
