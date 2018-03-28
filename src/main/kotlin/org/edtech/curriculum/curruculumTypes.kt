package org.edtech.curriculum

enum class PurposeType {
    SECTION, HEADING, BULLET
}
enum class CentralContentType {
    HEADING, BULLET
}

enum class GradeStep {
    F, E, D, C, B, A, G, X
}

data class Subject(
    val name: String,
    val description: String,
    val code: String,
    val skolfsId: String,
    val purposes: List<Purpose>,
    val courses: List<Course>
)

data class Purpose(
    val content: String,
    val type: PurposeType
)

data class Course(
    val name: String,
    val description: String,
    val code: String,
    val centralContent: List<CentralContent>,
    val knowledgeRequirement: List<KnowledgeRequirement>,
    val point: Int? = null,
    val year: YearGroup? = null
)

data class YearGroup(
    val start: Int?,
    val end: Int)

data class CentralContent(
    val content: String,
    val type: CentralContentType
)

data class KnowledgeRequirement(
    val text: String,
    val no: Int,
    val paragraphNo: Int,
    val knowledgeRequirementChoice: Map<GradeStep, String>
)
