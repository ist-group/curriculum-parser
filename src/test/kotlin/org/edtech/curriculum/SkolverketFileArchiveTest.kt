package org.edtech.curriculum

import org.junit.Assert
import org.junit.Test
import java.io.File

class SkolverketFileArchiveTest {
    private val dataDir = File("./src/test/resources/opendata/2018-07-02")

    @Test
    fun testGR() {
        val sf = SkolverketFileArchive( File("$dataDir/compulsory.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.GR)
        Assert.assertEquals(105, sf.getFileStreams("").size)

    }

    @Test
    fun testGY() {
        val sf = SkolverketFileArchive( File("$dataDir/syllabus.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.GY)
        Assert.assertEquals(sf.getFileStreams("").size, 313)
    }

    @Test
    fun testVUXGR() {
        val sf = SkolverketFileArchive( File("$dataDir/vuxgr.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.VUXGR)
        Assert.assertEquals(13, sf.getFileStreams("").size)
    }

    @Test
    fun testGYS() {
        val sf = SkolverketFileArchive( File("$dataDir/gys.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.GYS)
        Assert.assertEquals(sf.getFileStreams("").size, 91)
    }

    @Test
    fun testSFI() {
        val sf = SkolverketFileArchive( File("$dataDir/sfi.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.SFI)
        Assert.assertEquals(sf.getFileStreams("").size, 2)
    }

}