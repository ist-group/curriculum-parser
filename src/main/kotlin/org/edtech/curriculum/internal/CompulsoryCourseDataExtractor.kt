package org.edtech.curriculum.internal

import org.edtech.curriculum.*
import org.jsoup.nodes.Document
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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

    override fun getCourseData(): List<CourseHtml> {
        val code = subjectDocument.select("code").first().text()
        val centralContents = subjectDocument.select("centralContent")
                .map {
                    Pair(it.select("year").text(),
                            convertDashListToList(fixHtmlEncoding(it.select("text").text())))
                }.toList()
        return centralContents.map {
            CourseHtml("Årskurs ${it.first}",
                    "",
                    "${code}_${it.first}",
                    it.first,
                    "",
                    it.second,
                    getKnowledgeRequirements(it.first.toIntOrNull() ?: 0))
        }
    }

    private fun getKnowledgeRequirements(targetYear: Int): Map<GradeStep, String> {
        return subjectDocument
                // Get the subject code element
                .select("knowledgeRequirement")
                .filter {
                    compareYearString(
                            targetYear,
                            it.select("year").text()
                    )
                }
                .map {
                    val gradeStepText = it.select("gradeStep").text()
                    // Lower years has not grade steps, convert to G level
                    val gradeStep = if (gradeStepText.isEmpty()) GradeStep.G else GradeStep.valueOf(gradeStepText)
                    Pair(
                            gradeStep,
                            it.select("text").text())

                }.toMap()
    }

    /**
     * Check if the supplied targetYear is equal or in the range [min]-[max] as described by the string.
     */
    private fun compareYearString(targetYear: Int, year: String): Boolean {
        val yearParts = year.split("-")
        return if (yearParts.size > 1) {
            yearParts.getOrNull(0)?.toIntOrNull() ?: 0 <= targetYear
            yearParts.getOrNull(1)?.toIntOrNull() ?: 0 >= targetYear
           } else {
            yearParts.getOrNull(0)?.toIntOrNull() ?: 0 >= targetYear
        }
    }
}

