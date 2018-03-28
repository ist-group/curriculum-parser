package org.edtech.curriculum.internal

import org.edtech.curriculum.*
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
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
 * @param skolverketFileArchive to extract information from
 */
class CompulsorySubjectsDataExtractor(skolverketFileArchive: SkolverketFileArchive, syllabusType: SyllabusType): SubjectsDataExtractor {

    private val subjectsDocument = Jsoup.parse(skolverketFileArchive.getFileStream("${syllabusType.schoolType}-amnesplan.xml"), null, "", Parser.xmlParser())
    private val knowledgeRequirementDocument = Jsoup.parse(skolverketFileArchive.getFileStream("${syllabusType.schoolType}-kunskapskrav.xml"), null, "", Parser.xmlParser())

    private fun getKnowledgeRequirements(code: String, targetYear: Int): Map<GradeStep, String> {
        return knowledgeRequirementDocument
                // Get the subject code element
                .select("subject code:containsOwn($code)")
                // Process the subject element
                .mapNotNull { it.parent().select("knowledgeRequirements") }
                .flatMap { it.toList() }
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

    private fun convertStringToInstant(string: String): Instant? {
        return try {
            LocalDateTime.parse(
                    string,
                    DateTimeFormatter.BASIC_ISO_DATE
            ).toInstant(ZoneOffset.UTC)
        } catch (e: Exception) {
            null
        }
    }

    override fun getSubjectData(): List<SubjectHtml> {
        val skolFsId = subjectsDocument.select("skolfsId").text()
        val applianceDate = convertStringToInstant(subjectsDocument.select("applianceDate").text())

        return subjectsDocument.select("subject").map {
            val code = it.select("code").text()
            val purpose = fixHtmlEncoding(it.select("purpose").text())
            val name = it.select("name").text()
            val description = fixHtmlEncoding(it.select("description").text())
            val centralContents = it.select("centralContents")
                    .map {
                        Pair(it.select("year").text(),
                                fixHtmlEncoding(it.select("text").text()))
                    }.toList()
            val courses = centralContents.map {
                CourseHtml("Årskurs ${it.first}",
                        "",
                        "${code}_${it.first}",
                        it.first,
                        "",
                        it.second,
                        getKnowledgeRequirements(code, it.first.toIntOrNull() ?: 0))
            }
            SubjectHtml(
                    name,
                    description,
                    code,
                    skolFsId,
                    purpose,
                    courses,
                    applianceDate
            )
        }
    }
}

