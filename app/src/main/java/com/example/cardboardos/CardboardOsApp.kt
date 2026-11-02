package com.example.cardboardos

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import com.example.cardboardos.ui.vr.FloatingSpatialPanel
import com.example.cardboardos.ui.vr.StereoscopicContainer
import kotlinx.coroutines.delay

@Composable
fun CardboardOsApp(
    headTracker: HeadTracker,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var currentDestination by remember { mutableStateOf(VrDestination.HOME) }
    var vrSettings by remember { mutableStateOf(VrSettings()) }
    var pcConfig by remember { mutableStateOf(PcStreamConfig()) }
    var showGuideDialog by remember { mutableStateOf(false) }

    // Reticle hover & dwell click states
    var isReticleHovering by remember { mutableStateOf(false) }
    var dwellProgress by remember { mutableFloatStateOf(0f) }

    // Intercept hardware Android back button to return to Home
    BackHandler(enabled = currentDestination != VrDestination.HOME) {
        currentDestination = VrDestination.HOME
    }

    // Trigger haptic pulse on interaction
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

    // Dwell click timer simulation
    LaunchedEffect(isReticleHovering) {
        if (isReticleHovering) {
            val totalSteps = 20
            val delayPerStep = ((vrSettings.dwellTimeSeconds * 1000f) / totalSteps).toLong()
            for (step in 1..totalSteps) {
                delay(delayPerStep)
                dwellProgress = step / totalSteps.toFloat()
            }
            // Trigger activation
            triggerHaptic()
            isReticleHovering = false
            dwellProgress = 0f
        } else {
            dwellProgress = 0f
        }
    }

    // Manual drag gesture fallback when testing without moving the physical phone
    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { _, dragAmount ->
            headTracker.manualOffset(
                deltaYaw = -dragAmount.x * 0.15f,
                deltaPitch = dragAmount.y * 0.15f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize().then(dragModifier)) {
        StereoscopicContainer(
            settings = vrSettings,
            yawDeg = headTracker.yawDeg,
            pitchDeg = headTracker.pitchDeg,
            rollDeg = headTracker.rollDeg,
            isReticleHovering = isReticleHovering,
            dwellProgress = dwellProgress,
            onRecenter = {
                triggerHaptic()
                headTracker.recenter()
            },
            onHome = {
                triggerHaptic()
                currentDestination = VrDestination.HOME
            },
            onToggleVrMode = {
                triggerHaptic()
                vrSettings = vrSettings.copy(isStereoscopic = !vrSettings.isStereoscopic)
            },
            onScreenTriggerClick = {
                triggerHaptic()
            }
        ) { isLeftEye, eyeOffsetPx ->
            FloatingSpatialPanel(
                yawDeg = headTracker.yawDeg,
                pitchDeg = headTracker.pitchDeg,
                rollDeg = headTracker.rollDeg,
                eyeOffsetPx = eyeOffsetPx,
                modifier = Modifier.fillMaxSize()
            ) {
                Crossfade(targetState = currentDestination, label = "vr_destination") { destination ->
                    when (destination) {
                        VrDestination.HOME -> HomeScreen(
                            currentDestination = currentDestination,
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

                        VrDestination.BROWSER -> BrowserScreen(isLeftEye = isLeftEye)

                        VrDestination.APPS -> AppsScreen()

                        VrDestination.PC_CONNECT -> PcConnectScreen(
                            config = pcConfig,
                            onUpdateConfig = { pcConfig = it }
                        )

                        VrDestination.GAMES -> GamesScreen(
                            config = pcConfig,
                            onUpdateConfig = { pcConfig = it }
                        )

                        VrDestination.HAND_TRACKING -> HandTrackingScreen()

                        VrDestination.SETTINGS -> SettingsScreen(
                            settings = vrSettings,
                            onUpdateSettings = { vrSettings = it },
                            onRecenter = {
                                triggerHaptic()
                                headTracker.recenter()
                            }
                        )
                    }
                }
            }
        }

        if (showGuideDialog) {
            UnityGuideDialog(onDismiss = { showGuideDialog = false })
        }
    }
}
