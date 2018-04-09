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
        val fragment = Jsoup.parseBodyFragment(html)
        // Remove empty paragraphs
        fragment.select("body > p")
                .forEach {
                    if (it.text().trim().isEmpty()) {
                        it.remove()
                    }
                }
        return fragment
            .select("body > p, body > ul, body > h1, body > h2, body > h3, body > h4, body > h5, body > h6, body > i")
            .filter { it.text().isNotEmpty() }

            // Don't want skolverkets content map to be part of the purpose,
            // expect it to be last text to extract.
            .takeWhile { it.text() != "Kurser i ämnet" }
            .mapNotNull {
                when (it.tagName()) {
                    // Paragraph
                    "p"  -> {
                        // Sometimes the heading is marked as <p> so skip the paragraph before bullet lists
                        if (it.nextElementSibling() != null && it.nextElementSibling().tagName() == "ul") {
                            null
                        } else {
                            val heading = if (
                                    it.previousElementSibling() != null &&
                                    it.previousElementSibling().`is`("h1,h2,h3,h4,h5,h6,i")
                            ) {
                                it.previousElementSibling().text()
                            } else {
                                ""
                            }
                            Purpose(PurposeType.PARAGRAPH, heading, it.text().split(Regex("(?<=\\.)")))
                        }
                    }
                    // Bullet list, pick the previous element as heading
                    "ul" -> Purpose(PurposeType.BULLET, it.previousElementSibling().text(), it.children().map { it.text() })
                    // Heading
                    "h1","h2","h3","h4","h5","h6","i" -> {
                        // Only create a "loose" header if there is another header element following this one
                        if (it.nextElementSibling() == null || !it.nextElementSibling().`is`("p, ul")) {
                            // Just a heading, cannot connect it to some content
                            Purpose(PurposeType.PARAGRAPH, it.text(), listOf())
                        } else {
                            null
                        }
                    }
                    else -> null
                }
            }
    }
}