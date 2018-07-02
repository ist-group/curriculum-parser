package org.edtech.curriculum

import java.io.File



fun main(args: Array<String>) {
    val useCache = false
    val testResources = File("$TEST_RESOURCES_PATH/opendata/")
    val distinctSyllabusTypesByArchiveName = SyllabusType.values().distinctBy { it.filename }

    distinctSyllabusTypesByArchiveName.forEach { syllabusType ->
        Syllabus(syllabusType, testResources, useCache)
        println("Downloaded ${syllabusType.filename}")
    }
    println("Done!")
}
