package com.example.cardboardos.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class AppShortcutItem(
    val name: String,
    val packageName: String,
    val category: String,
    val vrCompatible: Boolean,
    val description: String
)

@Composable
fun AppsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pm = context.packageManager

    // Curated apps and VR compatible tools
    val featuredApps = remember {
        listOf(
            AppShortcutItem(
                name = "YouTube",
                packageName = "com.google.android.youtube",
                category = "Vídeo & 360°",
                vrCompatible = true,
                description = "Compatible con vídeos 360° y botón Cardboard nativo."
            ),
            AppShortcutItem(
                name = "Google Chrome",
                packageName = "com.android.chrome",
                category = "Navegación Web",
                vrCompatible = true,
                description = "Compatible con WebXR y vídeos VR interactivos."
            ),
            AppShortcutItem(
                name = "Moonlight Game Streaming",
                packageName = "com.limelight",
                category = "Streaming PC",
                vrCompatible = true,
                description = "Cliente oficial de streaming de baja latencia con Sunshine."
            ),
            AppShortcutItem(
                name = "Steam Link",
                packageName = "com.valvesoftware.steamlink",
                category = "Juegos PC",
                vrCompatible = true,
                description = "Transmisión directa de Steam Big Picture desde tu ordenador."
            ),
            AppShortcutItem(
                name = "Galería Redmi",
                packageName = "com.miui.gallery",
                category = "Archivos",
                vrCompatible = false,
                description = "Visor de fotos y vídeos locales de tu Redmi Note 7."
            ),
            AppShortcutItem(
                name = "Ajustes del Sistema",
                packageName = "com.android.settings",
                category = "Sistema",
                vrCompatible = false,
                description = "Configurar Bluetooth, Wi-Fi 5GHz y pantalla."
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // EXPLICATIVE BANNER REGARDING ANDROID VR LIMITATION
            Surface(
                color = CyberSurfaceGlass,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NeonPink.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información VR Android",
                            tint = NeonPink,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¿Por qué Android no proyecta cualquier app 2D dentro de VR?",
                            color = NeonPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Por seguridad y arquitectura de Android (Sandboxing), una app no puede capturar la pantalla de otra app para renderizarla en doble ojo estereoscópico sin permisos root de fabricante (como hace Oculus OS). Cardboard OS resuelve esto lanzando apps con Intent, mediante WebVR interna, o transmitiendo tu escritorio/juegos desde el PC con Sunshine.",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            lineHeight = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // APPS GRID
            Text(
                text = "BIBLIOTECA DE APLICACIONES DISPONIBLES",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(featuredApps) { app ->
                    AppLauncherCard(
                        app = app,
                        onLaunch = {
                            val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            } else {
                                Toast.makeText(
                                    context,
                                    "${app.name} no está instalada en tu Redmi Note 7. Puedes descargarla desde Google Play.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppLauncherCard(
    app: AppShortcutItem,
    onLaunch: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark.copy(alpha = 0.9f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable(onClick = onLaunch)
            .testTag("app_card_${app.name.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = NeonViolet.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = app.name,
                        tint = NeonCyan,
                        modifier = Modifier.padding(6.dp)
                    )
                }

                Surface(
                    color = if (app.vrCompatible) NeonCyan.copy(alpha = 0.15f) else Color(0x3364748B),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (app.vrCompatible) NeonCyan else TextSecondary
                    )
                ) {
                    Text(
                        text = if (app.vrCompatible) "VR READY" else "2D EXTERNA",
                        color = if (app.vrCompatible) NeonCyan else TextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = app.name,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = app.description,
                color = TextSecondary,
                fontSize = 9.sp,
                maxLines = 2,
                lineHeight = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Abrir", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(2.dp))
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Abrir app", tint = NeonCyan, modifier = Modifier.size(12.dp))
            }
        }
    }
}
