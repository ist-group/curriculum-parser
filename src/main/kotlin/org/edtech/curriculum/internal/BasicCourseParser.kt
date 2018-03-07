package org.edtech.curriculum.internal

import org.edtech.curriculum.Course
import org.jsoup.nodes.Element

open class BasicCourseParser(private val courseElement: Element) {

    open fun getCourse(): Course {
        return Course(
                courseElement.select("name").text(),
                courseElement.select("description").text().removePrefix("<p>").removeSuffix("</p>"),
                courseElement.select("code").text(),
                courseElement.select("point").text().toIntOrNull() ?: 0
        )
    }
}