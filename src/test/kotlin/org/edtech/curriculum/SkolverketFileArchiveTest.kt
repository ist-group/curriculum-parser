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
            SchoolType.GYS_SUBJECT_AREA -> 6
            SchoolType.SFI -> 1
            SchoolType.SPEC -> 34
            SchoolType.GRSSPEC -> 23
        }
    }
}