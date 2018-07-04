package org.edtech.curriculum.internal

import com.fasterxml.jackson.databind.ObjectMapper
import org.edtech.curriculum.*
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test
import java.io.File


class KnowledgeRequirementParserTest {

    private val hasMissingRequirementsFromSkolverket = setOf("BYPRIT0", "RINRID02", "SPEIDT0", "TESPRO01", "TEYPRO01", "HAVFIN05S")
    private val coursesWithSwitchedLines = setOf("SVESVE01")
    private val dataDir = File("./src/test/resources/opendata/")
    private val validDataDir = File("./src/test/resources/valid/")

    @Test
    fun testAgainstJsonFilesGR() {
        testAgainstJsonFiles(SchoolType.GR)
    }
    @Test
    fun testAgainstJsonFilesGRS() {
        testAgainstJsonFiles(SchoolType.GRS)
    }
    @Test
    fun testAgainstJsonFilesGY() {
        testAgainstJsonFiles(SchoolType.GY)
    }
    @Test
    fun testAgainstJsonFilesGYS() {
        testAgainstJsonFiles(SchoolType.GYS)
    }
    @Test
    fun testAgainstJsonFilesVUXGR() {
        testAgainstJsonFiles(SchoolType.VUXGR)
    }
/*    @Test
    fun testAgainstJsonFilesSFI() {
        testAgainstJsonFiles(org.edtech.curriculum.SchoolType.SFI)
    }
*/

    private fun testAgainstJsonFiles(schoolType: SchoolType) {
        val mapper = ObjectMapper()
        val subjectMap: MutableMap<String, Subject> = HashMap()

        File("$validDataDir/").listFiles().forEach { versionDir ->
            for (subject in Curriculum(schoolType, dataDir.resolve(versionDir.name)).getSubjects()) {
                subjectMap[subject.code] = subject
            }
            val subjectDir = versionDir.resolve(schoolType.name)
            if (!subjectDir.isDirectory) fail("${subjectDir.absolutePath} is not a directory")

            subjectDir.listFiles()
                .filter { it.name.endsWith(".json") }
                .forEach { file ->
                    val parsedSubject = subjectMap[file.nameWithoutExtension]
                    if (parsedSubject == null) {
                        fail("No subject ${file.nameWithoutExtension} for file ${file.absolutePath}")
                    } else {
                        val expected = file.readText()
                        val actual = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedSubject)
                        assertEquals("Difference for subject ${versionDir.name} - ${schoolType.name}/${file.nameWithoutExtension}", expected, actual)
                    }
                }
        }
    }

    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGR() {
        matchParsedKnowledgeRequirementTextWithOriginal(SchoolType.GR)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGRS() {
        matchParsedKnowledgeRequirementTextWithOriginal(SchoolType.GRS)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGRSAM() {
        matchParsedKnowledgeRequirementTextWithOriginal(SchoolType.GRSAM)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGY() {
        matchParsedKnowledgeRequirementTextWithOriginal(SchoolType.GY)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGYS() {
        matchParsedKnowledgeRequirementTextWithOriginal(SchoolType.GYS)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalVUXGR() {
        matchParsedKnowledgeRequirementTextWithOriginal(SchoolType.VUXGR)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalSFI() {
        matchParsedKnowledgeRequirementTextWithOriginal(SchoolType.SFI)
    }

    private fun matchParsedKnowledgeRequirementTextWithOriginal(schoolType: SchoolType) {
        dataDir.listFiles().forEach { versionDir ->
            Curriculum(schoolType, versionDir).subjectHtml.forEach { subject ->
                for (course in subject.courses) {
                    if (!coursesWithSwitchedLines.contains(course.code)) {
                        // Get the fully parsed course
                        val combined: MutableMap<GradeStep, StringBuilder> = HashMap()
                        val knowledgeRequirements = KnowledgeRequirementConverter()
                                .getKnowledgeRequirements(course.knowledgeRequirement)
                        for (knp in knowledgeRequirements) {
                            for (kn in knp.knowledgeRequirements) {
                                for ((g, s) in kn.knowledgeRequirementChoice) {
                                    if (combined.containsKey(g)) {
                                        combined[g]?.append(" ")?.append(s)
                                    } else {
                                        combined[g] = StringBuilder(s)
                                    }
                                }
                            }
                        }

                        for ((gradeStep, text) in combined) {
                            val textExpected = Jsoup.parse(fixCurriculumErrors(course.knowledgeRequirement.getOrDefault(gradeStep, "")))
                                    .select("p")
                                    .text()
                                    .trim()
                                    .replace("  ", " ")
                                    .replace(Regex("\\.([A-zåäö])"), ". \$1")
                            val textActual = Jsoup.parse(text.toString()).text().trim()
                            assertEquals("course: ${subject.name}/${course.name} GradeStep: ${gradeStep.name}", textExpected, textActual)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun noEmptyKnowledgeRequirementChoicesGR() {
        testSubjects(SchoolType.GR)
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesGRS() {
        testSubjects(SchoolType.GRS)
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesGY() {
        testSubjects(SchoolType.GY)
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesGYS() {
        testSubjects(SchoolType.GYS)
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesVUXGR() {
        testSubjects(SchoolType.VUXGR)
    }

    /**
     * Gradelevel 9 has requirements
     * Gradelevel 6 has requirements except in GRS
     * No gradelevel => has requirements
     */
    private fun hasRequirements(yearGroup: YearGroup?, schoolType: SchoolType): Boolean {
        return (yearGroup == null || yearGroup.end == 6 && schoolType != SchoolType.GRS || yearGroup.end == 9)
    }

    private fun testSubjects(schoolType: SchoolType) {
        dataDir.listFiles().forEach { versionDir ->
            Curriculum(schoolType, versionDir).getSubjects()
                    .forEach { subject ->
                        for (course in subject.courses) {
                            // Get the fully parsed course
                            if ( hasRequirements(course.year, schoolType) ) {
                                assertNotEquals("Knowledge Requirements is empty in  ${subject.name}/${course.name}", 0, course.knowledgeRequirementParagraphs.size)
                            }
                            // Make sure tha all requirements are set, exclude errors from skolverket.
                            if (!hasMissingRequirementsFromSkolverket.contains(course.code)) {
                                course.knowledgeRequirementParagraphs.forEach {
                                    it.knowledgeRequirements.forEach {
                                        val gradeSteps = it.knowledgeRequirementChoice
                                        if (!gradeSteps.keys.containsAll(setOf(GradeStep.A, GradeStep.C, GradeStep.E)) &&
                                                !gradeSteps.keys.contains(GradeStep.G)) {
                                            fail("Knowledge Requirement Choices should be either E,C,A or G failed for: ${subject.name}/${course.name}")
                                        }
                                        gradeSteps.forEach { gradeStep ->
                                            if (gradeStep.value.isBlank())
                                                fail("Found empty knowledge requirement critera in ${subject.name}/${course.name} [${course.code}]")
                                        }
                                    }
                                }
                            }

                        }
                    }
        }
    }
}
