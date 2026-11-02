package com.example.cardboardos.ui.vr

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink

/**
 * VR Reticle showing center gaze point and circular dwell click animation.
 */
@Composable
fun GazeReticle(
    isHovering: Boolean,
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(durationMillis = 50)
        )
    }

    Box(
        modifier = modifier.size(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(32.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f - 4f

            // Central pointer dot
            drawCircle(
                color = if (isHovering) NeonPink else NeonCyan,
                radius = if (isHovering) 4.5f else 3f,
                center = center
            )

            // Dwell progress circle
            if (isHovering && animatedProgress.value > 0f) {
                drawArc(
                    color = NeonPink,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress.value,
                    useCenter = false,
                    topLeft = Offset(4f, 4f),
                    size = androidx.compose.ui.geometry.Size(size.width - 8f, size.height - 8f),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )
            } else {
                // Subtle static reticle ring
                drawCircle(
                    color = Color(0x6000F0FF),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }
        }
    }
}
