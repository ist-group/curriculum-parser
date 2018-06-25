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
                    getKnowledgeRequirements(stringToRange(it.first)))
        }
    }

    internal fun getKnowledgeRequirements(range: IntRange): Map<GradeStep, String> {
        return subjectDocument
            // Get the subject code element
            .select("knowledgeRequirement")
            .filter {
                (it.select("year").text().toIntOrNull() ?: 0) in range
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

    companion object {
        internal fun stringToRange(rangeString: String): IntRange {
            val rangeText= rangeString.split("-")
            if (rangeText.size != 2) {
                throw NumberFormatException("The string `$rangeString` cannot be interpreted as an range")
            }
            return rangeText[0].trim().toInt()..rangeText[1].trim().toInt()
        }
    }
}

