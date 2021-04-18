package org.edtech.curriculum.internal

import org.edtech.curriculum.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.InputStream
import java.io.File

/**
 * Extracts the data from skolverket files when the curriculum data is stored in one file per subject
 */
class IndividualFiledSubjectDataExtractorSubject(private val inFile: InputStream, private val schoolType: SchoolType) {
     fun getSubjectData(): SubjectHtml {
        return getSubject(inFile)

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

    private inline fun <reified T : Enum<T>> valueOfOrNull(type: String?): T? {
        return try {
            if (type != null) {
                java.lang.Enum.valueOf(T::class.java, type)
            } else {
                null
            }
        } catch (ia: IllegalArgumentException) {
            null
        }
    }

    private fun getSubject(openDataDocumentStream: InputStream, typeOfSyllabus: SyllabusType? = null): SubjectHtml {
        val openDataDocument = Jsoup.parse(openDataDocumentStream, null, "", Parser.xmlParser())
        fun extractString(elementName: String): String = openDataDocument.select("subject > $elementName" ).text()
        val code =  extractString("code")

        // GRSPKOU01 should be a subject area!
        val syllabusType: SyllabusType? = if (code == "GRSPKOU01") {
            SyllabusType.SUBJECT_AREA_SYLLABUS
        } else {
            valueOfOrNull<SyllabusType>(extractString("typeOfSyllabus")) ?: typeOfSyllabus
        }

        return SubjectHtml(
                extractString("name"),
                extractString("description"),
                extractString("version").toIntOrNull(),
                code,
                extractString("designation"),
                extractString("skolfsId"),
                convertDashListToList(extractString("purpose")),
                extractCourses(openDataDocument, typeOfSyllabus),
                extractString("createdDate"),
                extractString("modifiedDate"),
                syllabusType,
                valueOfOrNull<TypeOfSchooling>(extractString("typeOfSchooling")),
                valueOfOrNull<TypeOfSchooling>(extractString("originatorTypeOfSchooling")),
                extractString("gradeScale"),
                extractString("validTo"),
                extractString("applianceDate")
           )
    }

    private fun extractCourses(openDataDocument: Document, typeOfSyllabus: SyllabusType? = null): List<CourseHtml> {
        if (typeOfSyllabus == SyllabusType.SUBJECT_AREA_SYLLABUS) {
            return SubjectAreaDataExtractor(openDataDocument).getCourseData()
        }
        // Get the list of courses and return as CoursePOJOs
        return when (schoolType) {
            SchoolType.GY, SchoolType.GYS ->
                UpperSecondaryCourseDataExtractor(openDataDocument).getCourseData()
            SchoolType.SFI ->
                SFICourseDataExtractor(openDataDocument).getCourseData()
            SchoolType.VUXGRS, SchoolType.VUXGR ->
                VuxCourseDataExtractor(openDataDocument).getCourseData()
            SchoolType.GR, SchoolType.GRS, SchoolType.GRSPEC, SchoolType.SPEC, SchoolType.GRSSPEC, SchoolType.GRSAM ->
                CompulsoryCourseDataExtractor(openDataDocument).getCourseData()
        }
    }
}
