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
                CourseHtml("Årskurs ${it.year}".trim(),
                        "",
                        "${code}_${it.year}",
                        it.type,
                        it.year,
                        "",
                        centralContent ?: "",
                        knowledgeRequirement)
            } else {
                null
            }
        }
    }

    internal fun getCoursesConditions(): Set<CourseCondition> =
            subjectDocument.select("centralContent")
                    .map { CourseCondition(
                            it.select("year").text(),
                            it.select("typeOfCentralContent").text())
                    }
                    .toSet()

    internal fun getKnowledgeRequirements(range: IntRange, type: String): List<RequirementGroup> {
        fun extractGradeStep(element: Element): GradeStep {
            val gradeStep = element.select("gradeStep").text()
            val typeOfRequirement = element.select("typeOfRequirement").text()
            // Pick gradeStep if supplied otherwhise pick the typeOfRequirement
            // Type of requirement is used in GRS in the same way as gradeStep.
            val gradeStepCombined = if (gradeStep.isEmpty()) typeOfRequirement else gradeStep

            // Lower years has not grade steps, convert to G level
            return if (gradeStepCombined.isEmpty() || GradeStep.values().none { it.name == gradeStepCombined })
                GradeStep.G
            else
                GradeStep.valueOf(gradeStepCombined)
        }

        return subjectDocument
            // Get the subject code element
            .select("knowledgeRequirement")
            .filter {
                (it.select("year").text().toIntOrNull() ?: 0) in range &&
                (type.isEmpty() || it.select("typeOfRequirement").text() == type)
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

