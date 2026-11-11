package com.example.cardboardos.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Pinch
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cardboardos.sensors.HandGesture
import com.example.cardboardos.sensors.HandTrackerManager
import com.example.cardboardos.ui.vr.HandTrackingCanvas
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
    onNavigateDestination: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    val handManager = remember { HandTrackerManager(context) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hitCount by remember { mutableIntStateOf(0) }
    var activeTargetHovered by remember { mutableStateOf<String?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            handManager.startCamera(lifecycleOwner)
        }
    }

    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            handManager.startCamera(lifecycleOwner)
        } else {
            // Initialize default interactive skeleton for immediate testing
            handManager.simulateHandMotion(0.45f, 0.5f, false)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            handManager.stopCamera()
        }
    }

    fun triggerHapticClick() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // LEFT COLUMN: INTERACTIVE HAND TRACKING PLAYGROUND
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxSize()
            ) {
                // TOP CONTROLS & CAMERA SWITCH
                Surface(
                    color = CyberSurfaceGlass,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (handManager.isTrackingActive) NeonGreen.copy(alpha = 0.2f) else NeonPink.copy(alpha = 0.2f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PanTool,
                                    contentDescription = null,
                                    tint = if (handManager.isTrackingActive) NeonGreen else NeonPink,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "SEGUIMIENTO ÓPTICO ACTIVO",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (handManager.isUsingFrontCamera) "Cámara Delantera (Prueba en mano)" else "Cámara Trasera (Visor Cardboard)",
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Flip camera button
                            Button(
                                onClick = {
                                    if (hasCameraPermission) {
                                        handManager.switchCamera(lifecycleOwner)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp).testTag("flip_camera_btn")
                            ) {
                                Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Cambiar Cámara", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Pinch Click Trigger button
                            Button(
                                onClick = {
                                    triggerHapticClick()
                                    handManager.triggerPinch(true)
                                    hitCount++
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp).testTag("pinch_action_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Pinch, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Pellizco / Clic", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // INTERACTIVE HAND SKELETON CANVAS & TARGET TEST ZONE
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(listOf(NeonCyan, NeonViolet, NeonPink)),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        handManager.simulateHandMotion(
                                            offset.x / size.width,
                                            offset.y / size.height,
                                            false
                                        )
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        handManager.simulateHandMotion(
                                            change.position.x / size.width,
                                            change.position.y / size.height,
                                            false
                                        )
                                    },
                                    onDragEnd = {
                                        handManager.simulateHandMotion(
                                            handManager.handNormalizedX,
                                            handManager.handNormalizedY,
                                            false
                                        )
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    triggerHapticClick()
                                    handManager.simulateHandMotion(
                                        offset.x / size.width,
                                        offset.y / size.height,
                                        true
                                    )
                                    hitCount++
                                }
                            }
                    ) {
                        // Background cyber grid lines
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        listOf(NeonViolet.copy(alpha = 0.15f), Color.Transparent)
                                    )
                                )
                        )

                        // 4 INTERACTIVE TARGET ORBS FOR TESTING HAND CLICKS
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TargetOrb(
                                title = "Orb Neón 1",
                                color = NeonCyan,
                                onClick = {
                                    triggerHapticClick()
                                    hitCount++
                                }
                            )

                            TargetOrb(
                                title = "Orb Neón 2",
                                color = NeonPink,
                                onClick = {
                                    triggerHapticClick()
                                    hitCount++
                                }
                            )

                            TargetOrb(
                                title = "Pellizco Test",
                                color = NeonViolet,
                                onClick = {
                                    triggerHapticClick()
                                    hitCount++
                                }
                            )
                        }

                        // 21 MEDIAPIPE SKELETAL HAND DRAWING (JOINTS + BONES + LASER RAY)
                        HandTrackingCanvas(
                            landmarks = handManager.landmarks,
                            isPinching = handManager.isPinching,
                            modifier = Modifier.fillMaxSize()
                        )

                        // GESTURE HUD BADGE OVERLAY
                        Surface(
                            color = Color(0xCC050811),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(
                                    text = "GESTO IA: ${handManager.currentGesture.label}",
                                    color = if (handManager.isPinching) NeonPink else NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Objetivos acertados con la mano: $hitCount",
                                    color = TextPrimary,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        // INSTRUCTION TIP FOR USER
                        Surface(
                            color = Color(0xAA000000),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Mueve tu mano ante la cámara o arrastra en pantalla",
                                color = TextSecondary,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // RIGHT COLUMN: PHYSICAL REALITY CHECK & TROUBLESHOOTING
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.95f)),
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxSize()
                    .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                            text = "¿CÓMO FUNCIONA EL SEGUIMIENTO?",
                            color = NeonPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (handManager.isCameraObstructed) {
                        Surface(
                            color = NeonPink.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "¡Atención! La cámara está tapada por las Cardboard. Usa la cámara frontal para probar en mano.",
                                    color = NeonPink,
                                    fontSize = 9.sp,
                                    lineHeight = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = "1. Prueba en la mano (Cámara Frontal):\nPulsa 'Cambiar Cámara' para usar la frontal. Pon tu mano abierta a unos 30 cm de la pantalla: verás el esqueleto 3D con los 21 puntos neón y el láser de apuntado.",
                        color = TextPrimary,
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "2. En unas gafas Cardboard reales:\nLas gafas normales de cartón tapan la cámara trasera del Redmi Note 7. Si deseas usar manos dentro del visor, haz un recorte circular en la solapa de cartón justo donde se sitúa la cámara.",
                        color = TextPrimary,
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "3. Gesto de Pellizco (Pinch Click):\nJunta el pulgar con el índice para simular el clic de Meta Quest, o pulsa el botón 'Pellizco / Clic' que ves arriba.",
                        color = TextPrimary,
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetOrb(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        color = color.copy(alpha = 0.25f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("orb_${title.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = color, modifier = Modifier.size(10.dp)) {}
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
