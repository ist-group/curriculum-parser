package org.edtech.curriculum

import org.edtech.curriculum.internal.*
import java.io.File

/**
 * Extract the information from Skolverket
 */
class Syllabus(private val syllabusType: SyllabusType, archiveDir: File = File(System.getProperty("java.io.tmpdir"))) {
    private val currentSkolverketFileArchive = syllabusType.getFileArchive(archiveDir)
    val subjectHtml: List<SubjectHtml> = loadSubjectsHtml()

    fun getSubjects(): List<Subject> {
        return loadSubjectsHtml().map { SubjectParser().getSubject(it) }
    }

    private fun loadSubjectsHtml(): List<SubjectHtml> {
        return IndividualFiledSubjectDataExtractor(currentSkolverketFileArchive, syllabusType).getSubjectData()
    }
}