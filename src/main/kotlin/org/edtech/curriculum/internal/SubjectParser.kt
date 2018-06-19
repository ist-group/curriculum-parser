package org.edtech.curriculum.internal

import org.edtech.curriculum.Purpose
import org.edtech.curriculum.PurposeType
import org.edtech.curriculum.Subject
import org.edtech.curriculum.SubjectHtml

class SubjectParser {
    fun getSubject(subjectData: SubjectHtml): Subject {
        return Subject(
                subjectData.name,
                subjectData.description.removePrefix("<p>").removeSuffix("</p>"),
                subjectData.code,
                subjectData.designation,
                subjectData.skolfsId,
                normalizePurposes(toPurposes(subjectData.purposes)),
                subjectData.courses.map { CourseParser(it).getCourse() }
        )
    }

    /**
     * Merge purpose paragraphs with 1. 2. 3. as text instead of an real list.
     */
    internal fun normalizePurposes(originalPurposes: List<Purpose>): List<Purpose> {
        val result = ArrayList<Purpose>()
        var lastBullet: Purpose? = null
        originalPurposes.forEach {
            if (it.type == PurposeType.PARAGRAPH && it.lines.any { s: String -> s.matches(Regex("\\d+\\.")) }) {
                // Create a new Purpose block if we get a new heading
                if (lastBullet != null && it.heading.isNotEmpty()) {
                    result.add(lastBullet!!)
                    lastBullet = null
                }
                // Merge all paragraph lines under the same pullet block
                lastBullet = if (lastBullet == null) {
                    Purpose(
                            PurposeType.BULLET,
                            it.heading,
                            it.lines.map { s -> s.replace(Regex("\\d+\\."), "").trim() }.filter { it.isNotEmpty() })
                } else {
                    Purpose(
                            PurposeType.BULLET,
                            lastBullet!!.heading,
                            lastBullet!!.lines + it.lines.map { s -> s.replace(Regex("\\d+\\."), "").trim() }.filter { it.isNotEmpty() })
                }
            } else {
                if (lastBullet != null) {
                    result.add(lastBullet!!)
                    lastBullet = null
                }
                result.add(it)
            }
        }
        if (lastBullet != null) {
            result.add(lastBullet!!)
        }

        return result
    }
}