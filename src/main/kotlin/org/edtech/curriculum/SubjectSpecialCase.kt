package org.edtech.curriculum

/**
 * This class contains all logic to alter skolverkets information to fix problems in the structure
 * - Missing designations
 * - Splitting Moderna Språk in to separate subjects
 */
class SubjectSpecialCase(private val subjectHtml: SubjectHtml, private val schoolType: SchoolType) {
    fun getSubjectsWithAppliedSpecialCases(): Map<SubjectCategory?, SubjectHtml> {
        val subjectCategories = getSubjectCategories(subjectHtml)
                .mapValues { (_, subjectHtml) -> addKnowledgeRequirementsToLowerYears(subjectHtml) }

        // Skolverket delivers incorrect data for the GR courses so we need to adjust the year spans
        if (schoolType == SchoolType.GRSPEC) {
            return subjectCategories.map {
                (category, subjectHtml) ->
                if (subjectHtml.code.startsWith("GRGR")) {
                    Pair(category, convertYearSpans(subjectHtml))
                } else {
                    Pair(category, subjectHtml)
                }
            }.toMap()
        }
        return subjectCategories
    }

    /**
     * Some subjects in the lower years do not have any requirements
     * Here we use the E level as the requirement for G level
     * Copy from
     * 6 to 3 (GR, GRS) or
     * 7 to 4 (GRSPEC)
     *
     */
    private fun addKnowledgeRequirementsToLowerYears(subjectHtml: SubjectHtml): SubjectHtml {
        if (schoolType == SchoolType.GR || schoolType == SchoolType.GRS || schoolType == SchoolType.GRSPEC) {
            if (subjectHtml.typeOfSyllabus == SyllabusType.SUBJECT_AREA_SYLLABUS) {
                return subjectHtml.copy(courses = subjectHtml.courses.map { courseHtml ->
                    if (courseHtml.knowledgeRequirementGroups.isEmpty()) {
                        courseHtml.copy(knowledgeRequirementGroups = subjectHtml.courses
                                .firstOrNull { it.knowledgeRequirementGroups.isNotEmpty() }
                                ?.knowledgeRequirementGroups
                                ?: listOf()
                        )
                    } else{
                        courseHtml
                    }
                })
            } else {
                return subjectHtml.copy(courses = subjectHtml.courses.map { courseHtml ->
                    if ((courseHtml.year.endsWith("3") || courseHtml.year.endsWith("4")) && courseHtml.knowledgeRequirementGroups.isEmpty()) {
                        courseHtml.copy(knowledgeRequirementGroups = subjectHtml.courses
                                .firstOrNull { it.year.endsWith("6") || it.year.endsWith("7")}
                                ?.knowledgeRequirementGroups
                                ?.asSequence()
                                ?.map { it.copy(knowledgeRequirements = mapOf(GradeStep.G to it.knowledgeRequirements.getOrDefault(GradeStep.E, ""))) }
                                ?.toList()
                                ?: listOf()
                        )
                    } else{
                        courseHtml
                    }
                })
            }
        }
        return subjectHtml
    }

    /**
     * Change the year spans to 1-4, 5-7, 8-10
     */
    private fun convertYearSpans(subjectHtml: SubjectHtml): SubjectHtml {
        val code = subjectHtml.code.replace(Regex("^GRGR"), "SP")
        return subjectHtml.copy(
                code = code,
                courses = subjectHtml.courses.map {
                    if (it.code.startsWith("GRGR")) {
                        val year = alterYearGroup(it.year)
                        it.copy(
                                year = year,
                                name = "Årskurs $year".trim(),
                                code = "${code}_$year".trim()
                        )
                    } else {
                        it
                    }
                }
        )
    }

    private fun alterYearGroup(year: String): String {
        return year
                .replace("9", "10")
                .replace("7", "8")
                .replace("6", "7")
                .replace("4", "5")
                .replace("3", "4")
    }

    private fun getDesignation(): String = when (subjectHtml.code) {
        //GR
        "GRGRDAN01" -> "DA" // Dans
        //GRS
        "GRSAEST01" -> "ES"  // Estetisk verksamhet
        "GRSAKOM01" -> "KOM" // Kommunikation
        "GRSAMOE01" -> "ML"  // Modersmål utom nationella minoritetsspråk
        "GRSAVAR01" -> "VAA" // Vardagsaktiviteter
        "GRSAVER01" -> "VEU" // Verklighetsuppfattning
        "GRSAMOT01" -> "MOT" // Motorik
        //GRSP
        "GRSPTSP01" -> "TN"  // Teckenspråk för döva och hörselskadade
        "GRSPTSU01" -> "TN"  // Teckenspråk för döva och hörselskadade elever med utvecklingsstörning
        "GRSPSVE01" -> "SV"  // Svenska för döva och hörselskadade
        "GRSPSVU01" -> "SV"  // Svenska för döva och hörselskadade elever med utvecklingsstörning
        "GRSPENG01" -> "EN"  // Engelska för döva och hörselskadade
        "GRSPENU01" -> "EN"  // Engelska för döva och hörselskadade elever med utvecklingsstörning
        "GRSPDRU01" -> "RÖD" // Rörelse och drama för elever med utvecklingsstörning
        "GRSPKOU01" -> "KOM" // Kommunikation för döva och hörselskadade elever med utvecklingsstörning
        else -> subjectHtml.designation
    }

    /**
     * Split and categorize all special case subjects created by skolverket
     * Add designation to those subjects which are missing designations
     *
     * Categories
     * ROMANI_LANGUAGE_SECOND,
     * ROMANI_LANGUAGE_FIRST,
     * MEANKIELI_LANGUAGE_SECOND,
     * MEANKIELI_LANGUAGE_FIRST,
     * JIDDISH_LANGUAGE_SECOND,
     * JIDDISH_LANGUAGE_FIRST,
     * FIN_LANGUAGE_SECOND,
     * FIN_LANGUAGE_FIRST,
     * WITHIN_LANGUAGE_CHOICE_CHINESE,
     * WITHIN_STUDENT_CHOICE_CHINESE,
     */
    private fun getSubjectCategories(subjectHtml: SubjectHtml): Map<SubjectCategory?, SubjectHtml> =
            when (subjectHtml.code) {
                // Moderna språk
                "GRGRMSP01" -> mapOf(
                        SubjectCategory.WITHIN_STUDENT_CHOICE to subjectHtml.copy(
                                designation = "M1",
                                name = "Moderna språk inom ramen för elevens val",
                                code = subjectHtml.code + "-M1",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_STUDENT_CHOICE" }),
                        SubjectCategory.WITHIN_LANGUAGE_CHOICE to subjectHtml.copy(
                                designation = "M2",
                                name = "Moderna språk inom ramen för språkval",
                                code = subjectHtml.code + "-M2",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_LANGUAGE_CHOICE" }),
                        SubjectCategory.WITHIN_STUDENT_CHOICE_CHINESE to subjectHtml.copy(
                                designation = "M1ZHO",
                                name = "Moderna språk inom ramen för elevens val, kinesiska",
                                code = subjectHtml.code + "-M1ZHO",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_STUDENT_CHOICE_CHINESE" }),
                        SubjectCategory.WITHIN_LANGUAGE_CHOICE_CHINESE to subjectHtml.copy(
                                designation = "M2ZHO",
                                name = "Moderna språk inom ramen för språkval, kinesiska",
                                code = subjectHtml.code + "-M2ZHO",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_LANGUAGE_CHOICE_CHINESE" })
                )
                "GRSPMSP01" -> mapOf(
                        SubjectCategory.WITHIN_STUDENT_CHOICE to subjectHtml.copy(
                                designation = "M1",
                                name = "Moderna språk inom ramen för elevens val",
                                code = subjectHtml.code + "-M1",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_STUDENT_CHOICE" }),
                        SubjectCategory.WITHIN_LANGUAGE_CHOICE to subjectHtml.copy(
                                designation = "M2",
                                name = "Moderna språk inom ramen för språkval",
                                code = subjectHtml.code + "-M2",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_LANGUAGE_CHOICE" })
                )
                // Teckenspråk för hörande
                "GRGRTSP01" -> mapOf(
                        SubjectCategory.WITHIN_STUDENT_CHOICE to subjectHtml.copy(
                                designation = "M1TN",
                                name = subjectHtml.name + " inom ramen för elevens val",
                                code = subjectHtml.code + "-M1",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_STUDENT_CHOICE" }),
                        SubjectCategory.WITHIN_LANGUAGE_CHOICE to subjectHtml.copy(
                                designation = "M2TN",
                                name = subjectHtml.name + " inom ramen för språkval",
                                code = subjectHtml.code + "-M2",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_LANGUAGE_CHOICE" })
                )
                // Modersmål - finska som nationellt minoritetsspråk
                "GRGRMOE01", "GRSAMOR01" -> mapOf(
                        SubjectCategory.FIRST_LANGUAGE to subjectHtml.copy(
                                designation = "MLFIN-ML1",
                                name = subjectHtml.name + ", som förstaspråk",
                                code = subjectHtml.code + "-ML1",
                                courses = subjectHtml.courses.filter {
                                    it.category?.endsWith("LANGUAGE_FIRST") ?: false
                                }),
                        SubjectCategory.SECOND_LANGUAGE to subjectHtml.copy(
                                designation = "MLFIN-ML2",
                                name = subjectHtml.name + ", som andraspråk",
                                code = subjectHtml.code + "-ML2",
                                courses = subjectHtml.courses.filter {
                                    it.category?.endsWith("LANGUAGE_SECOND") ?: false
                                })
                )
                // Modersmål - jiddisch som nationellt minoritetsspråk
                "GRGRMOR01", "GRSAMOS01" -> mapOf(
                        SubjectCategory.FIRST_LANGUAGE to subjectHtml.copy(
                                designation = "MLYID-ML1",
                                name = subjectHtml.name + ", som förstaspråk",
                                code = subjectHtml.code + "-ML1",
                                courses = subjectHtml.courses.filter {
                                    it.category?.endsWith("LANGUAGE_FIRST") ?: false
                                }),
                        SubjectCategory.SECOND_LANGUAGE to subjectHtml.copy(
                                designation = "MLYID-ML2",
                                name = subjectHtml.name + ", som andraspråk",
                                code = subjectHtml.code + "-ML2",
                                courses = subjectHtml.courses.filter {
                                    it.category?.endsWith("LANGUAGE_SECOND") ?: false
                                })
                )
                // Modersmål - meänkieli som nationellt minoritetsspråk
                "GRGRMOS01", "GRSAMOM01" -> mapOf(
                        SubjectCategory.FIRST_LANGUAGE to subjectHtml.copy(
                                designation = "MLFIT-ML1",
                                name = subjectHtml.name + ", som förstaspråk",
                                code = subjectHtml.code + "-ML1",
                                courses = subjectHtml.courses.filter {
                                    it.category?.endsWith("LANGUAGE_FIRST") ?: false
                                }),
                        SubjectCategory.SECOND_LANGUAGE to subjectHtml.copy(
                                designation = "MLFIT-ML2",
                                name = subjectHtml.name + ", som andraspråk",
                                code = subjectHtml.code + "-ML2",
                                courses = subjectHtml.courses.filter {
                                    it.category?.endsWith("LANGUAGE_SECOND") ?: false
                                })
                )
                "GRGRMOM01", "GRSAMOA01" -> mapOf(
                        SubjectCategory.FIRST_LANGUAGE to subjectHtml.copy(
                                designation = "MLROM-ML1",
                                name = subjectHtml.name + ", som förstaspråk",
                                code = subjectHtml.code + "-ML1",
                                courses = subjectHtml.courses.filter {
                                    it.category?.endsWith("LANGUAGE_FIRST") ?: false
                                }),
                        SubjectCategory.SECOND_LANGUAGE to subjectHtml.copy(
                                designation = "MLROM-ML2",
                                name = subjectHtml.name + ", som andraspråk",
                                code = subjectHtml.code + "-ML2",
                                courses = subjectHtml.courses.filter {
                                    it.category?.endsWith("LANGUAGE_SECOND") ?: false
                                })
                )

                "GRSPTSP01" -> mapOf(
                        null to subjectHtml.copy(
                                designation = "TN",
                                courses = subjectHtml.courses.filter {
                                    it.category == ""
                                }),
                        SubjectCategory.SIGN_LANGUAGE_FOR_BEGINNERS to subjectHtml.copy(
                                designation = "TN-NY",
                                name = subjectHtml.name + " för nybörjare",
                                code = subjectHtml.code + "-NY",
                                courses = subjectHtml.courses.filter {
                                    it.category == SubjectCategory.SIGN_LANGUAGE_FOR_BEGINNERS.name
                                })
                )

                else -> if (subjectHtml.designation.isNotEmpty())
                    mapOf<SubjectCategory?, SubjectHtml>(null to subjectHtml)
                else
                    mapOf<SubjectCategory?, SubjectHtml>(null to subjectHtml.copy(designation = getDesignation()))
            }
}