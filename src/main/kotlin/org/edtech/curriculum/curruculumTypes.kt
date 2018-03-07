package org.edtech.curriculum

enum class PurposeType {
    SECTION, HEADING, BULLET
}
enum class CentralContentType {
    HEADING, BULLET
}

enum class GradeStep {
    E, C, A, G, X
}

data class Subject(val name: String,
                   val description: String,
                   val code: String,
                   val skolfsId: String,
                   val purposes: List<Purpose>
)

data class Purpose(val content: String,
                   val lineNo: Int,
                   val type: PurposeType
)

data class Course(val name: String,
                  val description: String,
                  val code: String,
                  val point: Int,
                  val centralContent: List<CentralContent>? = null,
                  val knowledgeRequirement: List<KnowledgeRequirement>? = null
)

data class CentralContent(val content: String,
                          val lineNo: Int,
                          val type: CentralContentType
)

data class KnowledgeRequirement(val text: String,
                                val no: Int,
                                val paragraphNo: Int,
                                val knowledgeRequirementChoice: MutableMap<GradeStep, String>
)
