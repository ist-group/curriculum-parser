package org.edtech.curriculum.internal

import org.edtech.curriculum.*

internal class CourseParser(private val courseData: CourseHtml) {

    /**
     * Return a Course entity from the supplied root element containing the course data
     */
    fun getCourse(): Course {
        return Course(
                courseData.name,
                courseData.description,
                courseData.code,
                CentralContentConverter().getCentralContents(courseData.centralContent),
                KnowledgeRequirementConverter().getKnowledgeRequirements(courseData.knowledgeRequirement),
                courseData.point.toIntOrNull(),
                toYearGroup(courseData.year)
        )
    }
}