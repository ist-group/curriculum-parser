package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

fun main(args : Array<String>) {
    if (args.isEmpty()) {
        for (syllabusType in SyllabusType.values()) {
            val subjectMap = getSubjectCodeMap(syllabusType)
            val subjectDir = File("./src/test/resources/valid/${syllabusType.name}")
            if (subjectDir.isDirectory) {
                for (file in File("./src/test/resources/valid/${syllabusType.name}").listFiles()) {
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
    } else if (args.size == 2) {
        if (SyllabusType.values().map { it.name }.contains(args[0])) {
            val subjectMap = getSubjectCodeMap(SyllabusType.valueOf(args[0]))
            val subject= subjectMap[args[1]]
            if (subject != null) {
                writeSubjectToFile(subject, File("./src/test/resources/valid/${args[0]}/${args[1]}.json"))
            } else {
                println("ERROR: cannot find subject ${args[1]} in syllabus ${args[0]}.")
                System.exit(1)
            }
        }
    } else {
        println("ERROR: Incorrect parameter format.")
        println("Usage: RegenerateExpectedResult [<syllabusType> <subjectCode> | ]")
        System.exit(1)
    }
}

fun getSubjectCodeMap(syllabusType: SyllabusType): Map<String, Subject> {
    return Syllabus(syllabusType, File("./src/test/resources/opendata/"))
            .getSubjects()
            .map { Pair(it.code, it) }
            .toMap()
}

fun writeSubjectToFile(subject: Subject, file: File) {
    file.writeText(ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(subject))
}