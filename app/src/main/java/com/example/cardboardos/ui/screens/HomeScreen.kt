package com.example.cardboardos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardboardos.model.VrDestination
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    currentDestination: VrDestination,
    onNavigate: (VrDestination) -> Unit,
    onOpenGuide: () -> Unit,
    hasHardwareSensor: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP STATUS BAR (Holographic HUD)
            HudStatusBar(hasHardwareSensor = hasHardwareSensor)

            Spacer(modifier = Modifier.height(12.dp))

            // TITLE BANNER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "CARDBOARD OS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = NeonViolet.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet)
                ) {
                    Text(
                        text = "VR QUEST EXPERIENCE",
                        color = NeonPink,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "Entorno Virtual 3D Optimizado para Redmi Note 7 & Google Cardboard",
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // MAIN VR MENU GRID (Big touch/gaze targets)
            val menuItems = listOf(
                VrDestination.VIDEOS,
                VrDestination.BROWSER,
                VrDestination.APPS,
                VrDestination.PC_CONNECT,
                VrDestination.GAMES,
                VrDestination.HAND_TRACKING,
                VrDestination.SETTINGS
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(menuItems) { dest ->
                    VrMenuCard(
                        destination = dest,
                        isSelected = currentDestination == dest,
                        onClick = { onNavigate(dest) }
                    )
                }

                // Unity / APK Guide Special Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CyberSurfaceDark.copy(alpha = 0.9f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable { onOpenGuide() }
                            .testTag("menu_guide_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NeonPink.copy(alpha = 0.2f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "Guía Unity y APK",
                                    tint = NeonPink,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Guía APK & Unity",
                                color = NeonPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Compilar y Exportar",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VrMenuCard(
    destination: VrDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) NeonPink else NeonCyan.copy(alpha = 0.35f)
    val bgColor = if (isSelected) NeonViolet.copy(alpha = 0.35f) else CyberSurfaceDark.copy(alpha = 0.85f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .testTag("menu_card_${destination.name.lowercase()}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = NeonCyan.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.title,
                    tint = NeonCyan,
                    modifier = Modifier.padding(6.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = destination.title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = destination.subtitle,
                color = TextSecondary,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HudStatusBar(hasHardwareSensor: Boolean) {
    Surface(
        color = CyberSurfaceGlass,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = "Giroscopio",
                    tint = if (hasHardwareSensor) NeonCyan else Color.Yellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (hasHardwareSensor) "Giroscopio 60Hz Activo" else "Sensor Simulado",
                    fontSize = 10.sp,
                    color = TextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Wi-Fi",
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "5 GHz (Baja latencia)",
                    fontSize = 10.sp,
                    color = TextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "FPS",
                    tint = NeonPink,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "60 FPS (Snapdragon 660)",
                    fontSize = 10.sp,
                    color = TextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = "Batería",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "4000 mAh / 85%",
                    fontSize = 10.sp,
                    color = TextPrimary
                )
            }
        }
    }
}
