package org.edtech.curriculum

import java.io.File

fun main(args : Array<String>) {
    if (args.size >= 2) {
        when {
            args[0] == "--all" ->
                SubjectFileGenerator(File(args[1]), if (args.size > 2) {
                    File(args[2])
                } else {
                    File(System.getProperty("java.io.tmpdir"))
                }, false).generateAll()
            args[0] == "--regenerate" ->
                SubjectFileGenerator(File(args[1]), if (args.size > 2) {
                    File(args[2])
                } else {
                    File(System.getProperty("java.io.tmpdir"))
                }, false).regenerate()
            args.size > 2 -> {
                if (SyllabusType.values().any { it.name == args[0] }) {
                    SubjectFileGenerator(File(args[2]), if (args.size > 3) {
                        File(args[3])
                    } else {
                        File(System.getProperty("java.io.tmpdir"))
                    }, false).generateOneSubject(SyllabusType.valueOf(args[0]), args[1])
                } else {
                    println("ERROR: unknown syllabus type ${args[0]}.")
                    printHelp()
                    System.exit(1)
                }
            }
            else -> {
                println("ERROR: Incorrect parameter format.")
                printHelp()
                System.exit(1)
            }
        }
    } else {
        println("ERROR: Incorrect parameter format.")
        printHelp()
        System.exit(1)
    }
}

fun printHelp() {
    println("Usage: GenerateResult [<syllabusType> <subjectCode> | --all | --regenerate] <dest-dir> [<source-dir>]")
    println("\tThree modes are available: to the tool, either generate a specific file by generating syllabus/subject code or generate all files with the --all flag, ")
    println("\t- Generate a specific file by generating syllabus/subject code")
    println("\t- Use --all to generate all subjects for all school forms")
    println("\t- Use --regenerate to update existing files in the dest-dir")
    println("\nIf source-dir is not specified or empty, new versions of the source data from skolverket will be downloaded")
}

