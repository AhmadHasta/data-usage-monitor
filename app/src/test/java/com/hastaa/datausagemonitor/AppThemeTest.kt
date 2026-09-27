package com.hastaa.datausagemonitor

import com.hastaa.datausagemonitor.data.local.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AppThemeTest {

    @Test
    fun testDefaultThemeIsCyberNeon() {
        val defaultTheme = AppTheme.CYBER_NEON
        assertEquals("cyber_neon", defaultTheme.id)
        assertEquals("Cyber Neon", defaultTheme.label)
    }

    @Test
    fun testMd3ExpressiveTheme() {
        val md3Theme = AppTheme.MD3_EXPRESSIVE
        assertEquals("md3_expressive", md3Theme.id)
        assertEquals("Material 3 Expressive", md3Theme.label)
    }

    @Test
    fun testThemeEnumParsingAndFallback() {
        val parsedNeon = runCatching { AppTheme.valueOf("CYBER_NEON") }.getOrDefault(AppTheme.CYBER_NEON)
        assertEquals(AppTheme.CYBER_NEON, parsedNeon)

        val parsedExpressive = runCatching { AppTheme.valueOf("MD3_EXPRESSIVE") }.getOrDefault(AppTheme.CYBER_NEON)
        assertEquals(AppTheme.MD3_EXPRESSIVE, parsedExpressive)

        val fallback = runCatching { AppTheme.valueOf("INVALID_THEME") }.getOrDefault(AppTheme.CYBER_NEON)
        assertEquals(AppTheme.CYBER_NEON, fallback)
    }
}
