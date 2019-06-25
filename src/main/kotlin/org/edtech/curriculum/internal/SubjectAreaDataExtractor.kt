package org.edtech.curriculum.internal

import org.edtech.curriculum.CourseHtml
import org.edtech.curriculum.GradeStep
import org.edtech.curriculum.RequirementGroup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


/**
 * Parses the course data in the format supplied in syllabus.tgz
 *
 * Extracts the information to be used for processing
 *
 *
 * @param subjectDocument to extract information from
 */
class SubjectAreaDataExtractor(private val subjectDocument: Document): CourseDataExtractor {

    override fun getCourseData(): List<CourseHtml> {
        return listOf(CourseHtml(
                subjectDocument.select("name").text(),
                subjectDocument.select("description").text().removePrefix("<p>").removeSuffix("</p>"),
                subjectDocument.select("code").text(),
                "",
                "",
               "",
                convertDashListToList(subjectDocument.select("centralContents").text()),
                this.getKnowledgeRequirements(subjectDocument.select("knowledgeRequirements"))))
    }


    private fun getKnowledgeRequirements(knowledgeRequirementElements: Elements): List<RequirementGroup> {
        return listOf(RequirementGroup(knowledgeRequirementElements.map {
            Pair(
                    GradeStep.valueOf(it.selectFirst("typeOfRequirement").text()),
                    it.selectFirst("text").text()
            )
        }.toMap()))
    }
}

