package org.edtech.curriculum.internal

import org.edtech.curriculum.CourseHtml
import org.edtech.curriculum.GradeStep
import org.edtech.curriculum.RequirementGroup
import org.edtech.curriculum.stringToRange
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Parses the open data supplied by skolverket for the compulsory subjects
 *
 * The parser takes an XML file as supplied by http://opendata.skolverket.se/data/compulsary.tgz
 * Extracts the information to be used for processing
 *
 *
 * @param subjectDocument to extract information from
 */
class CompulsoryCourseDataExtractor(private val subjectDocument: Document): CourseDataExtractor {

    data class CourseCondition(val year: String, val type: String)

    override fun getCourseData(): List<CourseHtml> {
        val code = subjectDocument.select("code").first().text()
        return getCoursesConditions().mapNotNull {
            val centralContent = getCentralContent(it.year, it.type)
            val knowledgeRequirement = getKnowledgeRequirements(stringToRange(it.year), it.type)
            // Return the courses where we got content
            if (centralContent != null || knowledgeRequirement.isNotEmpty()) {
                CourseHtml("Årskurs ${it.year}  ${it.type}".trim(),
                        "",
                        "${code}_${it.year}-${it.type}".trim('-'),
                        it.year,
                        "",
                        centralContent ?: "",
                        knowledgeRequirement)
            } else {
                null
            }
        }
    }

    internal fun getCoursesConditions(): Set<CourseCondition> {
        val conditions =  subjectDocument.select("centralContent").map { CourseCondition(it.select("year").text(), it.select("typeOfCentralContent").text()) }.toSet()
        val conditionsFromRequirements =  subjectDocument.select("knowledgeRequirement").map { CourseCondition(it.select("year").text(), it.select("typeOfRequirement").text()) }.toSet()

        // check if there is any requirement that does not exist as a central content type
        val unmappedTypes = conditionsFromRequirements.filter { requirement -> conditions.none { it.type == requirement.type } }

        if (unmappedTypes.isNotEmpty()) {
            // Add the requirement types to all type less conditions
            return conditions.flatMap {condition ->
                if (condition.type.isEmpty()) {
                    unmappedTypes.map {
                        if (condition.year.endsWith(it.year)) {
                            CourseCondition(condition.year, it.type)
                        } else {
                            condition
                        }
                    }
                } else {
                    setOf(condition)
                }
            }.toSet()
        }
        return conditions
    }

    internal fun getKnowledgeRequirements(range: IntRange, type: String): List<RequirementGroup> {
        fun extractGradeStep(element: Element): GradeStep {
            val gradeStepText = element.select("gradeStep").text()
            // Lower years has not grade steps, convert to G level
            return if (gradeStepText.isEmpty()) GradeStep.G else GradeStep.valueOf(gradeStepText)
        }

        return subjectDocument
            // Get the subject code element
            .select("knowledgeRequirement")
            .filter {
                (it.select("year").text().toIntOrNull() ?: 0) in range &&
                (it.select("typeOfRequirement").text() == type)
            }
            .groupingBy { it.select("year").text().toInt() }
            .fold( { year, element ->
                RequirementGroup(
                    mapOf(extractGradeStep(element) to element.select("text").text()),
                    year)},
                    // combine the requirements into groups of end year-level
                    {_, acc, element ->
                        RequirementGroup(acc.knowledgeRequirements + mapOf(extractGradeStep(element) to element.select("text").text()), acc.year)
                    }
            ).values.toList()
    }

    private fun getCentralContent(range: String, type: String): String? {
        return subjectDocument
                // Get the subject code element
                .select("centralContent")
                .filter {
                    val elementType = it.select("typeOfCentralContent").text()
                    (it.select("year").text() == range &&
                            (elementType.isEmpty() || elementType == type))
                }.map { it.select("text").text() }
                .firstOrNull()
    }
}

