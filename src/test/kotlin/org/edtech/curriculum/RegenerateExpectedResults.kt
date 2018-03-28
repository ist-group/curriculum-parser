package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

fun main(args : Array<String>) {
    val mapper = ObjectMapper()

    for (skolverketFile in SyllabusType.values()) {
        val subjectMap: MutableMap<String, Subject> = HashMap()

        for (subject in Syllabus(skolverketFile).getSubjects()) {
            subjectMap[subject.code] = subject
        }
        val subjectDir = File("./src/test/resources/valid/${skolverketFile.name}")
        if (subjectDir.isDirectory) {
            for (file in File("./src/test/resources/valid/${skolverketFile.name}").listFiles()) {
                if (!file.name.endsWith(".json")) continue

                val subjectCode = file.nameWithoutExtension
                val parsedSubject = subjectMap[subjectCode]
                if (parsedSubject == null) {
                    println("ERROR: No subject $subjectCode for file ${file.absolutePath}")
                    System.exit(1)
                } else {
                    val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedSubject)
                    file.writeText(json)
                }
            }
        }
    }
}