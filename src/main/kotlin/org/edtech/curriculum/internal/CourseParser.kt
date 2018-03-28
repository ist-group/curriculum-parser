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
                toCentralContent(courseData.centralContent),
                getKnowledgeRequirements(courseData.knowledgeRequirement)?: listOf(),
                courseData.point.toIntOrNull(),
                toYearGroup(courseData.year)
        )
    }

    private fun getKnowledgeRequirements(knowledgeRequirementData: Map<GradeStep, String>): List<KnowledgeRequirement>? {
        return KnowledgeRequirementConverter().getKnowledgeRequirements(knowledgeRequirementData)
    }
}