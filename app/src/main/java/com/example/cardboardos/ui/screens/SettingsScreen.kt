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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardboardos.model.VrSettings
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    settings: VrSettings,
    onUpdateSettings: (VrSettings) -> Unit,
    onRecenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profiles = listOf(
        "Google Cardboard V2 (Redmi Note 7)",
        "Google Cardboard V1 (37mm)",
        "VR Box 2.0 (Lentes ajustables)",
        "BoboVR Z4 / Z5",
        "Xiaomi Mi VR Play"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // LEFT COLUMN: OPTICS & HEAD TRACKING
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // OPTICS CARD
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.9f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ÓPTICA Y DISTANCIA INTERPUPILAR (IPD)",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // IPD Slider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Separación IPD entre ojos:", color = TextPrimary, fontSize = 11.sp)
                            Text(text = "${settings.ipdOffsetDp.toInt()} dp", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = settings.ipdOffsetDp,
                            onValueChange = { onUpdateSettings(settings.copy(ipdOffsetDp = it)) },
                            valueRange = -24f..24f,
                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
                            modifier = Modifier.testTag("slider_ipd")
                        )

                        // Distortion Vignette switch
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(text = "Viñeta de lentes Cardboard:", color = TextPrimary, fontSize = 11.sp)
                                Text(text = "Suaviza los bordes simulando lentes físicas", color = TextSecondary, fontSize = 9.sp)
                            }
                            Switch(
                                checked = settings.distortionVignette,
                                onCheckedChange = { onUpdateSettings(settings.copy(distortionVignette = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonViolet)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // TRACKING & SENSORS CARD
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.9f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SENSORES Y SEGUIMIENTO DE CABEZA",
                            color = NeonViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Sensitivity
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Sensibilidad del Giroscopio:", color = TextPrimary, fontSize = 11.sp)
                            Text(text = "%.1fx".format(settings.gyroSensitivity), color = NeonViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = settings.gyroSensitivity,
                            onValueChange = { onUpdateSettings(settings.copy(gyroSensitivity = it)) },
                            valueRange = 0.5f..2.5f,
                            colors = SliderDefaults.colors(thumbColor = NeonViolet, activeTrackColor = NeonViolet),
                            modifier = Modifier.testTag("slider_sensitivity")
                        )

                        // Dwell click timer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Temporizador de clic por mirada (Dwell):", color = TextPrimary, fontSize = 11.sp)
                            Text(text = "%.1fs".format(settings.dwellTimeSeconds), color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = settings.dwellTimeSeconds,
                            onValueChange = { onUpdateSettings(settings.copy(dwellTimeSeconds = it)) },
                            valueRange = 0.6f..2.5f,
                            colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink),
                            modifier = Modifier.testTag("slider_dwell")
                        )
                    }
                }
            }

            // RIGHT COLUMN: PROFILES & RECENTER ACTIONS
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // HEADSET PROFILES
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.9f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "PERFIL DE GAFAS CARDBOARD",
                            color = NeonPink,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        for (profile in profiles) {
                            Surface(
                                color = if (settings.cardboardProfile == profile) NeonViolet.copy(alpha = 0.35f) else CyberSurfaceGlass,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (settings.cardboardProfile == profile) 1.2.dp else 0.5.dp,
                                    if (settings.cardboardProfile == profile) NeonPink else NeonCyan.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onUpdateSettings(settings.copy(cardboardProfile = profile)) }
                            ) {
                                Text(
                                    text = profile,
                                    color = if (settings.cardboardProfile == profile) NeonPink else TextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = if (settings.cardboardProfile == profile) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ACTIONS (Recenter & Reset)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRecenter,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("settings_recenter_btn")
                    ) {
                        Icon(imageVector = Icons.Default.CenterFocusStrong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Centrar Vista 3D", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onUpdateSettings(VrSettings()) },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("settings_reset_btn")
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Restablecer", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
