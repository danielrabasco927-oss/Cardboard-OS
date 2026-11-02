package com.example

import com.example.cardboardos.model.PcConnectionStatus
import com.example.cardboardos.model.PcStreamConfig
import com.example.cardboardos.model.VrDestination
import com.example.cardboardos.model.VrSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleCardboardOsTest {

    @Test
    fun `verify vr destinations are configured`() {
        assertEquals("Inicio", VrDestination.HOME.title)
        assertEquals("Vídeos", VrDestination.VIDEOS.title)
        assertEquals("Navegador", VrDestination.BROWSER.title)
        assertEquals("Conectar PC", VrDestination.PC_CONNECT.title)
        assertEquals("Juegos", VrDestination.GAMES.title)
    }

    @Test
    fun `verify default vr settings for redmi note 7`() {
        val settings = VrSettings()
        assertTrue(settings.isStereoscopic)
        assertEquals(0f, settings.ipdOffsetDp, 0.01f)
        assertEquals(1.0f, settings.gyroSensitivity, 0.01f)
        assertTrue(settings.cardboardProfile.contains("Redmi Note 7"))
    }

    @Test
    fun `verify pc stream config defaults`() {
        val config = PcStreamConfig()
        assertEquals(PcConnectionStatus.DISCONNECTED, config.status)
        assertEquals(47989, config.port)
        assertNotNull(config.pairingPin)
    }
}
