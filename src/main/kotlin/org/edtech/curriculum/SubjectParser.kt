package org.edtech.curriculum

import org.edtech.curriculum.internal.CourseParser
import org.edtech.curriculum.internal.HtmlParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
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

    private val openDataDocument = Jsoup.parse(openDataDocumentStream, null, "", Parser.xmlParser())
    private fun extractString(elementName: String): String = openDataDocument.select("subject > $elementName" ).text()
    private fun extractNodes(elementName: String): Elements = openDataDocument.select("subject > $elementName" )

    val name: String = extractString("name")
    val description: String = extractString("description").removePrefix("<p>").removeSuffix("</p>")
    val code: String = extractString("code")
    val skolfsId: String = extractString("skolfsId")
    val purpose: String = extractString("purpose")
    val applianceDate: String = extractString("applianceDate")
    val courses: List<Course> = extractCourses()

    fun getSubject(): Subject {
        val doc = Jsoup.parse(purpose)
        return Subject(name, description, code, skolfsId, HtmlParser().toPurposes(doc))
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

    fun getCourse(code: String): Course? {
        return courses.firstOrNull { it.code == code }
    }

    fun getCourseParser(code: String): CourseParser {
        return CourseParser(getCourseElement(code))
    }

    private fun getCourseElement(code: String): Element {
        val elements = extractNodes("courses")
        return if (!elements.isEmpty()) {
            elements.first { it.select("code").text() == code }
        } else {
            openDataDocument
        }
    }
}

