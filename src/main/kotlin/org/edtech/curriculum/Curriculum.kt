package org.edtech.curriculum

import org.edtech.curriculum.internal.*
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

    fun getSubjects(): List<Subject> {
        return loadSubjectsHtml().map { SubjectParser().getSubject(it) }
    }

    private fun loadSubjectsHtml(): List<SubjectHtml> {
        return IndividualFiledSubjectDataExtractor(currentSkolverketFileArchive, schoolType).getSubjectData()
    }
}