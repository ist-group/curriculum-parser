package org.edtech.curriculum.internal

import org.edtech.curriculum.*
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

    internal fun getCoursesConditions(): List<CourseCondition> {
        val yearRange =  subjectDocument.select("centralContent year").map { it.text() }.toSet()
        val types =  subjectDocument.select("typeOfCentralContent, typeOfRequirement").map { it.text() }.filter { it.isNotEmpty() }.toSet()

        // Combine the types with year ranges
        return if (types.isNotEmpty()) {
           types.flatMap { yearRange.map { year -> CourseCondition(year, it) } }.toList()
        } else {
           yearRange.map { year -> CourseCondition(year, "") }.toList()
        }
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

