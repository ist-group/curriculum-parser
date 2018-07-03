package org.edtech.curriculum

import java.io.File

fun main(args : Array<String>) {
    val versions = File("./src/test/resources/valid/").listFiles()

    if (args.isEmpty()) {
        versions.forEach {
            val subjectFileGenerator = SubjectFileGenerator(it, File("./src/test/resources/opendata/${it.name}"))
            subjectFileGenerator.regenerate()
        }
    } else if (args.size == 2) {

        if (SyllabusType.values().any { it.name == args[0] }) {
            versions.forEach {
                val subjectFileGenerator = SubjectFileGenerator(it, File("./src/test/resources/opendata/${it.name}"))
                subjectFileGenerator.generateOneSubject(SyllabusType.valueOf(args[0]), args[1])
            }
        }
    } else {
        println("ERROR: Incorrect parameter format.")
        println("Usage: RegenerateExpectedResult [<syllabusType> <subjectCode> | ]")
        System.exit(1)
    }
}