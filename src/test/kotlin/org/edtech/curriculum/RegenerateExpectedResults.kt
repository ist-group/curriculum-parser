package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

fun main(args : Array<String>) {
    val mapper = ObjectMapper()

    for (skolverketFile in listOf(SkolverketFile.GY, SkolverketFile.VUXGR, SkolverketFile.SFI)) {
        val subjectMap: MutableMap<String, Subject> = HashMap()
        val coursesMap: MutableMap<String, Course> = HashMap()

        for (subjectName in skolverketFile.subjectNames()) {
            val subjectParser = skolverketFile.openSubject(subjectName)
            subjectMap[subjectName] = subjectParser.getSubject()
            for (course in subjectParser.courses) {
                coursesMap[course.code] = course
            }
        }
        val subjectDir = File("./src/test/resources/${skolverketFile.name}/subjects")
        if (subjectDir.isDirectory) {
            for (file in File("./src/test/resources/${skolverketFile.name}/subjects").listFiles()) {
                if (!file.name.endsWith(".json")) continue
                val subjectName = file.name.split(".").first()
                val parsedSubject = subjectMap[subjectName]
                if (parsedSubject == null) {
                    println("ERROR: No subject ${subjectName} for file ${file.absolutePath}")
                    System.exit(1)
                } else {
                    val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedSubject)
                    file.writeText(json)
                }
            }
        }
        val courseDir = File("./src/test/resources/${skolverketFile.name}/courses")
        if (courseDir.isDirectory) {
            for (file in courseDir.listFiles()) {
                if (!file.name.endsWith(".json")) continue
                val courseCode = file.name.split(".").first()
                val parsedCourse = coursesMap[courseCode]
                if (parsedCourse == null) {
                    println("ERROR: No course $courseCode for file ${file.absolutePath}")
                    System.exit(1)
                } else {
                    val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedCourse)
                    file.writeText(json)
                }
            }
        }
    }
}