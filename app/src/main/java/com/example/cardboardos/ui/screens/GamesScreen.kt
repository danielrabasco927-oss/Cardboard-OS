package com.example.cardboardos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
fun GamesScreen(
    config: PcStreamConfig,
    onUpdateConfig: (PcStreamConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var isStreamingActive by remember { mutableStateOf(config.status == PcConnectionStatus.STREAMING) }
    var selectedGame by remember { mutableStateOf(config.activeGame) }
    var showExplanation by remember { mutableStateOf(false) }

    val gameLibrary = remember {
        listOf(
            "Elden Ring" to "RPG de mundo abierto",
            "Cyberpunk 2077" to "Acción futurista con trazado de rayos",
            "Forza Horizon 5" to "Simulación de conducción",
            "Doom Eternal" to "Shooter de alta velocidad (60fps)",
            "Steam Big Picture" to "Menú principal de tu biblioteca PC",
            "RetroArch PC" to "Emulación multiconsola clásica"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        // VIRTUAL 120" GAMING SCREEN ENVIRONMENT
        Box(modifier = Modifier.fillMaxSize()) {
            // Virtual sci-fi space/theater background
            Image(
                painter = painterResource(id = R.drawable.vr_space_environment_1793643553385),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dimming overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xD0050811))
            )

            // VIRTUAL 120-INCH CURVED PC MONITOR
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .fillMaxHeight(0.68f)
                    .align(Alignment.Center)
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(NeonPink, NeonCyan, NeonViolet)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isStreamingActive) {
                        // Live PC Game stream visualizer
                        Image(
                            painter = painterResource(id = R.drawable.vr_space_environment_1793643553385),
                            contentDescription = "Pantalla de Juego PC",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Streaming HUD Overlay (Top Right of screen)
                        Surface(
                            color = Color(0xAA000000),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(shape = CircleShape, color = NeonGreen, modifier = Modifier.size(8.dp)) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "EN VIVO: 60 FPS • ${config.latencyMs}ms • ${config.bitrateMbps} Mbps • 720p",
                                    color = NeonGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Game title
                        Surface(
                            color = Color(0xAA000000),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = selectedGame,
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        // Disconnected / Standby screen
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xF0070B14))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NeonViolet.copy(alpha = 0.2f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "MONITOR VIRTUAL DE JUEGOS PC (120\")",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Conexión lista con tu PC Windows (${config.pcIp}) mediante Sunshine & Moonlight",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    isStreamingActive = true
                                    onUpdateConfig(config.copy(status = PcConnectionStatus.STREAMING))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("games_start_stream")
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Iniciar Transmisión en Pantalla Gigante", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // TOP CONTROLS & CLARIFICATION MODAL TOGGLE
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = CyberSurfaceGlass,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Gamepad, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Mando Bluetooth: Conectado (Xbox / DualShock)", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Surface(
                color = NeonViolet.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink),
                modifier = Modifier.clickable { showExplanation = !showExplanation }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "¿Cine Virtual vs 3D Estereoscópico?", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // EXPLANATION DIALOG
        if (showExplanation) {
            Surface(
                color = CyberSurfaceDark,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonPink),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.65f)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diferencia Técnica: Pantalla Virtual vs VR 3D Real",
                        color = NeonPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Jugar en Pantalla Virtual (Cardboard OS):\nTu juego de PC se transmite a una pantalla curva gigante de 120\" en tu sala virtual. Es compatible con el 100% de tus juegos (Elden Ring, FIFA, GTA, emuladores) a 60 FPS sin mareos.\n\n2. Realidad Virtual 3D Auténtica (VR Estereoscópica):\nPara que un juego tenga profundidad 3D real y giro de cámara con la cabeza dentro del juego, se necesita un inyector en PC como Trinus VR o VorpX. Los juegos normales no se convierten por arte de magia en juegos 3D reales.",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showExplanation = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Entendido", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // BOTTOM GAME SELECTOR & STREAM ACTIONS
        Surface(
            color = CyberSurfaceGlass,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.9f)
                .padding(bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Game selection chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(gameLibrary) { (title, subtitle) ->
                        Surface(
                            color = if (selectedGame == title) NeonPink.copy(alpha = 0.35f) else CyberSurfaceDark,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                if (selectedGame == title) NeonPink else NeonCyan.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.clickable {
                                selectedGame = title
                                onUpdateConfig(config.copy(activeGame = title))
                            }
                        ) {
                            Text(
                                text = title,
                                color = if (selectedGame == title) NeonPink else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Stream Toggle Action
                Button(
                    onClick = {
                        isStreamingActive = !isStreamingActive
                        onUpdateConfig(
                            config.copy(
                                status = if (isStreamingActive) PcConnectionStatus.STREAMING else PcConnectionStatus.CONNECTED
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isStreamingActive) Color(0xFFDC2626) else NeonCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp).testTag("games_toggle_stream_action")
                ) {
                    Icon(
                        imageVector = if (isStreamingActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isStreamingActive) Color.White else CyberDarkBg
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isStreamingActive) "Detener" else "Transmitir",
                        color = if (isStreamingActive) Color.White else CyberDarkBg,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
