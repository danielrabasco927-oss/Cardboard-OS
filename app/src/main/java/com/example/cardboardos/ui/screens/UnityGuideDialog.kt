package com.example.cardboardos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun UnityGuideDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CyberSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonPink),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(480.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonPink.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = NeonPink, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Guía: Compilar APK & Proyecto Unity Cardboard XR", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Instrucciones completas para instalar en tu Redmi Note 7", color = NeonCyan, fontSize = 10.sp)
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_close_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SCROLLABLE BODY
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    GuideSectionCard(
                        title = "MÉTODO 1: INSTALAR ESTE APK DIRECTO EN REDMI NOTE 7 (RECOMENDADO)",
                        badge = "RÁPIDO Y FUNCIONAL AHORA",
                        badgeColor = NeonGreen
                    ) {
                        Text(
                            text = "Esta aplicación Cardboard OS ya está completamente programada en Kotlin nativo para Android. Para tenerla en tu teléfono:\n\n" +
                                    "1. En el menú superior de AI Studio, haz clic en 'Exportar' o descarga el archivo APK generado.\n" +
                                    "2. Pásalo a tu Redmi Note 7 mediante cable USB o súbelo a Google Drive / Telegram.\n" +
                                    "3. En el móvil, abre el Administrador de Archivos, pulsa sobre 'CardboardOS.apk' y selecciona 'Instalar'.\n" +
                                    "4. ¡Listo! Abre la app, colócalo en tus gafas Cardboard y disfruta.",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    GuideSectionCard(
                        title = "MÉTODO 2: CREAR TU VERSIÓN EN UNITY CON CARDBOARD XR PLUGIN",
                        badge = "UNITY 2022 LTS / 2023",
                        badgeColor = NeonCyan
                    ) {
                        Text(
                            text = "Si quieres recrear o expandir este proyecto dentro de Unity 3D:\n\n" +
                                    "1. Descarga Unity Hub e instala Unity 2022.3 LTS (o superior) marcando el módulo 'Android Build Support' (OpenJDK + Android SDK & NDK Tools).\n\n" +
                                    "2. Crea un proyecto nuevo 3D (URP o Built-in).\n\n" +
                                    "3. Abre Window > Package Manager > botón '+' > 'Add package from git URL...' e introduce:\n" +
                                    "https://github.com/googlevr/cardboard-xr-plugin.git\n\n" +
                                    "4. Ve a Edit > Project Settings > XR Plug-in Management. En la pestaña con el icono de Android, marca la casilla 'Cardboard XR Plugin'.\n\n" +
                                    "5. En Player Settings > Android:\n" +
                                    "   - Color Space: Linear\n" +
                                    "   - Graphics APIs: OpenGLES3 o Vulkan\n" +
                                    "   - Minimum API Level: Android 7.0 (API 24) o superior (Redmi Note 7 tiene Android 9/10).\n\n" +
                                    "6. En la jerarquía de la escena, crea una Main Camera y agrégale el script 'TrackedPoseDriver' de XR.\n\n" +
                                    "7. Conecta el Redmi Note 7 por USB con 'Depuración USB' activada y haz clic en File > Build & Run.",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Entendido, volver a Cardboard OS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GuideSectionCard(
    title: String,
    badge: String,
    badgeColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceGlass),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, badgeColor)
                ) {
                    Text(text = badge, color = badgeColor, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
