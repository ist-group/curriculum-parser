package org.edtech.curriculum

import org.edtech.curriculum.internal.CourseParser
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.io.InputStream

/**
 * Parses the open data XML supplied by skolverket
 *
 * The parser takes an XML file as supplied by http://opendata.skolverket.se/data/syllabus.tgz
 * Only files from subject/<subject-name>.xml are supported.
 *
 * The returned data objects are tagged versions of the unstructured html content in the XML file
 * The knowledge requirements are mapped per line in e-level so that they can be used as a matrix.
 *
 * @param openDataDocumentStream a file from the open data XML as supplied by skolverket
 */
class SubjectParser(openDataDocumentStream: InputStream) {

    internal val openDataDocument = Jsoup.parse(openDataDocumentStream, null, "", Parser.xmlParser())
    private fun extractString(elementName: String): String = openDataDocument.select("subject > $elementName" ).text()
    private fun extractNodes(elementName: String): Elements = openDataDocument.select("subject > $elementName" )

    internal val name: String = extractString("name")
    private val description: String = extractString("description").removePrefix("<p>").removeSuffix("</p>")
    private val code: String = extractString("code")
    private val skolfsId: String = extractString("skolfsId")
    private val purpose: String = extractString("purpose")
    val courses: List<Course> = extractCourses()

    fun getSubject(): Subject {
        return Subject(name, description, code, skolfsId, toPurposes(purpose))
    }

    fun getCourse(code: String): Course? {
        return courses.firstOrNull { it.code == code }
    }

    private fun extractCourses(): List<Course> {
        // Get the list of courses and return as CoursePOJOs
        val schoolType = extractString("typeOfSchooling")
        return if (schoolType == "BASIC_ADULT_EDUCATION") {
            // Vux has no courses in GR and no Years
            listOf(CourseParser(openDataDocument).getCourse())
        } else {
            //UPPER_SECONDARY_EDUCATION / SWEDISH_FOR_IMMIGRANTS
            extractNodes("courses")
                    .map { CourseParser(it).getCourse() }
                    .toList()
        }
    }

    /**
     * Convert the Purpose html to Entities depending on tag type
     */
    private fun toPurposes(html: String): List<Purpose> {
        return Jsoup.parse("<root>$html</root>").select("root > p, li, root > h1, root > h2, root > h3, root > h4, root > h5, root > h6")
                .filter { it.text().isNotEmpty() }
                // Dont want skolverkets content map to be part of the purpose, expect it to be last.
                .takeWhile { it.text() != "Kurser i ämnet" }
                .map {
                    val type = when (it.tagName()) {
                        "p" -> PurposeType.SECTION
                        "li" -> PurposeType.BULLET
                        else -> PurposeType.HEADING
                    }
                    Purpose(it.text(), type)
                }
    }
}

