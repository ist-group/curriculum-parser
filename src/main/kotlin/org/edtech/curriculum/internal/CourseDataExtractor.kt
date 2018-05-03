package org.edtech.curriculum.internal

import org.edtech.curriculum.CourseHtml

interface CourseDataExtractor {
    fun getCourseData(): List<CourseHtml>
}