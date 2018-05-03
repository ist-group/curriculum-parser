package org.edtech.curriculum.internal

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
                toPurposes(subjectData.purposes),
                subjectData.courses.map { CourseParser(it).getCourse() }
        )
    }
}