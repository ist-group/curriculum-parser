package org.edtech.curriculum

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class SkolverketFileArchiveTest {

    @Test
    fun testGR() {
        val subjectNames = SkolverketFileArchive( File("./src/test/resources/opendata/compulsory.tgz")).getArchiveFileNames()
        Assert.assertEquals(286, subjectNames.size)
        Assert.assertEquals("ATV- och MC-teknik", subjectNames[0])
        Assert.assertEquals("Administration", subjectNames[1])
        Assert.assertEquals("Information och kommunikation", subjectNames[124])
        Assert.assertEquals("Yttre miljo", subjectNames.last())
    }

    @Test
    fun testGY() {
        val subjectNames = SkolverketFileArchive( File("./src/test/resources/opendata/syllabus.tgz")).getArchiveFileNames()
        Assert.assertEquals(286, subjectNames.size)
        Assert.assertEquals("ATV- och MC-teknik", subjectNames[0])
        Assert.assertEquals("Administration", subjectNames[1])
        Assert.assertEquals("Information och kommunikation", subjectNames[124])
        Assert.assertEquals("Yttre miljo", subjectNames.last())
    }

    @Test
    fun testVUXGR() {
        val subjectNames = SkolverketFileArchive( File("./src/test/resources/opendata/vuxgr.tgz")).getArchiveFileNames()
        Assert.assertEquals(24, subjectNames.size)
        Assert.assertEquals(listOf(
                "Biologi", "Engelska", "Fysik", "Geografi", "Hem- och konsumentkunskap", "Historia", "Kemi",
                "Matematik", "Matematik Nationell delkurs 1", "Matematik Nationell delkurs 2",
                "Matematik Nationell delkurs 3", "Matematik Nationell delkurs 4", "Religionskunskap",
                "Samhallskunskap", "Svenska",
                "Svenska Nationell delkurs 1", "Svenska Nationell delkurs 2",
                "Svenska Nationell delkurs 3", "Svenska Nationell delkurs 4",
                "Svenska som andrasprak",
                "Svenska som andrasprak Nationell delkurs 1", "Svenska som andrasprak Nationell delkurs 2",
                "Svenska som andrasprak Nationell delkurs 3", "Svenska som andrasprak Nationell delkurs 4"),
                subjectNames
        )
    }

    @Test
    fun testGYS() {
        val sf = SkolverketFileArchive( File("./src/test/resources/opendata/gyz.tgz"))
        Assert.assertEquals(sf.getType(), SyllabusType.GYS)
    }

    @Test
    fun testSFI() {
        val sf = SkolverketFileArchive( File("./src/test/resources/opendata/sfi.tgz"))
        val subjectNames = sf.getArchiveFileNames()
        Assert.assertEquals(1, subjectNames.size)
        Assert.assertEquals("Kursplan for kommunal vuxenutbildning i svenska for invandrare", subjectNames[0])
        Assert.assertEquals(sf.getType(), SyllabusType.SFI)
    }

    @Before
    fun setUp() {
    }

    @Test
    fun testSubjectNames() {
    }

    @Test
    fun testGetFileStream() {
    }

    @Test
    fun testGetFileStreams() {
    }

}