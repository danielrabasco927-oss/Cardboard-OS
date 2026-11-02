package com.example

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.cardboardos.CardboardOsApp
import com.example.cardboardos.sensors.HeadTracker
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var headTracker: HeadTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep screen awake while mounted inside Google Cardboard headset
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Post system bars hiding so WindowManager finishes initial SurfaceSyncGroup transaction
        window.decorView.post {
            hideSystemUi()
        }

        headTracker = HeadTracker(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CyberDarkBg
                ) {
                    CardboardOsApp(headTracker = headTracker)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.post {
            hideSystemUi()
        }
        headTracker.start()
    }

    override fun onPause() {
        super.onPause()
        headTracker.stop()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.post {
                hideSystemUi()
            }
        }
    }

    /**
     * Physical Volume keys act as Cardboard capacitive trigger / recenter
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            headTracker.recenter()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
