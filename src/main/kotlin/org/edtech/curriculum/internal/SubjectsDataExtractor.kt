package org.edtech.curriculum.internal

import org.edtech.curriculum.SubjectHtml

interface SubjectsDataExtractor {
    fun getSubjectData(): List<SubjectHtml>
}