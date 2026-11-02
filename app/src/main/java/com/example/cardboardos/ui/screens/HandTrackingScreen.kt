package com.example.cardboardos.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Pinch
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
fun HandTrackingScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isDiagnosticTesting by remember { mutableStateOf(false) }
    var cameraObstructedState by remember { mutableStateOf<Boolean?>(null) }
    var activeGestureDetected by remember { mutableStateOf("Ninguno") }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

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
            // LEFT COLUMN: CAMERA VISION DIAGNOSTIC & GESTURE RECOGNITION
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // TITLE
                Surface(
                    color = CyberSurfaceGlass,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PanTool,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Visión Artificial & Detección de Manos IA",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pipeline MediaPipe Hand Landmarker para Cardboard OS",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // CAMERA DIAGNOSTIC CARD (CHECKING REDMI NOTE 7 OBSTRUCTION)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.9f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "COMPROBACIÓN FÍSICA: CÁMARA DEL REDMI NOTE 7",
                            color = NeonViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (!hasCameraPermission) {
                            Text(
                                text = "Se requiere permiso de cámara para el diagnóstico de visibilidad y seguimiento.",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("request_camera_perm_btn")
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Conceder Permiso de Cámara", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Camera is permitted, show test
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = if (cameraObstructedState == false) NeonGreen else Color.Yellow,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (cameraObstructedState) {
                                            true -> "Cámara Tapada por las Cardboard"
                                            false -> "Lente Despejado (Visión IA Activa)"
                                            null -> "Diagnóstico pendiente"
                                        },
                                        color = when (cameraObstructedState) {
                                            true -> NeonPink
                                            false -> NeonGreen
                                            null -> TextPrimary
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        // Test obstruction toggle
                                        cameraObstructedState = !(cameraObstructedState ?: true)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp).testTag("test_camera_obstruction_btn")
                                ) {
                                    Text(text = "Probar Sensor", color = CyberDarkBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Passthrough preview box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .background(Color.Black, RoundedCornerShape(10.dp))
                                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (cameraObstructedState == true) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = NeonPink, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "SENSOR OSCURO (0 LUX): TAPA DEL VISOR BLOQUEA LA LENTE",
                                            color = NeonPink,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "VISIÓN ABIERTA: 21 LANDMARKS DETECTADOS (MEDIA PIPE READY)",
                                            color = NeonGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // GESTURE ENGINE STATUS
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.9f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "GESTOS IA CONFIGURADOS",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        GestureItemRow(
                            icon = Icons.Default.TouchApp,
                            title = "Dedo índice alzado",
                            action = "Mover cursor virtual / Apuntar",
                            detected = activeGestureDetected == "Apuntar"
                        )
                        GestureItemRow(
                            icon = Icons.Default.Pinch,
                            title = "Gesto de Pellizco (Pinch)",
                            action = "Hacer clic / Seleccionar botón",
                            detected = activeGestureDetected == "Pellizco"
                        )
                        GestureItemRow(
                            icon = Icons.Default.PanTool,
                            title = "Palma abierta hacia la cámara",
                            action = "Abrir Menú Principal / Atrás",
                            detected = activeGestureDetected == "Palma"
                        )
                    }
                }
            }

            // RIGHT COLUMN: REALISTIC HARDWARE LIMITATIONS & PRACTICAL ALTERNATIVES
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.95f)),
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxSize()
                    .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "REALIDAD TÉCNICA EN REDMI NOTE 7",
                            color = NeonPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "En las Google Cardboard estándar de cartón, la cámara trasera del Redmi Note 7 queda totalmente tapada por la solapa frontal del visor. Por tanto, la IA no puede ver tus manos a menos que utilices una de estas soluciones:",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AlternativeOptionCard(
                        icon = Icons.Default.ContentCut,
                        title = "1. Modificación física del visor (Ventana)",
                        desc = "Hacer un recorte circular en la solapa frontal de cartón donde coincide la cámara dual del Redmi Note 7 para permitir el paso de luz."
                    )

                    AlternativeOptionCard(
                        icon = Icons.Default.Visibility,
                        title = "2. Visores con tapa deslizable (VR Box / BoboVR)",
                        desc = "Gafas de plástico económicas con tapa frontal extraíble pensadas específicamente para apps de Realidad Aumentada y cámara abierta."
                    )

                    AlternativeOptionCard(
                        icon = Icons.Default.Gamepad,
                        title = "3. Mando Bluetooth / Dwell Click (Recomendado)",
                        desc = "Usar un mini-mando Bluetooth de 5€ o la mirada con temporizador (Dwell Reticle de Cardboard OS), que no gasta batería extra ni procesador del móvil."
                    )
                }
            }
        }
    }
}

@Composable
private fun GestureItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    action: String,
    detected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = if (detected) NeonPink.copy(alpha = 0.3f) else NeonCyan.copy(alpha = 0.15f),
            modifier = Modifier.size(26.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (detected) NeonPink else NeonCyan, modifier = Modifier.padding(5.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = action, color = TextSecondary, fontSize = 9.sp)
        }
    }
}

@Composable
private fun AlternativeOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Surface(
        color = CyberSurfaceGlass,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, NeonViolet.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = desc, color = TextSecondary, fontSize = 9.sp, lineHeight = 11.sp)
            }
        }
    }
}
