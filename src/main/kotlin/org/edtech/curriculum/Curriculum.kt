package org.edtech.curriculum

import org.edtech.curriculum.internal.IndividualFiledSubjectDataExtractor
import org.edtech.curriculum.internal.SubjectParser
import java.io.File

/**
 * Extract the information from Skolverket
 */
class Curriculum(
        private val schoolType: SchoolType,
        archiveDir: File = File(System.getProperty("java.io.tmpdir")),
        cache: Boolean = true
) {
    // Store a copy of the raw subject HTML data
    val subjectHtml by lazy {
        IndividualFiledSubjectDataExtractor(schoolType.getFileArchive(archiveDir, cache), schoolType).getSubjectData()
    }
    // Parse the HTML into a more refined structure
    val subjects by lazy {
        subjectHtml.flatMap { SubjectParser(schoolType).getSubject(it) }.toList()
    }
}