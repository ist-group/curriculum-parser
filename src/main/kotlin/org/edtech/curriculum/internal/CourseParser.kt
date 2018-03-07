package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.edtech.curriculum.Course
import org.edtech.curriculum.GradeStep
import org.edtech.curriculum.KnowledgeRequirement
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class CourseParser(private val courseElement: Element): BasicCourseParser(courseElement) {
    fun extractKnowledgeRequirementForGradeStep(gradeStep: GradeStep): String {
        return courseElement.select("knowledgeRequirements gradeStep:containsOwn(${gradeStep.name})")
                .map { it.parent() }
                .joinToString { it.select("text").text() }
    }
    private fun getCentralContent(): List<CentralContent> {
        return HtmlParser()
            .toCentralContent(Jsoup.parse(
                courseElement.select("centralContent").text()
            ))
    }
    private fun getKnowledgeRequirements(): List<KnowledgeRequirement>? {
        return KnowledgeRequirementParser().getKnowledgeRequirements(
                extractKnowledgeRequirementForGradeStep(GradeStep.E),
                extractKnowledgeRequirementForGradeStep(GradeStep.C),
                extractKnowledgeRequirementForGradeStep(GradeStep.A)
        )
    }
    override fun getCourse(): Course {
        val basicCourse = super.getCourse()
        return Course(
                basicCourse.name,
                basicCourse.description,
                basicCourse.code,
                basicCourse.point,
                this.getCentralContent(),
                this.getKnowledgeRequirements()
        )
    }
}