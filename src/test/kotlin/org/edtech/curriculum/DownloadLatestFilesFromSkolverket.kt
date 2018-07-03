package org.edtech.curriculum

import java.io.File



fun main(args: Array<String>) {
    val useCache = false
    val testResources = File("./src/test/resources/opendata/latest")

    SchoolType.values()
            .distinctBy { it.filename }
            .forEach {
        Curriculum(it, testResources, useCache)
        println("Downloaded ${it.filename}")
    }
    println("Done!")
}
