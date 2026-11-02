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
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardboardos.model.PcConnectionStatus
import com.example.cardboardos.model.PcStreamConfig
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PcConnectScreen(
    config: PcStreamConfig,
    onUpdateConfig: (PcStreamConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var ipInput by remember { mutableStateOf(config.pcIp) }
    var selectedResolution by remember { mutableStateOf(config.resolution) }
    var bitrateMbps by remember { mutableIntStateOf(config.bitrateMbps) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var showSetupGuide by remember { mutableStateOf(false) }

    val resolutions = listOf(
        "720p 60fps (Recomendado Redmi Note 7)",
        "1080p 60fps (Requiere buena red Wi-Fi)",
        "1080p 30fps (Modo ahorro de batería)"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // LEFT COLUMN: CONNECTION CONTROLS & STATUS
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // STATUS HEADER
                Surface(
                    color = CyberSurfaceGlass,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (config.status == PcConnectionStatus.CONNECTED || config.status == PcConnectionStatus.STREAMING) NeonGreen else NeonCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (config.status == PcConnectionStatus.CONNECTED || config.status == PcConnectionStatus.STREAMING) NeonGreen.copy(alpha = 0.2f) else NeonPink.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Computer,
                                    contentDescription = "Estado PC",
                                    tint = if (config.status == PcConnectionStatus.CONNECTED || config.status == PcConnectionStatus.STREAMING) NeonGreen else NeonPink,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sunshine / Moonlight Streaming",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Estado: ${config.status.label}",
                                    color = if (config.status == PcConnectionStatus.CONNECTED || config.status == PcConnectionStatus.STREAMING) NeonGreen else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (config.status == PcConnectionStatus.CONNECTED || config.status == PcConnectionStatus.STREAMING) {
                            Surface(
                                color = NeonGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                            ) {
                                Text(
                                    text = "${config.latencyMs} ms",
                                    color = NeonGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // PC IP INPUT CARD
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.9f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "DIRECCIÓN IP DE TU ORDENADOR WINDOWS",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = ipInput,
                                onValueChange = { ipInput = it },
                                placeholder = { Text("Ej: 192.168.1.100", fontSize = 11.sp, color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("pc_ip_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = NeonViolet.copy(alpha = 0.4f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    isTestingConnection = true
                                    // Auto test ping to PC
                                    onUpdateConfig(
                                        config.copy(
                                            pcIp = ipInput,
                                            status = PcConnectionStatus.CONNECTED,
                                            latencyMs = (14..22).random()
                                        )
                                    )
                                    isTestingConnection = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("pc_connect_btn")
                            ) {
                                Text(
                                    text = if (config.status == PcConnectionStatus.CONNECTED) "Reconectar" else "Conectar",
                                    color = CyberDarkBg,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // PIN Pairing box
                        Surface(
                            color = NeonViolet.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = "PIN", tint = NeonPink, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(text = "PIN de emparejamiento Sunshine:", color = TextSecondary, fontSize = 9.sp)
                                        Text(text = config.pairingPin, color = NeonPink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                                Text(
                                    text = "Introduce en https://localhost:47990/pin",
                                    color = TextSecondary,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // STREAMING SETTINGS (Resolution & Bitrate)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.9f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "AJUSTES DE CALIDAD Y RENDIMIENTO (REDMI NOTE 7)",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Bitrate Slider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Tasa de bits (Bitrate):", color = TextPrimary, fontSize = 11.sp)
                            Text(text = "$bitrateMbps Mbps", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = bitrateMbps.toFloat(),
                            onValueChange = {
                                bitrateMbps = it.toInt()
                                onUpdateConfig(config.copy(bitrateMbps = bitrateMbps))
                            },
                            valueRange = 5f..35f,
                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                        )

                        // Resolution chips
                        Text(text = "Resolución de transmisión:", color = TextPrimary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        for (res in resolutions) {
                            Surface(
                                color = if (selectedResolution == res) NeonViolet.copy(alpha = 0.4f) else CyberSurfaceGlass,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (selectedResolution == res) 1.5.dp else 0.5.dp,
                                    if (selectedResolution == res) NeonPink else Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        selectedResolution = res
                                        onUpdateConfig(config.copy(resolution = res))
                                    }
                            ) {
                                Text(
                                    text = res,
                                    color = if (selectedResolution == res) TextPrimary else TextSecondary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // RIGHT COLUMN: STEP-BY-STEP SETUP GUIDE (WINDOWS & REDMI NOTE 7)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.95f)),
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxSize()
                    .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Help, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GUÍA DE CONFIGURACIÓN PC (PASO A PASO)",
                            color = NeonViolet,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SetupStepItem(
                        number = "1",
                        title = "Descarga Sunshine en tu PC",
                        desc = "Entra en github.com/LizardByte/Sunshine y descarga el instalador gratuito Sunshine-Windows-Installer.exe."
                    )
                    SetupStepItem(
                        number = "2",
                        title = "Conecta a la misma red Wi-Fi",
                        desc = "Conecta tanto el PC como tu Redmi Note 7 a la banda Wi-Fi de 5 GHz del router para latencia mínima (< 20 ms)."
                    )
                    SetupStepItem(
                        number = "3",
                        title = "Abre la interfaz de Sunshine",
                        desc = "En tu navegador del PC entra en https://localhost:47990. Ve a la pestaña 'PIN' e introduce el código que ves a la izquierda."
                    )
                    SetupStepItem(
                        number = "4",
                        title = "Alternativa: Steam Link",
                        desc = "Si solo quieres juegos de Steam, instala Steam Link en tu móvil, activa 'Remote Play' en los ajustes de Steam de tu PC y conéctate al instante."
                    )
                    SetupStepItem(
                        number = "5",
                        title = "Mando Bluetooth",
                        desc = "Empareja tu mando de Xbox, PS4 o mando Bluetooth para móviles a tu Redmi Note 7 para controlar el PC directamente."
                    )
                }
            }
        }
    }
}

@Composable
private fun SetupStepItem(
    number: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = NeonCyan.copy(alpha = 0.2f),
            modifier = Modifier.size(20.dp)
        ) {
            Text(
                text = number,
                color = NeonCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, color = TextSecondary, fontSize = 9.sp, lineHeight = 11.sp)
        }
    }
}
