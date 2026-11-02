package com.example.cardboardos.ui.vr

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet

/**
 * 3D Floating Spatial Panel with cyber glassmorphic aesthetics and head-relative perspective.
 */
@Composable
fun FloatingSpatialPanel(
    yawDeg: Float,
    pitchDeg: Float,
    rollDeg: Float,
    modifier: Modifier = Modifier,
    eyeOffsetPx: Float = 0f,
    content: @Composable BoxScope.() -> Unit
) {
    // Spatial parallax compensation so panel stays fixed in virtual 3D space
    val animatedYaw by animateFloatAsState(targetValue = yawDeg, animationSpec = spring(), label = "yaw")
    val animatedPitch by animateFloatAsState(targetValue = pitchDeg, animationSpec = spring(), label = "pitch")

    Box(
        modifier = modifier
            .graphicsLayer {
                // Virtual camera matrix transform
                translationX = -animatedYaw * 14f + eyeOffsetPx
                translationY = animatedPitch * 14f
                rotationZ = -rollDeg * 0.4f
                rotationY = (-animatedYaw * 0.6f).coerceIn(-45f, 45f)
                rotationX = (animatedPitch * 0.6f).coerceIn(-35f, 35f)
                cameraDistance = 14f * density
            }
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = NeonCyan,
                ambientColor = NeonViolet
            )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CyberSurfaceGlass,
            modifier = Modifier
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.8f),
                            NeonViolet.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            content()
        }
    }
}
