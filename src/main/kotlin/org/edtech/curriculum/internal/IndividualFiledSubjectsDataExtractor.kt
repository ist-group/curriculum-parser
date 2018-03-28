package org.edtech.curriculum.internal

import org.edtech.curriculum.CourseHtml
import org.edtech.curriculum.GradeStep
import org.edtech.curriculum.SkolverketFileArchive
import org.edtech.curriculum.SubjectHtml
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.io.InputStream
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * Extracts the data from skolverket files when the curriculum data is stored in one file per subject
 */
class IndividualFiledSubjectsDataExtractor(private val skolverketFileArchive: SkolverketFileArchive): SubjectsDataExtractor {
    override fun getSubjectData(): List<SubjectHtml> {
        return skolverketFileArchive.getFileStreams("/subject/").map {
            getSubject(it)
        }.toList()
    }

    private fun getSubject(openDataDocumentStream: InputStream): SubjectHtml {
        val openDataDocument = Jsoup.parse(openDataDocumentStream, null, "", Parser.xmlParser())
        fun extractString(elementName: String): String = openDataDocument.select("subject > $elementName" ).text()
        var applianceDate =  Instant.parse("2011-01-01T00:00:00.000Z")

        try {
            applianceDate =  Instant.parse(extractString("applianceDate"))
        } catch (dtpe: DateTimeParseException) { }

        val name = extractString("name")
        if (name.isEmpty()) {
            println(openDataDocumentStream)
        }
        return SubjectHtml(extractString("name"),
                extractString("description"),
                extractString("code"),
                extractString("skolfsId"),
                extractString("purpose"),
                extractCourses(openDataDocument, extractString("typeOfSchooling")),
                applianceDate
           )
    }

    private fun extractCourses(openDataDocument: Document, schoolType: String): List<CourseHtml> {
        // Get the list of courses and return as CoursePOJOs
        return if (schoolType == "BASIC_ADULT_EDUCATION") {
            // Vux has no courses in GR and no Years
            listOf(getCourse(openDataDocument))
        } else {
            //UPPER_SECONDARY_EDUCATION / SWEDISH_FOR_IMMIGRANTS
            openDataDocument.select("subject > courses")
                    .map { getCourse(it) }
                    .toList()
        }
    }

    private fun getKnowledgeRequirements(knowledgeRequirementElements: Elements): Map<GradeStep, String> {
        return knowledgeRequirementElements.map {
            Pair(
                    GradeStep.valueOf(it.selectFirst("gradeStep").text()),
                    it.selectFirst("text").text()
            )
        }.toMap()
    }

    private fun getCourse(courseElement: Element): CourseHtml {
        return CourseHtml(
                courseElement.select("name").text(),
                courseElement.select("description").text().removePrefix("<p>").removeSuffix("</p>"),
                courseElement.select("code").text(),
                "",
                courseElement.select("point").text(),
                courseElement.select("centralContent, centralContents").text(),
                this.getKnowledgeRequirements(courseElement.select("knowledgeRequirements"))
        )
    }
}