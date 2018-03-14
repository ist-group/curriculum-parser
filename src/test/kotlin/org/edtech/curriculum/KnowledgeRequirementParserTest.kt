package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import org.edtech.curriculum.internal.CourseParser
import org.edtech.curriculum.internal.fixCurriculumErrors
import org.edtech.curriculum.internal.getTextWithoutBoldWords
import org.edtech.curriculum.internal.textMatches
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert.*
import org.junit.Test
import java.io.File


class KnowledgeRequirementParserTest {

    private val hasMissingRequirementsFromSkolverket = setOf("BYPRIT0", "RINRID02", "SPEIDT0", "TESPRO01", "TEYPRO01")

    @Test
    fun testTextMatches() {
        assertTrue(textMatches("Eleven förhåller sig konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån",
                               "Eleven förhåller sig konstnärligt till rörelsevokabulär och varierar, och utvecklar rörelseuttryck efter.")
        )
        assertTrue(textMatches("Eleven planerar och organiserar med handledare matlagning.",
                               "Eleven planerar och organiserar matlagning.")
        )
        assertTrue(textMatches("Dessutom beskriver eleven <strong>översiktligt <strong>både enkla och någon mer komplex </strong></strong>språklig struktur i bekanta och mindre bekanta språk samt drar <strong>enkla </strong>slutsatser även i förhållande till sitt modersmål.",
                               "Dessutom beskriver eleven <strong>utförligt både enkla och några mer komplexa </strong>språkliga strukturer i bekanta och mindre bekanta språk samt drar <strong>välgrundade </strong>slutsatser även i förhållande till sitt modersmål.")
        )
        assertFalse(textMatches(" Vid arbete i <strong>bekanta</strong> situationer använder eleven <strong>med säkerhet</strong> instruktioner och utrustningsbeskrivningar.",
                " I arbetet använder eleven i <strong>nya</strong> situationer <strong>med säkerhet </strong>instruktioner och utrustningsbeskrivningar")
        )
        assertFalse(textMatches(
                "Eleven samarbetar <strong>med viss säkerhet </strong>med andra dansare och medverkande i konstnärliga gestaltande processer.",
                "Eleven<strong> ger också förslag på hur både process och resultat kan förbättras</strong>"))
    }

    @Test
    fun testGetTextWithoutBoldWords() {
        assertEquals("Eleven förhåller sig konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån ",
                getTextWithoutBoldWords("Eleven förhåller sig <strong>med viss säkerhet </strong>konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån <strong>instruktioner</strong>")
        )
        assertEquals("Eleven förhåller sig konstnärligt till rörelsevokabulär och varierar, och utvecklar rörelseuttryck efter .",
                getTextWithoutBoldWords("Eleven förhåller sig <strong>med god säkerhet </strong>konstnärligt till rörelsevokabulär och varierar, <strong>undersöker </strong>och utvecklar<strong> konsekvent </strong>rörelseuttryck efter <strong>olika krav</strong>.")
        )
    }

    @Test
    fun testAgainstJsonFiles() {
        val mapper = ObjectMapper()

        for (dir in File("./src/test/resources").listFiles()) {
            val skolverketFile = SkolverketFile.valueOf(dir.name)
            val subjectMap: MutableMap<String, Subject> = HashMap()

            for (subjectName in skolverketFile.subjectNames()) {
                val subjectParser = skolverketFile.openSubject(subjectName)
                subjectMap[subjectName] = subjectParser.getSubject()
            }

            val subjectDir = File("./src/test/resources/${skolverketFile.name}")
            if (!subjectDir.isDirectory) fail("${subjectDir.absolutePath} is not a directory")

            for (file in File("./src/test/resources/${skolverketFile.name}").listFiles()) {
                if (!file.name.endsWith(".json")) continue
                val subjectName = file.name.split(".").first()
                val parsedSubject = subjectMap[subjectName]
                if (parsedSubject == null) {
                    fail("No subject $subjectName for file ${file.absolutePath}")
                } else {
                    val expected = file.readText()
                    val actual = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedSubject)
                    assertEquals("Difference for subject $subjectName", expected, actual)
                }
            }
        }
    }

    @Test
    fun matchParsedKnowledgeRequirementTextWithOriginal() {
         val skolverketFile = SkolverketFile.GY
         for (subjectName in skolverketFile.subjectNames()) {
             val subjectParser = skolverketFile.openSubject(subjectName)
             val subject = subjectParser.getSubject()
             for (course in subject.courses) {
                 // Get the fully parsed course
                 val combined: MutableMap<GradeStep, StringBuilder> = HashMap()
                 for(kn in course.knowledgeRequirement?: listOf()) {
                     for ( (g,s) in kn.knowledgeRequirementChoice) {
                         if (combined.containsKey(g)) {
                             combined[g]?.append(" ")?.append(s)
                         } else {
                             combined[g] = StringBuilder(s)
                         }
                     }
                 }
                 val cp = getCourseParser(subjectParser.openDataDocument, course.code)
                 for( (gradeStep, text) in combined) {
                     val textExpected = Jsoup.parse(fixCurriculumErrors(cp.extractKnowledgeRequirementForGradeStep(gradeStep)))
                             .select("p")
                             .text()
                             .trim()
                             .replace("  ", " ")
                             .replace(Regex("\\.([A-zåäö])"), ". \$1")
                     val textActual =  Jsoup.parse(text.toString()).text().trim()
                     assertEquals("course: ${subject.name}/${course.name} GradeStep: ${gradeStep.name}", textExpected, textActual)
                 }
             }
         }
    }

    @Test
    fun noEmptyKnowledgeRequirementChoices() {
         val skolverketFile = SkolverketFile.GY
         for (subjectName in skolverketFile.subjectNames()) {
             val subjectParser = skolverketFile.openSubject(subjectName)
             val subject = subjectParser.getSubject()
             for (course in subject.courses) {
                 // Get the fully parsed course
                 assertNotEquals("Knowledge Requirements cannot be empty", 0, course.knowledgeRequirement?.size ?: 0)
                 // Make sure tha all requirements are set, exclude errors from skolverket.
                 if (!hasMissingRequirementsFromSkolverket.contains(course.code)) {
                     course.knowledgeRequirement?.forEach {
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

    private fun getCourseParser(doc: Document, code: String): CourseParser {
        val elements = doc.select("subject > courses" )
        val element = if (!elements.isEmpty()) {
            elements.first { it.select("code").text() == code }
        } else {
            doc
        }
        return CourseParser(element)
    }
}
