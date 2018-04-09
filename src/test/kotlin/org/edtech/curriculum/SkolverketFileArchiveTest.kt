package org.edtech.curriculum

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class SkolverketFileArchiveTest {

    @Test
    fun testGR() {
        val sf = SkolverketFileArchive( File("./src/test/resources/opendata/compulsory.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.GR)
        Assert.assertEquals(sf.getFileStreams("").size, 8)

    }

    @Test
    fun testGY() {
        val sf = SkolverketFileArchive( File("./src/test/resources/opendata/syllabus.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.GY)
        Assert.assertEquals(sf.getFileStreams("").size, 313)
    }

    @Test
    fun testVUXGR() {
        val sf = SkolverketFileArchive( File("./src/test/resources/opendata/vuxgr.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.VUXGR)
        Assert.assertEquals(sf.getFileStreams("").size, 25)
    }

    @Test
    fun testGYS() {
        val sf = SkolverketFileArchive( File("./src/test/resources/opendata/gys.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.GYS)
        Assert.assertEquals(sf.getFileStreams("").size, 91)
    }

    @Test
    fun testSFI() {
        val sf = SkolverketFileArchive( File("./src/test/resources/opendata/sfi.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.SFI)
        Assert.assertEquals(sf.getFileStreams("").size, 2)
    }

}