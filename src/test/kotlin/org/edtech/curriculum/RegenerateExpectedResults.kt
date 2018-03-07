package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

fun main(args : Array<String>) {
    val mapper = ObjectMapper()

    for (skolverketFile in listOf(SkolverketFile.GY)) {
        val subjectMap: MutableMap<String, Subject> = HashMap()
        val coursesMap: MutableMap<String, Course> = HashMap()

        for (subjectName in skolverketFile.subjectNames()) {
            val subjectParser = skolverketFile.openSubject(subjectName)
            subjectMap[subjectName] = subjectParser.getSubject()
            for (course in subjectParser.getCourses()) {
                coursesMap[course.code] = subjectParser.getCourse(course.code)
            }
        }

        for (file in File("./src/test/resources/GY/subjects").listFiles()) {
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

        for (file in File("./src/test/resources/GY/courses").listFiles()) {
            if (!file.name.endsWith(".json")) continue
            val courseCode = file.name.split(".").first()
            val parsedCourse = coursesMap[courseCode]
            if (parsedCourse == null) {
                println("ERROR: No course ${courseCode} for file ${file.absolutePath}")
                System.exit(1)
            } else {
                val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedCourse)
                file.writeText(json)
            }
        }
    }

}