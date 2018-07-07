package org.edtech.curriculum

import java.time.LocalDateTime

enum class PurposeType {
    PARAGRAPH, BULLET
}

enum class SyllabusType {
    COURSE_SYLLABUS, SUBJECT_AREA_SYLLABUS
}

enum class TypeOfSchooling {
    COMPULSORY_SCHOOL,
    UPPER_SECONDARY_EDUCATION,
    BASIC_ADULT_EDUCATION,
    SPECIAL_SCHOOL_FOR_PUPILS_WITH_IMPAIRED_HEARING,
    EDU_FOR_PUPILS_WITH_LEARNING_DISABILITIES,
    SAMI_SCHOOL,
    SWEDISH_FOR_IMMIGRANTS,
    EDU_FOR_ADULTS_WITH_LEARNING_DISABILITIES
}

enum class GradeStep {
    F, E, D, C, B, A, G, X, BASIC, ADVANCED
}
/*
enum class TypeOfRequirement {
    WITHIN_LANGUAGE_CHOICE,
    WITHIN_STUDENT_CHOICE,
    SIGN_LANGUAGE_FOR_BEGINNERS,
    ROMANI_LANGUAGE_SECOND,
    ROMANI_LANGUAGE_FIRST,
    MEANKIELI_LANGUAGE_SECOND,
    MEANKIELI_LANGUAGE_FIRST,
    JIDDISH_LANGUAGE_SECOND,
    JIDDISH_LANGUAGE_FIRST,
    FIN_LANGUAGE_SECOND,
    FIN_LANGUAGE_FIRST,
    WITHIN_LANGUAGE_CHOICE_CHINESE,
    WITHIN_STUDENT_CHOICE_CHINESE,
    SECOND_LANGUAGE,
    FIRST_LANGUAGE
}
*/

enum class AspectType {
    LISTENING_COMPREHENSION,
    VERBAL_PRODUCTION,
    WRITING_PROFICIENCY,
    READING_COMPREHENSION,
    VERBAL_INTERACTION
}

data class Subject(
        val name: String,
        val description: String,
        val version: Int?,
        val code: String,
        val designation: String?,
        val skolfsId: String,
        val purposes: List<Purpose>,
        val courses: List<Course>,
        val createdDate: LocalDateTime?,
        val modifiedDate: LocalDateTime?,
        val typeOfSyllabus: SyllabusType?,
        val typeOfSchooling: TypeOfSchooling?,
        val gradeScale: String?,
        val validTo: LocalDateTime?,
        val applianceDate: LocalDateTime?
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
        val knowledgeRequirements: List<KnowledgeRequirement>,
        val year: Int? = null
)

data class KnowledgeRequirement(
        val text: String,
        val knowledgeRequirementChoice: Map<GradeStep, String>
)
