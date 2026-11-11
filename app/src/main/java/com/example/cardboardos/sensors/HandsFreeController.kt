package com.example.cardboardos.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Complete Hands-Free & Hand Air Gesture Controller for Google Cardboard.
 * Designed specifically for when the smartphone is inside the headset and the touchscreen cannot be touched.
 *
 * Inputs:
 * 1. Air Hand Wave / Cover Camera Gesture: Waving hand in front of camera triggers an instant click!
 * 2. Headset Tap Detector: A gentle tap on the side of the headset triggers a click.
 * 3. Gaze Dwell Click: Looking at any button for 1 second auto-clicks without touching anything.
 */
class HandsFreeController(
    private val context: Context,
    private val onTriggerClick: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Current focused item ID
    var focusedItemId by mutableStateOf<String?>(null)
    var dwellProgress by mutableFloatStateOf(0f)

    // Visual feedback when an air gesture / tap is triggered
    var lastTriggerSource by mutableStateOf("Listo")
    var isClickAnimationActive by mutableStateOf(false)

    // Air Hand Motion Optical variables
    private var lastAverageLum = -1
    private var lastGestureTime = 0L

    // Accelerometer Tap Detection
    private var lastTapTime = 0L

    fun start() {
        accelerometer?.let { sensor ->
            sensorManager?.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    /**
     * Optical Hand Gesture Analyzer:
     * When the user waves their hand or passes their palm in front of the camera,
     * the sudden shadow / motion triggers an immediate CLICK!
     */
    fun analyzeCameraFrameForHandGesture(image: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastGestureTime < 600) return // Debounce clicks

        try {
            val plane = image.planes[0]
            val buffer = plane.buffer
            val width = image.width
            val height = image.height
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride

            // Sample 16 points across the frame
            var totalLum = 0L
            val stepX = (width / 4).coerceAtLeast(1)
            val stepY = (height / 4).coerceAtLeast(1)

            for (y in 0 until height step stepY) {
                val rowOffset = y * rowStride
                for (x in 0 until width step stepX) {
                    val idx = rowOffset + x * pixelStride
                    if (idx < buffer.limit()) {
                        totalLum += (buffer.get(idx).toInt() and 0xFF)
                    }
                }
            }

            val currentAvgLum = (totalLum / 16).toInt()

            if (lastAverageLum != -1) {
                val delta = abs(currentAvgLum - lastAverageLum)
                // If a hand passes in front, there is a sudden large delta in light!
                if (delta > 28) {
                    lastGestureTime = now
                    lastTriggerSource = "Gesto de Mano en el Aire"
                    triggerClick()
                }
            }

            lastAverageLum = currentAvgLum
        } catch (_: Exception) {}
    }

    /**
     * Physical Headset Tap Detection:
     * When user taps the side of their Cardboard goggles with their hand.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val now = System.currentTimeMillis()
        if (now - lastTapTime < 700) return // Debounce taps

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        // Quick impulse strike on the headset body (between 12 and 35 m/s^2)
        if (magnitude > 14.5f) {
            lastTapTime = now
            lastTriggerSource = "Toque en el Visor"
            triggerClick()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    fun triggerClick() {
        isClickAnimationActive = true
        onTriggerClick()
    }
}
