package com.hastaa.datausagemonitor

import com.hastaa.datausagemonitor.domain.model.MetricDisplayMode
import com.hastaa.datausagemonitor.domain.model.TileConfig
import com.hastaa.datausagemonitor.domain.model.TileContentStyle
import com.hastaa.datausagemonitor.domain.model.TileTextLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TileConfigTest {

    @Test
    fun testDefaultMetricDisplayMode() {
        val config = TileConfig()
        assertEquals(TileContentStyle.METRIC_WITH_ICON, config.contentStyle)
        assertEquals(MetricDisplayMode.NUMBERS_AND_ICON, config.metricDisplayMode)
        assertEquals(TileTextLayout.SINGLE_LINE, config.textLayout)
    }

    @Test
    fun testMetricDisplayModeValues() {
        val modes = MetricDisplayMode.entries
        assertEquals(3, modes.size)
        assertEquals(MetricDisplayMode.NUMBERS_AND_ICON, modes[0])
        assertEquals(MetricDisplayMode.TEXT_ONLY, modes[1])
        assertEquals(MetricDisplayMode.ICON_ONLY, modes[2])
    }

    @Test
    fun testTileTextLayoutValuesAndBothHelper() {
        val layouts = TileTextLayout.entries
        assertEquals(5, layouts.size)
        assertTrue(TileTextLayout.SINGLE_LINE.isBoth)
        assertTrue(TileTextLayout.DUAL_LINE_NETWORK_FIRST.isBoth)
        assertTrue(TileTextLayout.DUAL_LINE_METRIC_FIRST.isBoth)
        assertFalse(TileTextLayout.METRIC_ONLY.isBoth)
        assertFalse(TileTextLayout.NETWORK_ONLY.isBoth)
    }
}
