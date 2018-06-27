package org.edtech.curriculum.internal

import org.edtech.curriculum.*
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
class IndividualFiledSubjectDataExtractor(private val skolverketFileArchive: SkolverketFileArchive, private val syllabusType: SyllabusType): SubjectDataExtractor {
    override fun getSubjectData(): List<SubjectHtml> {
        return skolverketFileArchive.getFileStreams(syllabusType.archivePath).map {
            getSubject(it)
        }.toList()
    }

    private fun getSubject(openDataDocumentStream: InputStream): SubjectHtml {
        val openDataDocument = Jsoup.parse(openDataDocumentStream, null, "", Parser.xmlParser())
        fun extractString(elementName: String): String = openDataDocument.select("subject > $elementName" ).text()

        val applianceDate =  try {
            Instant.parse(extractString("applianceDate"))
        } catch (dateTimeParseException: DateTimeParseException) {
            Instant.parse("2011-01-01T00:00:00.000Z")
        }
        return SubjectHtml(
                extractString("name"),
                extractString("description"),
                extractString("code"),
                extractString("designation"),
                extractString("skolfsId"),
                convertDashListToList(extractString("purpose")),
                extractCourses(openDataDocument),
                applianceDate
           )
    }

    private fun extractCourses(openDataDocument: Document): List<CourseHtml> {
        // Get the list of courses and return as CoursePOJOs
        return when (syllabusType) {
            SyllabusType.GY, SyllabusType.GYS ->
                SyllabusCourseDataExtractor(openDataDocument).getCourseData()
            SyllabusType.VUXGR ->
                VuxCourseDataExtractor(openDataDocument).getCourseData()
            SyllabusType.GR, SyllabusType.GRS, SyllabusType.GRSPEC, SyllabusType.GRSAM ->
                CompulsoryCourseDataExtractor(openDataDocument).getCourseData()
            else -> SyllabusCourseDataExtractor(openDataDocument).getCourseData()
        }
    }
}
