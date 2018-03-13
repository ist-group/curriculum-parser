package org.edtech.curriculum.internal

import org.edtech.curriculum.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.concurrent.atomic.AtomicInteger

class CourseParser(private val courseElement: Element) {

    /**
     * Return a Course entity from the supplied root element containing the course data
     */
    fun getCourse(): Course {
        return Course(
                courseElement.select("name").text(),
                courseElement.select("description").text().removePrefix("<p>").removeSuffix("</p>"),
                courseElement.select("code").text(),
                courseElement.select("point").text().toIntOrNull() ?: 0,
                this.getCentralContent(),
                this.getKnowledgeRequirements()
        )
    }

    internal fun extractKnowledgeRequirementForGradeStep(gradeStep: GradeStep): String {
        return courseElement.select("knowledgeRequirements gradeStep:containsOwn(${gradeStep.name})")
                .map { it.parent() }
                .joinToString { it.select("text").text() }
    }
    private fun extractKnowledgeRequirementElementsForGradeStepAndAspect(gradeStep: GradeStep, aspect: String): Element {
        return courseElement.select("knowledgeRequirements aspectType:containsOwn($aspect)")
                .map { it.parent() }.first { it.select("gradeStep:containsOwn(${gradeStep.name})").isNotEmpty() }
    }
    private fun getCentralContent(): List<CentralContent> {
        return toCentralContent(
                courseElement.select("centralContent, centralContents").text()
            )
    }

    private fun extractAspectTypes(): Set<String> {
        return courseElement.select("aspectType").map { it.text() }.toSet()
    }

    private fun cleanKnowledgeRequirementText(text: String): String {
        return Jsoup.parse(text).select("p").html()
    }

    /**
     * Combine heading and bullets in one list
     */
    private fun toCentralContent(html: String): List<CentralContent> {
        return Jsoup.parse(html).select("strong, li, i, h1, h2, h3, h4, h5, h6")
                .filter { it.text().isNotEmpty() }
                .map {
                    val type = when (it.tagName()) {
                        "li" -> CentralContentType.BULLET
                        else -> CentralContentType.HEADING
                    }
                    CentralContent(it.text(), type)
                }
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
}