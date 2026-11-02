package com.example.cardboardos.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    modifier: Modifier = Modifier
) {
    var currentUrl by remember { mutableStateOf("https://es.wikipedia.org/wiki/Realidad_virtual") }
    var inputUrl by remember { mutableStateOf("https://es.wikipedia.org/wiki/Realidad_virtual") }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }
    var showVirtualKeyboard by remember { mutableStateOf(false) }

    val bookmarks = remember {
        listOf(
            "Wikipedia VR" to "https://es.wikipedia.org/wiki/Realidad_virtual",
            "YouTube VR" to "https://m.youtube.com",
            "Sketchfab 3D" to "https://sketchfab.com/feed",
            "Google" to "https://www.google.com",
            "Retro Games" to "https://www.retrogames.cc",
            "Three.js VR" to "https://threejs.org/examples/#webxr_vr_ballshooter"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP NAVIGATION BAR (VR CYBERPUNK BROWSER TOOLBAR)
            Surface(
                color = CyberSurfaceGlass,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Back
                        IconButton(
                            onClick = { webViewInstance?.goBack() },
                            modifier = Modifier.size(34.dp).testTag("browser_back")
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = NeonCyan)
                        }

                        // Forward
                        IconButton(
                            onClick = { webViewInstance?.goForward() },
                            modifier = Modifier.size(34.dp).testTag("browser_forward")
                        ) {
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Adelante", tint = NeonCyan)
                        }

                        // Reload
                        IconButton(
                            onClick = { webViewInstance?.reload() },
                            modifier = Modifier.size(34.dp).testTag("browser_reload")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Recargar", tint = NeonViolet)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // URL input bar
                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            placeholder = { Text("Introduce una URL o búsqueda...", fontSize = 11.sp, color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("browser_url_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = NeonViolet.copy(alpha = 0.5f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CyberSurfaceDark,
                                unfocusedContainerColor = CyberSurfaceDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Virtual Keyboard Toggle
                                    IconButton(
                                        onClick = { showVirtualKeyboard = !showVirtualKeyboard },
                                        modifier = Modifier.size(28.dp).testTag("browser_toggle_keyboard")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Keyboard,
                                            contentDescription = "Teclado VR",
                                            tint = if (showVirtualKeyboard) NeonPink else TextSecondary
                                        )
                                    }

                                    // Go Button
                                    IconButton(
                                        onClick = {
                                            var target = inputUrl.trim()
                                            if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                                target = if (target.contains(".")) "https://$target" else "https://www.google.com/search?q=$target"
                                            }
                                            currentUrl = target
                                            webViewInstance?.loadUrl(target)
                                        },
                                        modifier = Modifier.size(28.dp).testTag("browser_navigate_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.Search, contentDescription = "Ir", tint = NeonCyan)
                                    }
                                }
                            }
                        )
                    }

                    // Progress bar
                    if (isLoading) {
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            color = NeonPink,
                            trackColor = Color.Transparent,
                            modifier = Modifier.fillMaxWidth().height(2.dp).padding(top = 2.dp)
                        )
                    }

                    // Bookmarks shortcut row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        items(bookmarks) { (title, url) ->
                            Surface(
                                color = NeonViolet.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, NeonCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable {
                                    inputUrl = url
                                    currentUrl = url
                                    webViewInstance?.loadUrl(url)
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = title, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // WEBVIEW EMBEDDED CONTAINER (VIRTUAL DISPLAY)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.setSupportZoom(true)
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                    url?.let { inputUrl = it }
                                }
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                }
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    return false
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    progress = newProgress
                                }
                            }

                            loadUrl(currentUrl)
                            webViewInstance = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // VIRTUAL GAZE / ON-SCREEN KEYBOARD FOR CARDBOARD
            if (showVirtualKeyboard) {
                VirtualGazeKeyboard(
                    currentText = inputUrl,
                    onTextChange = { inputUrl = it },
                    onEnter = {
                        showVirtualKeyboard = false
                        var target = inputUrl.trim()
                        if (!target.startsWith("http://") && !target.startsWith("https://")) {
                            target = if (target.contains(".")) "https://$target" else "https://www.google.com/search?q=$target"
                        }
                        currentUrl = target
                        webViewInstance?.loadUrl(target)
                    },
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

/**
 * On-Screen VR QWERTY Keyboard designed for Gaze reticle dwell or single tap inside Cardboard.
 */
@Composable
private fun VirtualGazeKeyboard(
    currentText: String,
    onTextChange: (String) -> Unit,
    onEnter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CyberSurfaceDark,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rows = listOf(
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
                listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "."),
                listOf("z", "x", "c", "v", "b", "n", "m", "/", ":", "-")
            )

            for (row in rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    for (char in row) {
                        KeyButton(text = char, onClick = { onTextChange(currentText + char) })
                    }
                }
            }

            // Special actions row (Space, Backspace, Clear, Enter)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Surface(
                    color = NeonViolet.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onTextChange("") }
                ) {
                    Text(text = "Limpiar", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                }

                Surface(
                    color = NeonCyan.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTextChange("$currentText ") }
                ) {
                    Text(text = "Espacio", color = TextPrimary, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 6.dp))
                }

                Surface(
                    color = NeonPink.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable {
                        if (currentText.isNotEmpty()) {
                            onTextChange(currentText.dropLast(1))
                        }
                    }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Backspace, contentDescription = "Borrar", tint = NeonPink, modifier = Modifier.size(14.dp))
                    }
                }

                Surface(
                    color = NeonCyan,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable(onClick = onEnter)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Ir", color = CyberDarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Confirmar", tint = CyberDarkBg, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        color = CyberSurfaceGlass,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.6.dp, NeonCyan.copy(alpha = 0.3f)),
        modifier = Modifier
            .size(width = 30.dp, height = 28.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
