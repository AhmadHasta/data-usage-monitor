package com.hastaa.datausagemonitor

import com.hastaa.datausagemonitor.util.ByteFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class ByteFormatterTest {

    @Test
    fun testZeroBytes() {
        assertEquals("0 B", ByteFormatter.formatBytes(0L))
        assertEquals("0 B", ByteFormatter.formatBytes(-50L))
    }

    @Test
    fun testBytesUnderOneKilobyte() {
        assertEquals("512 B", ByteFormatter.formatBytes(512L))
        assertEquals("1023 B", ByteFormatter.formatBytes(1023L))
    }

    @Test
    fun testKilobytes() {
        assertEquals("1 KB", ByteFormatter.formatBytes(1024L))
        assertEquals("512 KB", ByteFormatter.formatBytes(512L * 1024L))
    }

    @Test
    fun testMegabytes() {
        // 428 MB
        val bytes428MB = 428L * 1024L * 1024L
        assertEquals("428 MB", ByteFormatter.formatBytes(bytes428MB))

        // ~1.24 MB
        val bytes1_24MB = (1.24 * 1024.0 * 1024.0).toLong()
        assertEquals("1.24 MB", ByteFormatter.formatBytes(bytes1_24MB))
    }

    @Test
    fun testGigabytes() {
        // 1.24 GB
        val bytes1_24GB = (1.24 * 1024.0 * 1024.0 * 1024.0).toLong()
        assertEquals("1.24 GB", ByteFormatter.formatBytes(bytes1_24GB))

        // 18.42 GB
        val bytes18_42GB = (18.42 * 1024.0 * 1024.0 * 1024.0).toLong()
        assertEquals("18.42 GB", ByteFormatter.formatBytes(bytes18_42GB))
    }

    @Test
    fun testFormatParts() {
        val (value, unit) = ByteFormatter.formatBytesParts((1.24 * 1024.0 * 1024.0 * 1024.0).toLong())
        assertEquals("1.24", value)
        assertEquals("GB", unit)
    }
}
