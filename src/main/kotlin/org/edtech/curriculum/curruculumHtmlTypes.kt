package org.edtech.curriculum

data class SubjectHtml(
        val name: String,
        val description: String,
        val version: Int?,
        val code: String,
        val designation: String,
        val skolfsId: String,
        val purposes: String,
        val courses: List<CourseHtml>,
        val createdDate: String?,
        val modifiedDate: String?,
        val typeOfSyllabus: SyllabusType?,
        val typeOfSchooling: TypeOfSchooling?,
        val originatorTypeOfSchooling: TypeOfSchooling?,
        val gradeScale: String?,
        val validTo: String?,
        val applianceDate: String?
)

data class CourseHtml(
        val name: String,
        val description: String,
        val code: String,
        val year: String,
        val point: String,
        val centralContent: String,
        val knowledgeRequirementGroups: List<RequirementGroup>
)

data class RequirementGroup(
        val knowledgeRequirements: Map<GradeStep, String>,
        val year:  Int? = null
)