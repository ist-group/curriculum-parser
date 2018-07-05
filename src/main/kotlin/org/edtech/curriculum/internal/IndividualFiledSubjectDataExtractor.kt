package org.edtech.curriculum.internal

import org.edtech.curriculum.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.InputStream
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * Extracts the data from skolverket files when the curriculum data is stored in one file per subject
 */
class IndividualFiledSubjectDataExtractor(private val skolverketFileArchive: SkolverketFileArchive, private val schoolType: SchoolType): SubjectDataExtractor {
    override fun getSubjectData(): List<SubjectHtml> {
        return skolverketFileArchive.getFileStreams(schoolType.archivePath).map {
            getSubject(it)
        }.toList()
    }

    private inline fun <reified T : kotlin.Enum<T>> valueOfOrNull(type: String?): T? {
        return try {
            java.lang.Enum.valueOf(T::class.java, type)
        } catch (ia: IllegalArgumentException) {
            null
        }
    }

    private fun getSubject(openDataDocumentStream: InputStream): SubjectHtml {
        val openDataDocument = Jsoup.parse(openDataDocumentStream, null, "", Parser.xmlParser())
        fun extractString(elementName: String): String = openDataDocument.select("subject > $elementName" ).text()

        val applianceDate =  try {
            Instant.parse(extractString("applianceDate"))
        } catch (dateTimeParseException: DateTimeParseException) {
            Instant.parse("2011-01-01T00:00:00.000Z")
        }
        val createdDate =  try {
            Instant.parse(extractString("createdDate"))
        } catch (dateTimeParseException: DateTimeParseException) {
            null
        }
        val modifiedDate =  try {
            Instant.parse(extractString("modifiedDate"))
        } catch (dateTimeParseException: DateTimeParseException) {
            null
        }
        val validTo =  try {
            Instant.parse(extractString("validTo"))
        } catch (dateTimeParseException: DateTimeParseException) {
            null
        }

        return SubjectHtml(
                extractString("name"),
                extractString("description"),
                extractString("version").toIntOrNull(),
                extractString("code"),
                extractString("designation"),
                extractString("skolfsId"),
                convertDashListToList(extractString("purpose")),
                extractCourses(openDataDocument),
                createdDate,
                modifiedDate,
                valueOfOrNull<SyllabusType>(extractString("typeOfSyllabus")),
                valueOfOrNull<TypeOfSchooling>(extractString("typeOfSchooling")),
                valueOfOrNull<TypeOfSchooling>(extractString("originatorTypeOfSchooling")),
                extractString("gradeScale"),
                validTo,
                applianceDate
           )
    }

    private fun extractCourses(openDataDocument: Document): List<CourseHtml> {
        // Get the list of courses and return as CoursePOJOs
        return when (schoolType) {
            SchoolType.GY, SchoolType.GYS ->
                UpperSecondaryCourseDataExtractor(openDataDocument).getCourseData()
            SchoolType.VUXGR ->
                VuxCourseDataExtractor(openDataDocument).getCourseData()
            SchoolType.GR, SchoolType.GRS, SchoolType.GRSPEC, SchoolType.GRSAM ->
                CompulsoryCourseDataExtractor(openDataDocument).getCourseData()
        }
    }
}
