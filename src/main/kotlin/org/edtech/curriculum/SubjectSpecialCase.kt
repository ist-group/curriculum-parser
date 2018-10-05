package org.edtech.curriculum

/**
 * This class contains all logic to alter skolverkets information to fix problems in the structure
 * - Missing designations
 * - Splitting Moderna Språk in to separate subjects
 */
class SubjectSpecialCase(private val subjectHtml: SubjectHtml) {
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
    fun getSubjectCategories(): Map<SubjectCategory?, SubjectHtml> =
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
                                designation = "TN1",
                                name = subjectHtml.name + " inom ramen för elevens val",
                                code = subjectHtml.code + "1",
                                courses = subjectHtml.courses.filter { it.category == "WITHIN_STUDENT_CHOICE" }),
                        SubjectCategory.WITHIN_LANGUAGE_CHOICE to subjectHtml.copy(
                                designation = "TN2",
                                name = subjectHtml.name + " inom ramen för språkval",
                                code = subjectHtml.code + "2",
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

                else -> if (subjectHtml.designation.isNotEmpty())
                    mapOf<SubjectCategory?, SubjectHtml>(null to subjectHtml)
                else
                    mapOf<SubjectCategory?, SubjectHtml>(null to subjectHtml.copy(designation = getDesignation()))
            }
}