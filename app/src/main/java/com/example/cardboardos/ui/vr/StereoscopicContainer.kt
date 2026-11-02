package com.example.cardboardos.ui.vr

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardboardos.model.VrSettings
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet

/**
 * Renders the Dual-Eye Stereoscopic Viewport (Side-by-Side) for Cardboard
 * or Monoscopic Flat mode with Reticle and Recenter controls.
 */
@Composable
fun StereoscopicContainer(
    settings: VrSettings,
    yawDeg: Float,
    pitchDeg: Float,
    rollDeg: Float,
    isReticleHovering: Boolean,
    dwellProgress: Float,
    onRecenter: () -> Unit,
    onHome: () -> Unit,
    onToggleVrMode: () -> Unit,
    onScreenTriggerClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (isLeftEye: Boolean, eyeOffsetPx: Float) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onScreenTriggerClick
            )
            .testTag("vr_viewport_container")
    ) {
        if (settings.isStereoscopic) {
            // DUAL EYE SIDE-BY-SIDE (SBS) STEREOSCOPIC MODE
            Row(modifier = Modifier.fillMaxSize()) {
                // LEFT EYE VIEWPORT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    content(true, -settings.ipdOffsetDp * 2f)

                    // Left Eye Lens Vignette
                    if (settings.distortionVignette) {
                        LensVignetteOverlay()
                    }

                    // Left Eye Reticle
                    GazeReticle(
                        isHovering = isReticleHovering,
                        progress = dwellProgress,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    EyeBadge(text = "OJO IZQ", modifier = Modifier.align(Alignment.TopStart))
                }

                // CENTER SEPTUM (CARDBOARD DIVIDER LINE)
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black, NeonCyan.copy(alpha = 0.5f), Color.Black)
                            )
                        )
                )

                // RIGHT EYE VIEWPORT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    content(false, settings.ipdOffsetDp * 2f)

                    // Right Eye Lens Vignette
                    if (settings.distortionVignette) {
                        LensVignetteOverlay()
                    }

                    // Right Eye Reticle
                    GazeReticle(
                        isHovering = isReticleHovering,
                        progress = dwellProgress,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    EyeBadge(text = "OJO DER", modifier = Modifier.align(Alignment.TopEnd))
                }
            }
        } else {
            // MONOSCOPIC / PREVIEW MODE (Single Fullscreen)
            Box(modifier = Modifier.fillMaxSize()) {
                content(true, 0f)

                GazeReticle(
                    isHovering = isReticleHovering,
                    progress = dwellProgress,
                    modifier = Modifier.align(Alignment.Center)
                )

                Surface(
                    color = CyberSurfaceGlass,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "MODO VISTA PREVIA (PANTALLA PLANA)",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // FLOATING SYSTEM HUD BAR (Quick actions & recenter)
        Surface(
            color = CyberSurfaceGlass.copy(alpha = 0.85f),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-14).dp)
                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                // Home button
                IconButton(
                    onClick = onHome,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("action_home")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Inicio Cardboard OS",
                        tint = NeonCyan
                    )
                }

                // Recenter View button
                IconButton(
                    onClick = onRecenter,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("action_recenter")
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = "Centrar vista 3D",
                        tint = NeonPink
                    )
                }

                // VR SBS vs Flat Toggle
                IconButton(
                    onClick = onToggleVrMode,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("action_toggle_vr")
                ) {
                    Icon(
                        imageVector = if (settings.isStereoscopic) Icons.Default.ViewInAr else Icons.Default.Visibility,
                        contentDescription = "Cambiar modo Cardboard / Plano",
                        tint = NeonViolet
                    )
                }

                Text(
                    text = if (settings.isStereoscopic) "VR Cardboard" else "Modo Plano",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, end = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun EyeBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0x66000000),
        shape = CircleShape,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = text,
            color = Color(0x88FFFFFF),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun LensVignetteOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color(0x33000000),
                        Color(0xBB000000)
                    )
                )
            )
    )
}
