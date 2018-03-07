package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.edtech.curriculum.Course
import org.edtech.curriculum.GradeStep
import org.edtech.curriculum.KnowledgeRequirement
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.concurrent.atomic.AtomicInteger

class CourseParser(private val courseElement: Element): BasicCourseParser(courseElement) {
    fun extractKnowledgeRequirementForGradeStep(gradeStep: GradeStep): String {
        return courseElement.select("knowledgeRequirements gradeStep:containsOwn(${gradeStep.name})")
                .map { it.parent() }
                .joinToString { it.select("text").text() }
    }
    private fun extractKnowledgeRequirementElementsForGradeStepAndAspect(gradeStep: GradeStep, aspect: String): Element {
        return courseElement.select("knowledgeRequirements aspectType:containsOwn($aspect)")
                .map { it.parent() }.first { it.select("gradeStep:containsOwn(${gradeStep.name})").isNotEmpty() }
    }
    private fun getCentralContent(): List<CentralContent> {
        return HtmlParser()
            .toCentralContent(Jsoup.parse(
                courseElement.select("centralContent").text()
            ))
    }

    private fun extractAspectTypes(): Set<String> {
        return courseElement.select("aspectType").map { it.text() }.toSet()
    }

    private fun cleanKnowledgeRequirementText(text: String): String {
        return Jsoup.parse(text).select("p").html()
    }

    private fun getKnowledgeRequirements(): List<KnowledgeRequirement>? {
        val aspectTypes = extractAspectTypes()
        if (aspectTypes.isEmpty()) {
            return KnowledgeRequirementParser().getKnowledgeRequirements(
                    extractKnowledgeRequirementForGradeStep(GradeStep.E),
                    extractKnowledgeRequirementForGradeStep(GradeStep.C),
                    extractKnowledgeRequirementForGradeStep(GradeStep.A)
            )
        } else {
            val ai = AtomicInteger()
            return aspectTypes.map {
                val knElementE = extractKnowledgeRequirementElementsForGradeStepAndAspect(GradeStep.E, it)
                val knElementC = extractKnowledgeRequirementElementsForGradeStepAndAspect(GradeStep.C, it)
                val knElementA = extractKnowledgeRequirementElementsForGradeStepAndAspect(GradeStep.A, it)
                val text = cleanKnowledgeRequirementText(knElementE.select("aspectDesc").text())
                val choices = mutableMapOf(
                        Pair(GradeStep.E, cleanKnowledgeRequirementText(knElementE.select("text").text())),
                        Pair(GradeStep.C, cleanKnowledgeRequirementText(knElementC.select("text").text())),
                        Pair(GradeStep.A, cleanKnowledgeRequirementText(knElementA.select("text").text()))
                )
                KnowledgeRequirement(text, 0, ai.incrementAndGet(), choices)
            }.toList()
        }
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