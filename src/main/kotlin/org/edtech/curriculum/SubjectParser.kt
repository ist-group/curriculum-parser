package org.edtech.curriculum

import org.edtech.curriculum.internal.CourseParser
import org.edtech.curriculum.internal.HtmlParser
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.io.File
import java.io.FileInputStream

/**
 * Parses the open data XML supplied by skolverket
 *
 * The parser takes an XML file as supplied by http://opendata.skolverket.se/data/syllabus.tgz
 * Only files from subject/<subject-name>.xml are supported.
 *
 * The returned data objects are tagged versions of the unstructured html content in the XML file
 * The knowledge requirements are mapped per line in e-level so that they can be used as a matrix.
 *
 * @param openDataDocumentFile a file from the open data XML as supplied by skolverket
 */
class SubjectParser(openDataDocumentFile: File) {

    private val openDataDocument = Jsoup.parse(FileInputStream(openDataDocumentFile), null, "", Parser.xmlParser())
    private fun extractString(elementName: String): String = openDataDocument.select("subject > $elementName" ).text()
    private fun extractNodes(elementName: String): Elements = openDataDocument.select("subject > $elementName" )

    val name: String = extractString("name")
    val description: String = extractString("description").removePrefix("<p>").removeSuffix("</p>")
    val code: String = extractString("code")
    val purpose: String = extractString("purpose")
    val applianceDate: String = extractString("applianceDate")

    fun getSubject(): Subject {
        val doc = Jsoup.parse(purpose)
        return Subject(name, description, code, HtmlParser().toPurposes(doc))
    }

    fun getCourses(): List<Course>? {
        // Get the list of courses and return as CoursePOJOs
        return extractNodes("courses")
            .map { CourseParser(it).getCourse() }
            .toList()
    }

    fun getCourse(code: String): Course {
        return getCourseParser(code).getCourse()
    }
    fun getCourseParser(code: String): CourseParser {
        // Get the list of courses and return as CoursePOJOs
        val element = extractNodes("courses")
                .first { it.select("code").text() == code }
        return CourseParser(element)
    }

}

