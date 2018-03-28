package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import org.edtech.curriculum.internal.*
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test
import java.io.File


class KnowledgeRequirementParserTest {

    private val hasMissingRequirementsFromSkolverket = setOf("BYPRIT0", "RINRID02", "SPEIDT0", "TESPRO01", "TEYPRO01")
    private val dataDir = File("./src/test/resources/opendata/")



    @Test
    fun testAgainstJsonFiles() {
        val mapper = ObjectMapper()

        for (dir in File("./src/test/resources/valid").listFiles()) {
            val syllabusType = SyllabusType.valueOf(dir.name)
            val subjectMap: MutableMap<String, Subject> = HashMap()

            for (subject in Syllabus(syllabusType, dataDir).getSubjects()) {
                subjectMap[subject.code] = subject
            }

            val subjectDir = File("./src/test/resources/valid/${syllabusType.name}")
            if (!subjectDir.isDirectory) fail("${subjectDir.absolutePath} is not a directory")

            for (file in File("./src/test/resources/valid/${syllabusType.name}").listFiles()) {
                if (!file.name.endsWith(".json")) continue
                val parsedSubject = subjectMap[file.nameWithoutExtension]
                if (parsedSubject == null) {
                    fail("No subject ${file.nameWithoutExtension} for file ${file.absolutePath}")
                } else {
                    val expected = file.readText()
                    val actual = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedSubject)
                    assertEquals("Difference for subject ${syllabusType.name}/${file.nameWithoutExtension}", expected, actual)
                }
            }
        }
    }

    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGR() {
        matchParsedKnowledgeRequirementTextWithOriginal(SyllabusType.GR)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGRS() {
        matchParsedKnowledgeRequirementTextWithOriginal(SyllabusType.GRS)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGRSAM() {
        matchParsedKnowledgeRequirementTextWithOriginal(SyllabusType.GRSAM)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGY() {
        matchParsedKnowledgeRequirementTextWithOriginal(SyllabusType.GY)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalGYS() {
        matchParsedKnowledgeRequirementTextWithOriginal(SyllabusType.GYS)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalVUXGR() {
        matchParsedKnowledgeRequirementTextWithOriginal(SyllabusType.VUXGR)
    }
    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginalSFI() {
        matchParsedKnowledgeRequirementTextWithOriginal(SyllabusType.SFI)
    }

    private fun matchParsedKnowledgeRequirementTextWithOriginal(syllabusType: SyllabusType) {
        for (subject in Syllabus(syllabusType, dataDir).subjectHtml) {
            for (course in subject.courses) {
                // Get the fully parsed course
                val combined: MutableMap<GradeStep, StringBuilder> = HashMap()
                val knowledgeRequirements = KnowledgeRequirementConverter()
                        .getKnowledgeRequirements(course.knowledgeRequirement)
                for (kn in knowledgeRequirements) {
                    for ((g, s) in kn.knowledgeRequirementChoice) {
                        if (combined.containsKey(g)) {
                            combined[g]?.append(" ")?.append(s)
                        } else {
                            combined[g] = StringBuilder(s)
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

    @Test
    fun noEmptyKnowledgeRequirementChoicesGR() {
        testSubjects(Syllabus(SyllabusType.GR, dataDir).getSubjects())
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesGRS() {
        testSubjects(Syllabus(SyllabusType.GRS, dataDir).getSubjects())
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesGY() {
        testSubjects(Syllabus(SyllabusType.GY, dataDir).getSubjects())
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesGYS() {
        testSubjects(Syllabus(SyllabusType.GR, dataDir).getSubjects())
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesVUXGR() {
        testSubjects(Syllabus(SyllabusType.VUXGR, dataDir).getSubjects())
    }
    @Test
    fun noEmptyKnowledgeRequirementChoicesSFI() {
        testSubjects(Syllabus(SyllabusType.SFI, dataDir).getSubjects())
    }

    private fun testSubjects(subjects: List<Subject>) {
        for (subject in subjects) {
            for (course in subject.courses) {
                // Get the fully parsed course
                assertNotEquals("Knowledge Requirements cannot be empty", 0, course.knowledgeRequirement.size)
                // Make sure tha all requirements are set, exclude errors from skolverket.
                if (!hasMissingRequirementsFromSkolverket.contains(course.code)) {
                    course.knowledgeRequirement.forEach {
                        if (!it.knowledgeRequirementChoice.keys.containsAll(setOf(GradeStep.E,GradeStep.C, GradeStep.E)) &&
                                !it.knowledgeRequirementChoice.keys.contains(GradeStep.G)) {
                            fail("Knowledge Requirement Choices should be either E,C,A or G failed for: ${subject.name}/${course.name}")
                        }
                        it.knowledgeRequirementChoice.forEach {
                            if (it.value.isBlank())
                                fail("Found empty knowledge requirement critera in ${subject.name}/${course.name} [${course.code}]")
                        }
                    }
                }

            }
        }
    }
}
