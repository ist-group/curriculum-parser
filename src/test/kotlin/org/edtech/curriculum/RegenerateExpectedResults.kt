package org.edtech.curriculum

import java.io.File

fun main(args : Array<String>) {
    val subjectFileGenerator = SubjectFileGenerator(File("./src/test/resources/valid/2018-07-02"), File("./src/test/resources/opendata/2018-07-02"))
    if (args.isEmpty()) {
        subjectFileGenerator.regenerate()
    } else if (args.size == 2) {
        if (SyllabusType.values().any { it.name == args[0] }) {
            subjectFileGenerator.generateOneSubject(SyllabusType.valueOf(args[0]), args[1])
        }
    } else {
        println("ERROR: Incorrect parameter format.")
        println("Usage: RegenerateExpectedResult [<syllabusType> <subjectCode> | ]")
        System.exit(1)
    }
}