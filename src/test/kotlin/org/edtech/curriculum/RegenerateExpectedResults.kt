package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

fun main(args : Array<String>) {
    val mapper = ObjectMapper()

    for (skolverketFile in listOf(SkolverketFile.GY, SkolverketFile.VUXGR, SkolverketFile.SFI)) {
        val subjectMap: MutableMap<String, Subject> = HashMap()

        for (subjectName in skolverketFile.subjectNames()) {
            val subjectParser = skolverketFile.openSubject(subjectName)
            subjectMap[subjectName] = subjectParser.getSubject()
        }
        val subjectDir = File("./src/test/resources/${skolverketFile.name}")
        if (subjectDir.isDirectory) {
            for (file in File("./src/test/resources/${skolverketFile.name}").listFiles()) {
                if (!file.name.endsWith(".json")) continue
                val subjectName = file.name.split(".").first()
                val parsedSubject = subjectMap[subjectName]
                if (parsedSubject == null) {
                    println("ERROR: No subject $subjectName for file ${file.absolutePath}")
                    System.exit(1)
                } else {
                    val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedSubject)
                    file.writeText(json)
                }
            }
        }
    }
}