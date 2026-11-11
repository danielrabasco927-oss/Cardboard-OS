package com.example.cardboardos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cardboardos.model.PcStreamConfig
import com.example.cardboardos.model.VrDestination
import com.example.cardboardos.model.VrSettings
import com.example.cardboardos.sensors.HeadTracker
import com.example.cardboardos.ui.screens.AppsScreen
import com.example.cardboardos.ui.screens.BrowserScreen
import com.example.cardboardos.ui.screens.GamesScreen
import com.example.cardboardos.ui.screens.HandTrackingScreen
import com.example.cardboardos.ui.screens.HomeScreen
import com.example.cardboardos.ui.screens.PcConnectScreen
import com.example.cardboardos.ui.screens.SettingsScreen
import com.example.cardboardos.ui.screens.UnityGuideDialog
import com.example.cardboardos.ui.screens.VideoCinemaScreen
import com.example.cardboardos.ui.vr.CameraPassthroughView
import com.example.cardboardos.ui.vr.FloatingSpatialPanel
import com.example.cardboardos.ui.vr.PermissionRequestView
import com.example.cardboardos.ui.vr.VrBottomBar
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CardboardOsApp(
    headTracker: HeadTracker,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var currentDestination by remember { mutableStateOf<VrDestination?>(null) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var vrSettings by remember { mutableStateOf(VrSettings()) }
    var pcConfig by remember { mutableStateOf(PcStreamConfig()) }
    var showGuideDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = currentDestination != null) {
        currentDestination = null
    }

    fun triggerHaptic() {
        if (vrSettings.hapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(35)
                }
            } catch (_: Exception) {}
        }
    }

    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { _, dragAmount ->
            headTracker.manualOffset(
                deltaYaw = -dragAmount.x * 0.15f,
                deltaPitch = dragAmount.y * 0.15f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize().then(dragModifier)) {
        if (!hasCameraPermission) {
            // PROMPT CAMERA PERMISSIONS IMMEDIATELY
            PermissionRequestView(
                onPermissionGranted = {
                    hasCameraPermission = true
                }
            )
        } else {
            // LAYER 1: LIVE HARDWARE CAMERA PASSTHROUGH
            CameraPassthroughView(
                isFrontCamera = isFrontCamera,
                hasCameraPermission = true,
                modifier = Modifier.fillMaxSize()
            )

            // CARDBOARD SEPTUM DIVIDER (WHEN STEREOSCOPIC SBS MODE IS ENABLED)
            if (vrSettings.isStereoscopic) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .align(Alignment.Center)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black, NeonCyan.copy(alpha = 0.6f), Color.Black)
                            )
                        )
                )

                // Lens vignette overlay
                if (vrSettings.distortionVignette) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color.Transparent, Color(0x66000000), Color(0xCC000000))
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color.Transparent, Color(0x66000000), Color(0xCC000000))
                                    )
                                )
                        )
                    }
                }
            }

            // LAYER 2: FLOATING VR PANELS (WHEN AN APP IS OPENED)
            AnimatedVisibility(
                visible = currentDestination != null,
                enter = fadeIn() + slideInVertically { -it / 4 },
                exit = fadeOut() + slideOutVertically { -it / 4 },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp)
            ) {
                FloatingSpatialPanel(
                    yawDeg = headTracker.yawDeg,
                    pitchDeg = headTracker.pitchDeg,
                    rollDeg = headTracker.rollDeg,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // FLOATING WINDOW TITLE BAR (MINIMIZE & CLOSE TO SEE CAMERA)
                        Surface(
                            color = CyberSurfaceDark.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    currentDestination?.icon?.let { icon ->
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = currentDestination?.title ?: "",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentDestination?.subtitle ?: "",
                                        color = TextSecondary,
                                        fontSize = 9.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = {
                                            triggerHaptic()
                                            currentDestination = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Ver Cámara", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = {
                                            triggerHaptic()
                                            currentDestination = null
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                                    }
                                }
                            }
                        }

                        // WINDOW CONTENT
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(CyberDarkBg.copy(alpha = 0.88f))
                        ) {
                            when (currentDestination) {
                                VrDestination.HOME -> HomeScreen(
                                    currentDestination = VrDestination.HOME,
                                    onNavigate = { dest ->
                                        triggerHaptic()
                                        currentDestination = dest
                                    },
                                    onOpenGuide = {
                                        triggerHaptic()
                                        showGuideDialog = true
                                    },
                                    hasHardwareSensor = headTracker.hasHardwareSensor
                                )

                                VrDestination.VIDEOS -> VideoCinemaScreen(
                                    yawDeg = headTracker.yawDeg,
                                    pitchDeg = headTracker.pitchDeg
                                )

                                VrDestination.BROWSER -> BrowserScreen(isLeftEye = true)

                                VrDestination.APPS -> AppsScreen()

                                VrDestination.PC_CONNECT -> PcConnectScreen(
                                    config = pcConfig,
                                    onUpdateConfig = { pcConfig = it }
                                )

                                VrDestination.GAMES -> GamesScreen(
                                    config = pcConfig,
                                    onUpdateConfig = { pcConfig = it }
                                )

                                VrDestination.HAND_TRACKING -> HandTrackingScreen(
                                    onNavigateDestination = { destName ->
                                        triggerHaptic()
                                        currentDestination = when (destName) {
                                            "cinema" -> VrDestination.VIDEOS
                                            else -> VrDestination.HOME
                                        }
                                    }
                                )

                                VrDestination.SETTINGS -> SettingsScreen(
                                    settings = vrSettings,
                                    onUpdateSettings = { vrSettings = it },
                                    onRecenter = {
                                        triggerHaptic()
                                        headTracker.recenter()
                                    }
                                )

                                null -> { /* Camera Passthrough pure view */ }
                            }
                        }
                    }
                }
            }

            // LAYER 3: FLOATING BOTTOM BAR
            VrBottomBar(
                currentDestination = currentDestination,
                onSelectDestination = { dest ->
                    triggerHaptic()
                    currentDestination = dest
                },
                isFrontCamera = isFrontCamera,
                onToggleCamera = {
                    triggerHaptic()
                    isFrontCamera = !isFrontCamera
                },
                isStereoscopic = vrSettings.isStereoscopic,
                onToggleVrMode = {
                    triggerHaptic()
                    vrSettings = vrSettings.copy(isStereoscopic = !vrSettings.isStereoscopic)
                },
                onRecenter = {
                    triggerHaptic()
                    headTracker.recenter()
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Unity & APK guide dialog
        if (showGuideDialog) {
            UnityGuideDialog(onDismiss = { showGuideDialog = false })
        }
    }
}
