package com.example.cardboardos.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

enum class VrDestination(
    val title: String,
    val icon: ImageVector,
    val subtitle: String
) {
    HOME("Inicio", Icons.Default.Home, "Escritorio virtual 3D"),
    VIDEOS("Vídeos", Icons.Default.Movie, "Cine virtual y vídeos 360°"),
    BROWSER("Navegador", Icons.Default.Public, "Navegar por Internet"),
    APPS("Aplicaciones", Icons.Default.Apps, "Accesos apps compatibles"),
    PC_CONNECT("Conectar PC", Icons.Default.Computer, "Sunshine, Moonlight y Steam"),
    GAMES("Juegos", Icons.Default.SportsEsports, "Juegos PC por streaming"),
    HAND_TRACKING("Control Manos", Icons.Default.PanTool, "Visión IA y gestos"),
    SETTINGS("Ajustes", Icons.Default.Tune, "Visor, IPD y rendimiento")
}

data class VrSettings(
    val isStereoscopic: Boolean = true,
    val ipdOffsetDp: Float = 0f, // -24dp to +24dp
    val gyroSensitivity: Float = 1.0f,
    val gyroSmoothing: Float = 0.25f,
    val dwellTimeSeconds: Float = 1.2f,
    val distortionVignette: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val cinemaDistanceScale: Float = 1.0f,
    val cinemaCurvature: Float = 0.4f,
    val cardboardProfile: String = "Google Cardboard V2 (Redmi Note 7)"
)

enum class PcConnectionStatus(val label: String) {
    DISCONNECTED("Desconectado"),
    SCANNING("Escaneando red local..."),
    PAIRING("Emparejando PIN..."),
    CONNECTED("Conectado"),
    STREAMING("Transmitiendo en vivo")
}

data class PcStreamConfig(
    val pcIp: String = "192.168.1.100",
    val port: Int = 47989,
    val status: PcConnectionStatus = PcConnectionStatus.DISCONNECTED,
    val pairingPin: String = "4829",
    val resolution: String = "720p 60fps (Óptimo Redmi Note 7)",
    val bitrateMbps: Int = 12,
    val latencyMs: Int = 16,
    val activeGame: String = "Elden Ring / Steam"
)

data class VideoMediaItem(
    val id: String,
    val title: String,
    val author: String,
    val duration: String,
    val is360: Boolean,
    val videoUrl: String,
    val description: String
)
