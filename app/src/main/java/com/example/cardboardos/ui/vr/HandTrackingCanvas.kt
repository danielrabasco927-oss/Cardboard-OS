package com.example.cardboardos.ui.vr

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.cardboardos.sensors.HandJoint
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet

/**
 * Renders the 21 MediaPipe skeletal hand joints, finger bones, and pointing laser beam in VR.
 */
@Composable
fun HandTrackingCanvas(
    landmarks: List<HandJoint>,
    isPinching: Boolean,
    modifier: Modifier = Modifier
) {
    if (landmarks.isEmpty()) return

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            fun getOffset(joint: HandJoint): Offset {
                return Offset(joint.x * canvasW, joint.y * canvasH)
            }

            // MediaPipe 21 Hand Connections:
            // Palm: 0-1, 0-5, 0-17, 5-9, 9-13, 13-17
            // Thumb: 1-2, 2-3, 3-4
            // Index: 5-6, 6-7, 7-8
            // Middle: 9-10, 10-11, 11-12
            // Ring: 13-14, 14-15, 15-16
            // Pinky: 17-18, 18-19, 19-20
            val connections = listOf(
                0 to 1, 1 to 2, 2 to 3, 3 to 4,
                0 to 5, 5 to 6, 6 to 7, 7 to 8,
                5 to 9, 9 to 10, 10 to 11, 11 to 12,
                9 to 13, 13 to 14, 14 to 15, 15 to 16,
                13 to 17, 17 to 18, 18 to 19, 19 to 20,
                0 to 17
            )

            // Draw glowing bones
            val boneColor = if (isPinching) NeonPink.copy(alpha = 0.85f) else NeonViolet.copy(alpha = 0.75f)
            for ((from, to) in connections) {
                if (from < landmarks.size && to < landmarks.size) {
                    drawLine(
                        color = boneColor,
                        start = getOffset(landmarks[from]),
                        end = getOffset(landmarks[to]),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw Pointing Laser Ray from Index Tip (Landmark 8)
            if (landmarks.size > 8) {
                val indexTip = getOffset(landmarks[8])
                val indexPip = getOffset(landmarks[7])
                val rayDir = indexTip - indexPip
                val rayEnd = indexTip + (rayDir * 4f)

                drawLine(
                    color = if (isPinching) NeonPink else NeonCyan,
                    start = indexTip,
                    end = rayEnd,
                    strokeWidth = if (isPinching) 6f else 3f,
                    cap = StrokeCap.Round
                )

                // Laser target reticle at ray end
                drawCircle(
                    color = if (isPinching) NeonPink else NeonCyan,
                    radius = if (isPinching) 12f else 6f,
                    center = rayEnd
                )
            }

            // Draw 21 joint nodes
            for ((i, joint) in landmarks.withIndex()) {
                val pos = getOffset(joint)
                val isTip = i in listOf(4, 8, 12, 16, 20)
                val jointColor = when {
                    isPinching && (i == 4 || i == 8) -> NeonPink
                    isTip -> NeonCyan
                    else -> Color.White
                }
                val jointRadius = when {
                    isPinching && (i == 4 || i == 8) -> 9f
                    isTip -> 7f
                    else -> 4.5f
                }

                drawCircle(color = jointColor, radius = jointRadius, center = pos)
            }

            // Pinch feedback ripple
            if (isPinching && landmarks.size > 8) {
                val pinchCenter = (getOffset(landmarks[4]) + getOffset(landmarks[8])) / 2f
                drawCircle(
                    color = NeonPink.copy(alpha = 0.6f),
                    radius = 28f,
                    center = pinchCenter,
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}
