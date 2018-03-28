package org.edtech.curriculum.internal

import org.edtech.curriculum.Purpose
import org.edtech.curriculum.PurposeType
import org.edtech.curriculum.Subject
import org.edtech.curriculum.SubjectHtml
import org.jsoup.Jsoup

class SubjectParser {
    fun getSubject(subjectData: SubjectHtml): Subject {
        return Subject(
                subjectData.name,
                subjectData.description.removePrefix("<p>").removeSuffix("</p>"),
                subjectData.code,
                subjectData.skolfsId,
                toPurposes(subjectData.purposes),
                subjectData.courses.map { CourseParser(it).getCourse() }
        )
    }

    /**
     * Convert the Purpose html to Entities depending on tag type
     */
    private fun toPurposes(html: String): List<Purpose> {
        return Jsoup.parse("<root>$html</root>").select("root > p, li, root > h1, root > h2, root > h3, root > h4, root > h5, root > h6")
                .filter { it.text().isNotEmpty() }
                // Don't want skolverkets content map to be part of the purpose,
                // expect it to be last text to extract.
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