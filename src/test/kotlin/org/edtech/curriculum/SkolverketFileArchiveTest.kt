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
           assertEquals(expectedNumberOfSubjectStreams(schoolType),
                    sf.getFileStreams(schoolType.archivePath).size, "${schoolType.filename} / $schoolType does not contain expected number of subject xml-files")

            assertTrue(sf.fileExists(schoolType.archivePath), "${schoolType.archivePath} does not exist in ${schoolType.filename}")
        }
    }

    private fun expectedNumberOfSubjectStreams(schoolType: SchoolType): Int {
        return when (schoolType) {
            SchoolType.GR -> 25
            SchoolType.GRS -> 23
            SchoolType.GRSAM -> 25
            SchoolType.GRSPEC -> 34
            SchoolType.GY -> 294
            SchoolType.VUXGR -> 15
            SchoolType.GYS -> 74
            SchoolType.SFI -> 1
        }
    }
}