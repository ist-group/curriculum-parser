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
    private val currentSkolverketFileArchive = schoolType.getFileArchive(archiveDir, cache)

    val subjectHtml: List<SubjectHtml> = loadSubjectsHtml()
    val subjects by lazy {
        subjectHtml.flatMap { SubjectParser(schoolType).getSubject(it) }.toList()
    }

    private fun loadSubjectsHtml(): List<SubjectHtml> {
        return IndividualFiledSubjectDataExtractor(currentSkolverketFileArchive, schoolType).getSubjectData()
    }
}