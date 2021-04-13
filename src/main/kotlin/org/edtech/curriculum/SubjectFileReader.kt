package org.edtech.curriculum

import org.edtech.curriculum.internal.IndividualFiledSubjectDataExtractorSubject
import org.edtech.curriculum.internal.SubjectParser
import java.io.File
import java.io.InputStream
import java.io.FileInputStream

/**
 * Extract the information from Skolverket
 */
class SubjectFileReader(
        private val schoolType: SchoolType,
        private val inputFile: InputStream
) {

    fun getSubjectFromFile() : Subject {
        return SubjectParser(schoolType)
                    .getSubject(IndividualFiledSubjectDataExtractorSubject(inputFile, schoolType)
                .getSubjectData())[0]
    }
}