package com.example.cardboardos.ui.vr

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * 3D Holographic Hand Cursor with Laser Pointer Beam and Air Gesture Feedback.
 * Replaces touch interaction inside Cardboard headsets.
 */
@Composable
fun VirtualHandCursor(
    isHovering: Boolean,
    dwellProgress: Float,
    isClickPulse: Boolean,
    lastTriggerSource: String,
    modifier: Modifier = Modifier
) {
    val pulseAnim = remember { Animatable(1f) }

    LaunchedEffect(isClickPulse) {
        if (isClickPulse) {
            pulseAnim.snapTo(1.6f)
            pulseAnim.animateTo(1f, animationSpec = tween(250))
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // CENTER RETICLE & LASER POINTER
        Box(
            modifier = Modifier
                .size(72.dp)
                .offset(y = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val scale = pulseAnim.value

                // 1. Dwell Loading Ring
                if (dwellProgress > 0f) {
                    drawArc(
                        color = NeonPink,
                        startAngle = -90f,
                        sweepAngle = 360f * dwellProgress,
                        useCenter = false,
                        topLeft = Offset(8f, 8f),
                        size = androidx.compose.ui.geometry.Size(size.width - 16f, size.height - 16f),
                        style = Stroke(width = 4.5f, cap = StrokeCap.Round)
                    )
                }

                // 2. Glowing Outer Reticle Ring
                drawCircle(
                    color = if (isClickPulse) NeonPink else NeonCyan.copy(alpha = 0.7f),
                    radius = (20f * scale),
                    center = center,
                    style = Stroke(width = 2.5f)
                )

                // 3. Center Laser Dot
                drawCircle(
                    color = if (isClickPulse) Color.White else NeonPink,
                    radius = 5f * scale,
                    center = center
                )

                // 4. Subtle Crosshairs
                val lineLen = 10f
                drawLine(
                    color = NeonCyan.copy(alpha = 0.5f),
                    start = Offset(center.x - 26f, center.y),
                    end = Offset(center.x - 26f + lineLen, center.y),
                    strokeWidth = 2f
                )
                drawLine(
                    color = NeonCyan.copy(alpha = 0.5f),
                    start = Offset(center.x + 26f - lineLen, center.y),
                    end = Offset(center.x + 26f, center.y),
                    strokeWidth = 2f
                )
            }

            // Holographic Hand Icon floating next to the cursor
            Surface(
                shape = CircleShape,
                color = if (isClickPulse) NeonPink else NeonViolet.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = 24.dp, y = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PanTool,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }

        // TOP HANDS-FREE HELPER PILL
        Surface(
            color = CyberSurfaceDark.copy(alpha = 0.85f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = if (isClickPulse) NeonPink else NeonCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isClickPulse) "¡CLIC ACTIVADO! ($lastTriggerSource)" else "Control por Manos: Pasa la mano frente al visor o mira 1s",
                    color = if (isClickPulse) NeonPink else TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
