package org.edtech.curriculum

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File

class SkolverketFileArchiveTest {
    private val dataDir = File("./src/test/resources/opendata/2018-09-26")

    @TestFactory
    fun testSkolverketArchives() = SchoolType.values().map { schoolType ->
        DynamicTest.dynamicTest(schoolType.name) {
            val sf = SkolverketFileArchive(File(dataDir, schoolType.filename))
            assertTrue(sf.archiveExists(), "${schoolType.filename} does not exist")
            assertEquals(expectedNumberOfXMLFiles(schoolType),
                    sf.getFileStreams(schoolType.archivePath).size, "${schoolType.filename } -> ${schoolType.name}  does not contain expected number of xml-files")
            assertTrue(sf.fileExists(schoolType.archivePath), "${schoolType.archivePath} does not exist in ${schoolType.filename}")
        }
    }


    @TestFactory
    fun testSkolverketArchivesSubjectArea() = SchoolType.values()
            .filter { schoolType -> schoolType.archivePathSubjectArea != null }
            .map { schoolType ->
        DynamicTest.dynamicTest(schoolType.name + "Subject Area") {
            val sf = SkolverketFileArchive(File(dataDir, schoolType.filename))
            assertTrue(sf.archiveExists(), "${schoolType.filename} does not exist")
            assertTrue(sf.fileExists(schoolType.archivePathSubjectArea!!), "${schoolType.archivePathSubjectArea} does not exist in ${schoolType.filename}")
            assertEquals(expectedNumberOfXMLFilesSubjectArea(schoolType),
                    sf.getFileStreams(schoolType.archivePathSubjectArea!!).size, "${schoolType.filename } -> ${schoolType.name} / ${schoolType.archivePathSubjectArea} does not contain expected number of xml-files")
        }
    }

    private fun expectedNumberOfXMLFiles(schoolType: SchoolType): Int {
            return when (schoolType) {
                SchoolType.GR -> 25
                SchoolType.GRS -> 23
                SchoolType.GRSAM -> 25
                SchoolType.GRSPEC -> 34
                SchoolType.GY -> 294
                SchoolType.VUXGR -> 15
                SchoolType.VUXGRS -> 13
                SchoolType.GYS -> 74
                SchoolType.SFI -> 1
                SchoolType.SPEC -> 34
                SchoolType.GRSSPEC -> 23
            }
    }
    private fun expectedNumberOfXMLFilesSubjectArea(schoolType: SchoolType): Int {
        return when (schoolType) {
            SchoolType.GYS -> 6
            SchoolType.VUXGRS -> 3
            else -> 0
        }
    }
}