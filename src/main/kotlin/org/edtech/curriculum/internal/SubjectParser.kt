package org.edtech.curriculum.internal

import org.edtech.curriculum.SchoolType
import org.edtech.curriculum.Subject
import org.edtech.curriculum.SubjectHtml
import org.edtech.curriculum.SubjectSpecialCase
import java.time.LocalDateTime
import java.time.OffsetDateTime

class SubjectParser(val schoolType: SchoolType) {
    fun getSubject(subjectData: SubjectHtml): List<Subject> {
        return SubjectSpecialCase(subjectData, schoolType)
                .getSubjectsWithAppliedSpecialCases().map { (category, subjectData) ->
                    Subject(
                            subjectData.name,
                            fixDescriptions(subjectData.description),
                            subjectData.version,
                            subjectData.code,
                            subjectData.designation,
                            category,
                            subjectData.skolfsId,
                            toPurposes(convertDashListToList(subjectData.purposes)),
                            subjectData.courses.map { CourseParser(it).getCourse() },
                            stringToDate(subjectData.createdDate),
                            stringToDate(subjectData.modifiedDate),
                            subjectData.typeOfSyllabus,
                            subjectData.typeOfSchooling,
                            subjectData.originatorTypeOfSchooling,
                            subjectData.gradeScale,
                            stringToDate(subjectData.validTo),
                            stringToDate(subjectData.applianceDate))
                }.toList()
    }

    private fun stringToDate(dateString: String?): LocalDateTime? {
        return if (dateString?.isNotEmpty() == true) {
            OffsetDateTime.parse(dateString).toLocalDateTime()
        } else {
            null
        }
    }

}