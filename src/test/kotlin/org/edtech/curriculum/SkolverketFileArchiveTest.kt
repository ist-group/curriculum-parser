package org.edtech.curriculum

import org.junit.Assert
import org.junit.Test
import java.io.File

class SkolverketFileArchiveTest {
    private val dataDir = File("./src/test/resources/opendata/2018-07-02")

    @Test
    fun testGR() {
        val sf = SkolverketFileArchive( File("$dataDir/compulsory.tgz"))
        Assert.assertEquals(SchoolType.GR, sf.getType())
        Assert.assertEquals(105, sf.getFileStreams("").size)

    }

    @Test
    fun testGY() {
        val sf = SkolverketFileArchive( File("$dataDir/syllabus.tgz"))
        Assert.assertEquals(SchoolType.GY, sf.getType())
        Assert.assertEquals(313, sf.getFileStreams("").size)
    }

    @Test
    fun testVUXGR() {
        val sf = SkolverketFileArchive( File("$dataDir/vuxgr.tgz"))
        Assert.assertEquals(SchoolType.VUXGR, sf.getType())
        Assert.assertEquals(13, sf.getFileStreams("").size)
    }

    @Test
    fun testGYS() {
        val sf = SkolverketFileArchive( File("$dataDir/gys.tgz"))
        Assert.assertEquals(SchoolType.GYS, sf.getType())
        Assert.assertEquals(91, sf.getFileStreams("").size)
    }

    @Test
    fun testSFI() {
        val sf = SkolverketFileArchive( File("$dataDir/sfi.tgz"))
        Assert.assertEquals(SchoolType.SFI, sf.getType())
        Assert.assertEquals(2, sf.getFileStreams("").size)
    }

}