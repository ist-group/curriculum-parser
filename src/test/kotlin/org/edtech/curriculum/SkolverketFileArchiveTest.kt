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
                    sf.getFileStreams(".xml").size, "${schoolType.filename} does not contain expected number of xml-files")
            assertTrue(sf.fileExists(schoolType.archivePath), "${schoolType.archivePath} does not exist in ${schoolType.filename}")
        }
    }

    private fun expectedNumberOfXMLFiles(schoolType: SchoolType): Int {
        return when (schoolType) {
            SchoolType.GR, SchoolType.GRS, SchoolType.GRSAM, SchoolType.GRSPEC -> 111
            SchoolType.GY -> 321
            SchoolType.VUXGR -> 16
            SchoolType.GYS -> 91
            //SchoolType.SFI -> 2
        }
    }
}