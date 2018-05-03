package org.edtech.curriculum.internal

import org.edtech.curriculum.SubjectHtml

interface SubjectDataExtractor {
    fun getSubjectData(): List<SubjectHtml>
}