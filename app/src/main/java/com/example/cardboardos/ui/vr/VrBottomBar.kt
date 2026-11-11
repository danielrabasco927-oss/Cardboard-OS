package com.example.cardboardos.ui.vr

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardboardos.model.VrDestination
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VrBottomBar(
    currentDestination: VrDestination?,
    onSelectDestination: (VrDestination?) -> Unit,
    focusedItemId: String?,
    isFrontCamera: Boolean,
    onToggleCamera: () -> Unit,
    isStereoscopic: Boolean,
    onToggleVrMode: () -> Unit,
    onRecenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CyberSurfaceDark.copy(alpha = 0.94f),
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("vr_bottom_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val isPassthroughFocused = focusedItemId == "passthrough"
            // CAMERA PASSTHROUGH QUICK ACTION
            Surface(
                color = when {
                    isPassthroughFocused -> NeonPink.copy(alpha = 0.6f)
                    currentDestination == null -> NeonPink.copy(alpha = 0.35f)
                    else -> CyberSurfaceGlass
                },
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    if (isPassthroughFocused) 2.dp else 1.dp,
                    if (isPassthroughFocused || currentDestination == null) NeonPink else NeonCyan.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .clickable { onSelectDestination(null) }
                    .testTag("bottom_bar_passthrough")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Ver Cámara",
                        tint = if (isPassthroughFocused || currentDestination == null) Color.White else NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ver Cámara",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // DESTINATIONS BAR
            val navItems = listOf(
                VrDestination.HOME,
                VrDestination.VIDEOS,
                VrDestination.BROWSER,
                VrDestination.APPS,
                VrDestination.PC_CONNECT,
                VrDestination.GAMES,
                VrDestination.SETTINGS
            )

            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(navItems) { dest ->
                    val isSelected = currentDestination == dest
                    val isFocused = focusedItemId == dest.name.lowercase()

                    Surface(
                        color = when {
                            isFocused -> NeonPink.copy(alpha = 0.5f)
                            isSelected -> NeonViolet.copy(alpha = 0.5f)
                            else -> Color.Transparent
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isFocused) 2.dp else if (isSelected) 1.2.dp else 0.5.dp,
                            if (isFocused) NeonPink else if (isSelected) NeonCyan else NeonCyan.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .clickable {
                                if (isSelected) {
                                    onSelectDestination(null)
                                } else {
                                    onSelectDestination(dest)
                                }
                            }
                            .testTag("nav_btn_${dest.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = dest.title,
                                tint = if (isFocused) Color.White else if (isSelected) NeonPink else NeonCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dest.title,
                                color = if (isFocused || isSelected) TextPrimary else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isFocused || isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // SYSTEM ACTION TOOLS: FLIP CAMERA, VR/FLAT TOGGLE, RECENTER
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Switch Camera (Front / Back)
                val isCamFocused = focusedItemId == "flip_cam"
                Surface(
                    color = if (isCamFocused) NeonPink.copy(alpha = 0.4f) else Color.Transparent,
                    shape = CircleShape,
                    border = if (isCamFocused) androidx.compose.foundation.BorderStroke(1.5.dp, NeonPink) else null
                ) {
                    IconButton(
                        onClick = onToggleCamera,
                        modifier = Modifier.size(32.dp).testTag("action_flip_cam")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Cambiar Cámara Delantera/Trasera",
                            tint = if (isCamFocused) Color.White else NeonCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Recenter orientation
                val isRecenterFocused = focusedItemId == "recenter"
                Surface(
                    color = if (isRecenterFocused) NeonPink.copy(alpha = 0.4f) else Color.Transparent,
                    shape = CircleShape,
                    border = if (isRecenterFocused) androidx.compose.foundation.BorderStroke(1.5.dp, NeonPink) else null
                ) {
                    IconButton(
                        onClick = onRecenter,
                        modifier = Modifier.size(32.dp).testTag("action_recenter_bar")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "Centrar Vista",
                            tint = if (isRecenterFocused) Color.White else NeonPink,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // VR Dual Screen vs Flat Screen Toggle
                val isVrFocused = focusedItemId == "toggle_vr"
                Surface(
                    color = if (isVrFocused) NeonPink.copy(alpha = 0.4f) else Color.Transparent,
                    shape = CircleShape,
                    border = if (isVrFocused) androidx.compose.foundation.BorderStroke(1.5.dp, NeonPink) else null
                ) {
                    IconButton(
                        onClick = onToggleVrMode,
                        modifier = Modifier.size(32.dp).testTag("action_toggle_vr_bar")
                    ) {
                        Icon(
                            imageVector = if (isStereoscopic) Icons.Default.ViewInAr else Icons.Default.Visibility,
                            contentDescription = "Modo Cardboard / Plano",
                            tint = if (isVrFocused) Color.White else NeonViolet,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
