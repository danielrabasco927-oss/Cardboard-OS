package com.example.cardboardos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.cardboardos.model.VideoMediaItem
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoCinemaScreen(
    yawDeg: Float,
    pitchDeg: Float,
    modifier: Modifier = Modifier
) {
    var is360Mode by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var progressSeconds by remember { mutableFloatStateOf(42f) }
    var volume by remember { mutableFloatStateOf(0.8f) }
    var screenDistanceScale by remember { mutableFloatStateOf(1.0f) } // 0.8f (cerca) to 1.4f (lejos)
    var screenSizeScale by remember { mutableFloatStateOf(1.0f) } // 0.7f to 1.3f
    var showAdjustments by remember { mutableStateOf(false) }

    val sampleVideos = remember {
        listOf(
            VideoMediaItem(
                id = "1",
                title = "Cosmos 360 — Odisea Espacial",
                author = "Cardboard Studios",
                duration = "12:40",
                is360 = true,
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                description = "Experiencia 360° panorámica inmersiva navegando galaxias y nebulosas estelares."
            ),
            VideoMediaItem(
                id = "2",
                title = "Cine IMAX — Cyberpunk City 2099",
                author = "VR Films",
                duration = "24:15",
                is360 = false,
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                description = "Película de ciencia ficción proyectada en pantalla curva gigante de 150 pulgadas."
            ),
            VideoMediaItem(
                id = "3",
                title = "Big Buck Bunny HD",
                author = "Blender Foundation",
                duration = "09:56",
                is360 = false,
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                description = "Clásico de animación de alta definición en entorno de sala de cine virtual."
            )
        )
    }

    var selectedVideo by remember { mutableStateOf(sampleVideos[1]) }
    var localVideoUri by remember { mutableStateOf<Uri?>(null) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            localVideoUri = uri
            selectedVideo = VideoMediaItem(
                id = "local",
                title = uri.lastPathSegment ?: "Vídeo Local del Móvil",
                author = "Almacenamiento Redmi Note 7",
                duration = "Local",
                is360 = false,
                videoUrl = uri.toString(),
                description = "Vídeo reproducido directamente desde la memoria interna del teléfono."
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        if (is360Mode) {
            // ==========================================
            // MODO 360° / PANORÁMICO
            // ==========================================
            Box(modifier = Modifier.fillMaxSize()) {
                // Background 360 equirectangular sphere image shifted by head yaw & pitch
                Image(
                    painter = painterResource(id = R.drawable.vr_space_environment_1793643553385),
                    contentDescription = "Entorno 360 Grados",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )

                // 360 Mode Floating HUD
                Surface(
                    color = CyberSurfaceGlass,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TravelExplore,
                            contentDescription = "Modo 360",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MODO 360° ACTIVO (Mueve la cabeza para explorar)",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // ==========================================
            // MODO CINE VIRTUAL (SALA DE CINE + PANTALLA CURVA)
            // ==========================================
            Box(modifier = Modifier.fillMaxSize()) {
                // Virtual Theater Hall background
                Image(
                    painter = painterResource(id = R.drawable.vr_cinema_theater_1793643593127),
                    contentDescription = "Sala de Cine Virtual",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark theater ambient lighting vignette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xCC050811),
                                    Color(0x66050811),
                                    Color(0xF0050811)
                                )
                            )
                        )
                )

                // GIANT VIRTUAL IMAX SCREEN
                val screenWidthFraction = (0.75f * screenSizeScale).coerceIn(0.5f, 0.95f)
                val screenHeightFraction = (0.55f * screenSizeScale).coerceIn(0.35f, 0.75f)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth(screenWidthFraction)
                        .fillMaxHeight(screenHeightFraction)
                        .align(Alignment.Center)
                        .border(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(NeonCyan, NeonViolet, NeonPink)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Movie playback simulation canvas
                        Image(
                            painter = painterResource(id = R.drawable.vr_space_environment_1793643553385),
                            contentDescription = "Pantalla de Proyección",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Ambient Glow on screen edge
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0x55000000),
                                            Color(0xAA000000)
                                        )
                                    )
                                )
                        )

                        // Video Title Badge on screen
                        Surface(
                            color = Color(0x99000000),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = selectedVideo.title,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Play/Pause center overlay indicator when tapped
                        if (!isPlaying) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0x99000000),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(54.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = "Pausa",
                                    tint = NeonPink,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // FLOATING CINEMA CONTROLS & DOCK
        // ==========================================
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Adjustment sliders dialog when toggled
            if (showAdjustments) {
                Surface(
                    color = CyberSurfaceGlass,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Ajustes de la Pantalla de Cine",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Tamaño: ${(screenSizeScale * 120).toInt()}\"", color = TextPrimary, fontSize = 10.sp, modifier = Modifier.width(90.dp))
                            Slider(
                                value = screenSizeScale,
                                onValueChange = { screenSizeScale = it },
                                valueRange = 0.7f..1.3f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Volumen: ${(volume * 100).toInt()}%", color = TextPrimary, fontSize = 10.sp, modifier = Modifier.width(90.dp))
                            Slider(
                                value = volume,
                                onValueChange = { volume = it },
                                valueRange = 0f..1f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink)
                            )
                        }
                    }
                }
            }

            // Media control bar
            Surface(
                color = CyberSurfaceGlass,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Play / Pause
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier.size(36.dp).testTag("cinema_play_pause")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                            tint = NeonCyan
                        )
                    }

                    // Seek backward 10s
                    IconButton(
                        onClick = { progressSeconds = (progressSeconds - 10f).coerceAtLeast(0f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FastRewind, contentDescription = "Retroceder 10s", tint = TextPrimary)
                    }

                    // Timeline slider
                    Slider(
                        value = progressSeconds,
                        onValueChange = { progressSeconds = it },
                        valueRange = 0f..180f,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink)
                    )

                    // Seek forward 10s
                    IconButton(
                        onClick = { progressSeconds = (progressSeconds + 10f).coerceAtMost(180f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FastForward, contentDescription = "Avanzar 10s", tint = TextPrimary)
                    }

                    // Toggle 360 vs Cinema Mode
                    Button(
                        onClick = { is360Mode = !is360Mode },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (is360Mode) NeonPink else NeonViolet
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(32.dp).testTag("cinema_toggle_360")
                    ) {
                        Text(
                            text = if (is360Mode) "Modo Cine" else "Modo 360°",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Pick local video from mobile
                    IconButton(
                        onClick = { videoPickerLauncher.launch("video/*") },
                        modifier = Modifier.size(32.dp).testTag("cinema_pick_local")
                    ) {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = "Vídeo local", tint = NeonCyan)
                    }

                    // Adjustments
                    IconButton(
                        onClick = { showAdjustments = !showAdjustments },
                        modifier = Modifier.size(32.dp).testTag("cinema_settings_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = "Ajustar cine", tint = TextPrimary)
                    }
                }
            }
        }
    }
}
