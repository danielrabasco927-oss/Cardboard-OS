package com.example.cardboardos.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.PI

class HeadTracker(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    var yawDeg by mutableFloatStateOf(0f)
        private set
    var pitchDeg by mutableFloatStateOf(0f)
        private set
    var rollDeg by mutableFloatStateOf(0f)
        private set

    var hasHardwareSensor by mutableStateOf(rotationSensor != null)
        private set

    // Recenter reference offsets
    private var baseYaw = 0f
    private var basePitch = 0f

    // Smoothing & sensitivity
    var sensitivity = 1.0f
    var smoothingAlpha = 0.25f // Low pass filter factor

    private var rawYaw = 0f
    private var rawPitch = 0f
    private var rawRoll = 0f

    private val rotationMatrix = FloatArray(9)
    private val remappedMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    fun start() {
        rotationSensor?.let { sensor ->
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

    fun recenter() {
        baseYaw = rawYaw
        basePitch = rawPitch
        yawDeg = 0f
        pitchDeg = 0f
    }

    fun manualOffset(deltaYaw: Float, deltaPitch: Float) {
        yawDeg = (yawDeg + deltaYaw).coerceIn(-180f, 180f)
        pitchDeg = (pitchDeg + deltaPitch).coerceIn(-60f, 60f)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.values.isEmpty()) return

        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        // Redmi Note 7 is mounted in Landscape mode inside Cardboard
        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            SensorManager.AXIS_X,
            SensorManager.AXIS_Z,
            remappedMatrix
        )

        SensorManager.getOrientation(remappedMatrix, orientationAngles)

        val currentRawYaw = (orientationAngles[0] * 180.0 / PI).toFloat()
        val currentRawPitch = (orientationAngles[1] * 180.0 / PI).toFloat()
        val currentRawRoll = (orientationAngles[2] * 180.0 / PI).toFloat()

        rawYaw = currentRawYaw
        rawPitch = currentRawPitch
        rawRoll = currentRawRoll

        // Relative yaw and pitch after recenter
        var targetYaw = (rawYaw - baseYaw) * sensitivity
        // Wrap yaw -180 to 180
        while (targetYaw > 180f) targetYaw -= 360f
        while (targetYaw < -180f) targetYaw += 360f

        val targetPitch = ((rawPitch - basePitch) * sensitivity).coerceIn(-65f, 65f)
        val targetRoll = rawRoll.coerceIn(-30f, 30f)

        // Low-pass exponential smoothing
        yawDeg += (targetYaw - yawDeg) * smoothingAlpha
        pitchDeg += (targetPitch - pitchDeg) * smoothingAlpha
        rollDeg += (targetRoll - rollDeg) * smoothingAlpha
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}
