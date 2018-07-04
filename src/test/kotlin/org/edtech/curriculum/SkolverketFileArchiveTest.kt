package org.edtech.curriculum

import org.junit.Assert
import org.junit.Test
import java.io.File

class SkolverketFileArchiveTest {
    private val dataDir = File("./src/test/resources/opendata/2018-07-02")

    @Test
    fun testSkolverketArchives() {
        SchoolType.values().forEach { schoolType ->
            val sf = SkolverketFileArchive(File(dataDir, schoolType.filename))
            Assert.assertTrue("${schoolType.filename} does not exist", sf.archiveExists())
            Assert.assertEquals("${schoolType.filename} does not contain expected number of xml-files",
                    expectedNumberOfXMLFiles(schoolType), sf.getFileStreams(".xml").size)
            Assert.assertTrue("${schoolType.archivePath} does not exist in ${schoolType.filename}", sf.fileExists(schoolType.archivePath))
        }
    }

    private fun expectedNumberOfXMLFiles(schoolType: SchoolType): Int {
        return when (schoolType) {
            SchoolType.GR, SchoolType.GRS, SchoolType.GRSAM, SchoolType.GRSPEC -> 105
            SchoolType.GY -> 313
            SchoolType.VUXGR -> 13
            SchoolType.GYS -> 91
            SchoolType.SFI -> 2
        }
    }
}