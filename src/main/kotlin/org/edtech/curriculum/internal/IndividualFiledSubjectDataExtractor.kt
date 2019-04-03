package org.edtech.curriculum.internal

import org.edtech.curriculum.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.InputStream

/**
 * Extracts the data from skolverket files when the curriculum data is stored in one file per subject
 */
class IndividualFiledSubjectDataExtractor(private val skolverketFileArchive: SkolverketFileArchive, private val schoolType: SchoolType): SubjectDataExtractor {
    override fun getSubjectData(): List<SubjectHtml> {
        return skolverketFileArchive.getFileStreams(schoolType.archivePath).map {
            getSubject(it)
        }.toList().filter { filterBySpecSchoolType(it.code) }
    }

    /**
     * Special school is a mix of special syllabuses together with the gr syllabuses
     * This parser splits these into different types
     */
    private fun filterBySpecSchoolType (code: String): Boolean {
        return when (schoolType) {
            SchoolType.SPEC -> code.startsWith("GRSP")
            SchoolType.GRSPEC -> code.startsWith("GRGR")
            else -> true
        }
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

        return SubjectHtml(
                extractString("name"),
                extractString("description"),
                extractString("version").toIntOrNull(),
                extractString("code"),
                extractString("designation"),
                extractString("skolfsId"),
                convertDashListToList(extractString("purpose")),
                extractCourses(openDataDocument),
                extractString("createdDate"),
                extractString("modifiedDate"),
                valueOfOrNull<SyllabusType>(extractString("typeOfSyllabus")),
                valueOfOrNull<TypeOfSchooling>(extractString("typeOfSchooling")),
                valueOfOrNull<TypeOfSchooling>(extractString("originatorTypeOfSchooling")),
                extractString("gradeScale"),
                extractString("validTo"),
                extractString("applianceDate")
           )
    }

    private fun extractCourses(openDataDocument: Document): List<CourseHtml> {
        // Get the list of courses and return as CoursePOJOs
        return when (schoolType) {
            SchoolType.GY, SchoolType.GYS ->
                UpperSecondaryCourseDataExtractor(openDataDocument).getCourseData()
            SchoolType.GYS_SUBJECT_AREA ->
                SubjectAreaDataExtractor(openDataDocument).getCourseData()
            SchoolType.VUXGR ->
                VuxCourseDataExtractor(openDataDocument).getCourseData()
            SchoolType.GR, SchoolType.GRS, SchoolType.GRSPEC, SchoolType.SPEC, SchoolType.GRSSPEC, SchoolType.GRSAM ->
                CompulsoryCourseDataExtractor(openDataDocument).getCourseData()
        }
    }
}
