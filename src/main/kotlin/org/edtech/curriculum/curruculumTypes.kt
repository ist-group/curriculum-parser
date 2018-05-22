package org.edtech.curriculum

enum class PurposeType {
    PARAGRAPH, BULLET
}

enum class GradeStep {
    F, E, D, C, B, A, G, X
}

data class Subject(
    val name: String,
    val description: String,
    val code: String,
    val designation: String?,
    val skolfsId: String,
    val purposes: List<Purpose>,
    val courses: List<Course>
)

data class Purpose(
    val type: PurposeType,
    val heading: String,
    val lines: List<String>
)

data class Course(
        val name: String,
        val description: String,
        val code: String,
        val centralContent: List<CentralContent>,
        val knowledgeRequirementParagraphs: List<KnowledgeRequirementParagraph>,
        val point: Int? = null,
        val year: YearGroup? = null
)

data class YearGroup(
    val start: Int?,
    val end: Int
)

data class CentralContent(
    val heading: String,
    val lines: List<String>
)

data class KnowledgeRequirementParagraph(
    val heading: String,
    val knowledgeRequirements: List<KnowledgeRequirement>
)

data class KnowledgeRequirement(
    val text: String,
    val knowledgeRequirementChoice: Map<GradeStep, String>
)
