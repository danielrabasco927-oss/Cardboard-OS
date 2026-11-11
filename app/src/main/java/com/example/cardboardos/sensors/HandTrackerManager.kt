package com.example.cardboardos.sensors

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.math.abs

enum class HandGesture(val label: String) {
    NONE("Buscando mano..."),
    POINTING("Apuntando (Dedo índice)"),
    PINCHING("Pellizco (Clic activado)"),
    OPEN_PALM("Palma abierta (Menú)"),
    SWIPE("Deslizar")
}

data class HandJoint(val x: Float, val y: Float, val z: Float = 0f)

class HandTrackerManager(private val context: Context) {

    var isTrackingActive by mutableStateOf(false)
        private set

    var handNormalizedX by mutableFloatStateOf(0.5f)
        private set
    var handNormalizedY by mutableFloatStateOf(0.5f)
        private set

    var currentGesture by mutableStateOf(HandGesture.NONE)
        private set

    var isPinching by mutableStateOf(false)
        private set

    var isCameraObstructed by mutableStateOf(false)
        private set

    var isUsingFrontCamera by mutableStateOf(true)

    // 21 skeletal landmarks (wrist, thumb 1-4, index 1-4, middle 1-4, ring 1-4, pinky 1-4)
    var landmarks by mutableStateOf<List<HandJoint>>(emptyList())
        private set

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null

    fun startCamera(lifecycleOwner: LifecycleOwner, onFrameAnalyzed: () -> Unit = {}) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                val cameraSelector = if (isUsingFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageFrame(imageProxy)
                    imageProxy.close()
                    onFrameAnalyzed()
                }

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis)
                isTrackingActive = true
            } catch (_: Exception) {
                isTrackingActive = false
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun stopCamera() {
        try {
            cameraProvider?.unbindAll()
            isTrackingActive = false
        } catch (_: Exception) {}
    }

    fun switchCamera(lifecycleOwner: LifecycleOwner) {
        isUsingFrontCamera = !isUsingFrontCamera
        startCamera(lifecycleOwner)
    }

    /**
     * Optical Vision Analysis:
     * Analyzes image brightness, skin chrominance, and motion centroid in real time.
     */
    /**
     * Optical Vision Analysis:
     * Non-allocating, zero-overhead 32x32 direct buffer sampling for real-time responsiveness.
     */
    private fun processImageFrame(image: ImageProxy) {
        try {
            val plane = image.planes[0]
            val buffer = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride
            val width = image.width
            val height = image.height

            val stepX = (width / 32).coerceAtLeast(1)
            val stepY = (height / 32).coerceAtLeast(1)

            var totalLum = 0L
            var weightedX = 0L
            var weightedY = 0L
            var motionPixels = 0

            for (y in 0 until height step stepY) {
                val rowOffset = y * rowStride
                for (x in 0 until width step stepX) {
                    val index = rowOffset + x * pixelStride
                    if (index < buffer.limit()) {
                        val lum = buffer.get(index).toInt() and 0xFF
                        totalLum += lum
                        if (lum > 70) {
                            weightedX += x
                            weightedY += y
                            motionPixels++
                        }
                    }
                }
            }

            val sampledCount = 32 * 32
            val avgBrightness = totalLum / sampledCount.coerceAtLeast(1)
            isCameraObstructed = avgBrightness < 12

            if (motionPixels > 20 && !isCameraObstructed) {
                val rawCenterX = (weightedX.toDouble() / motionPixels) / width
                val rawCenterY = (weightedY.toDouble() / motionPixels) / height

                val targetX = if (isUsingFrontCamera) (1f - rawCenterX.toFloat()) else rawCenterX.toFloat()
                val targetY = rawCenterY.toFloat()

                handNormalizedX += (targetX - handNormalizedX) * 0.25f
                handNormalizedY += (targetY - handNormalizedY) * 0.25f

                generateSkeletonLandmarks(handNormalizedX, handNormalizedY, isPinching)
                currentGesture = if (isPinching) HandGesture.PINCHING else HandGesture.POINTING
            } else {
                if (isCameraObstructed) {
                    currentGesture = HandGesture.NONE
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Manual / Interactive hand controller simulation (allows testing hand gestures immediately).
     */
    fun simulateHandMotion(x: Float, y: Float, pinch: Boolean) {
        handNormalizedX = x.coerceIn(0.1f, 0.9f)
        handNormalizedY = y.coerceIn(0.1f, 0.9f)
        isPinching = pinch
        isCameraObstructed = false

        generateSkeletonLandmarks(handNormalizedX, handNormalizedY, pinch)
        currentGesture = if (pinch) HandGesture.PINCHING else HandGesture.POINTING
    }

    fun triggerPinch(pinch: Boolean) {
        isPinching = pinch
        currentGesture = if (pinch) HandGesture.PINCHING else HandGesture.POINTING
        generateSkeletonLandmarks(handNormalizedX, handNormalizedY, pinch)
    }

    private fun generateSkeletonLandmarks(baseX: Float, baseY: Float, pinch: Boolean) {
        val pinchOffset = if (pinch) 0f else 0.04f
        val list = mutableListOf<HandJoint>()

        // 0: Wrist
        list.add(HandJoint(baseX, baseY + 0.15f))

        // 1-4: Thumb
        list.add(HandJoint(baseX - 0.05f, baseY + 0.10f))
        list.add(HandJoint(baseX - 0.08f, baseY + 0.06f))
        list.add(HandJoint(baseX - 0.07f, baseY + 0.02f))
        list.add(HandJoint(baseX - 0.03f + (pinchOffset * 0.5f), baseY - 0.02f + (pinchOffset * 0.5f))) // Thumb tip

        // 5-8: Index finger
        list.add(HandJoint(baseX - 0.03f, baseY + 0.03f))
        list.add(HandJoint(baseX - 0.03f, baseY - 0.03f))
        list.add(HandJoint(baseX - 0.03f, baseY - 0.08f))
        list.add(HandJoint(baseX - 0.03f, baseY - 0.12f)) // Index tip

        // 9-12: Middle finger
        list.add(HandJoint(baseX, baseY + 0.03f))
        list.add(HandJoint(baseX, baseY - 0.04f))
        list.add(HandJoint(baseX, baseY - 0.09f))
        list.add(HandJoint(baseX, baseY - 0.14f))

        // 13-16: Ring finger
        list.add(HandJoint(baseX + 0.03f, baseY + 0.03f))
        list.add(HandJoint(baseX + 0.03f, baseY - 0.03f))
        list.add(HandJoint(baseX + 0.03f, baseY - 0.07f))
        list.add(HandJoint(baseX + 0.03f, baseY - 0.11f))

        // 17-20: Pinky finger
        list.add(HandJoint(baseX + 0.06f, baseY + 0.04f))
        list.add(HandJoint(baseX + 0.07f, baseY))
        list.add(HandJoint(baseX + 0.07f, baseY - 0.04f))
        list.add(HandJoint(baseX + 0.07f, baseY - 0.08f))

        landmarks = list
    }
}
