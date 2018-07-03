package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File


class SubjectFileGenerator(private val destDir: File, private val archiveDir: File) {
    /**
     * Generates json files for all syllabi
     */
    fun generateAll() {
        // Generate all curriculum files
        for (syllabusType in SyllabusType.values()) {
            val subjectDir = File("${destDir.absolutePath}/${syllabusType.name}")
            if (!subjectDir.exists()) {
                subjectDir.mkdirs()
            }
            Syllabus(syllabusType, archiveDir).getSubjects().forEach {
                writeSubjectToFile(it, File("$subjectDir/${it.code}.json"))
            }
        }
    }

    /**
     * Update only existing files
     */
    fun regenerate() {
        for (syllabusType in SyllabusType.values()) {
            val subjectMap = Syllabus(syllabusType, archiveDir)
                    .getSubjects()
                    .map { Pair(it.code, it) }
                    .toMap()
            val subjectDir = File("${destDir.absolutePath}/${syllabusType.name}")
            if (subjectDir.isDirectory) {
                for (file in subjectDir.listFiles()) {
                    if (!file.name.endsWith(".json")) continue

                    val subjectCode = file.nameWithoutExtension
                    val parsedSubject = subjectMap[subjectCode]
                    if (parsedSubject == null) {
                        println("ERROR: No subject $subjectCode for file ${file.absolutePath}")
                        System.exit(1)
                    } else {
                        writeSubjectToFile(parsedSubject, file)
                    }
                }
            }
        }
    }

    /**
     * Update one specific subject
     */
    fun generateOneSubject(syllabusType: SyllabusType, subjectCode: String) {
        val subject = Syllabus(syllabusType, archiveDir)
                .getSubjects()
                .firstOrNull { it.code == subjectCode }
        if (subject != null) {
            writeSubjectToFile(subject, File("$destDir/$syllabusType/$subjectCode.json"))
        } else {
            println("ERROR: cannot find subject $String in syllabus $syllabusType.")
            printHelp()
            System.exit(1)
        }
    }

    private fun writeSubjectToFile(subject: Subject, file: File) {
        // Force \n as line breaks
        file.writeText(ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(subject))
    }
}